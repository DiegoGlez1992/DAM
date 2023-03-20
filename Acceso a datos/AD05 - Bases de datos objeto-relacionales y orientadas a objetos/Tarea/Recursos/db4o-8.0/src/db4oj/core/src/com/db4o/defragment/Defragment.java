/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.mapping.*;
import com.db4o.io.*;

/**
 * defragments database files.
 * 
 * <br><br>db4o structures storage inside database files as free and occupied slots, very
 * much like a file system - and just like a file system it can be fragmented.<br><br>
 * 
 * The simplest way to defragment a database file:<br><br>
 * 
 * <code>Defragment.defrag("sample.db4o");</code><br><br>
 * 
 * This will move the file to "sample.db4o.backup", then create a defragmented
 * version of this file in the original position, using a temporary file
 * "sample.db4o.mapping". If the backup file already exists, this will throw an
 * exception and no action will be taken.<br><br>
 * 
 * For more detailed configuration of the defragmentation process, provide a
 * DefragmentConfig instance:<br><br>
 * 
 * <code>DefragmentConfig config=new DefragmentConfig("sample.db4o","sample.bap",new BTreeIDMapping("sample.map"));<br>
 *	config.forceBackupDelete(true);<br>
 *	config.storedClassFilter(new AvailableClassFilter());<br>
 * config.db4oConfig(db4oConfig);<br>
 * Defragment.defrag(config);</code><br><br>
 * 
 * This will move the file to "sample.bap", then create a defragmented version
 * of this file in the original position, using a temporary file "sample.map" for BTree mapping.
 * If the backup file already exists, it will be deleted. The defragmentation
 * process will skip all classes that have instances stored within the db4o file,
 * but that are not available on the class path (through the current
 * classloader). Custom db4o configuration options are read from the
 * {@link com.db4o.config.Configuration Configuration} passed as db4oConfig.
 * 
 * <strong>Note:</strong> For some specific, non-default configuration settings like
 * UUID generation, etc., you <strong>must</strong> pass an appropriate db4o configuration,
 * just like you'd use it within your application for normal database operation.
 */
public class Defragment {

	/**
	 * Renames the file at the given original path to a backup file and then
	 * builds a defragmented version of the file in the original place.
	 * 
	 * @param origPath
	 *            The path to the file to be defragmented.
	 * @throws IOException
	 *             if the original file cannot be moved to the backup location
	 */
	public static void defrag(String origPath) throws IOException {
		defrag(new DefragmentConfig(origPath), new NullListener());
	}

	/**
	 * Renames the file at the given original path to the given backup file and
	 * then builds a defragmented version of the file in the original place.
	 * 
	 * @param origPath
	 *            The path to the file to be defragmented.
	 * @param backupPath
	 *            The path to the backup file to be created.
	 * @throws IOException
	 *             if the original file cannot be moved to the backup location
	 */
	public static void defrag(String origPath, String backupPath)
			throws IOException {
		defrag(new DefragmentConfig(origPath, backupPath), new NullListener());
	}

	/**
	 * Renames the file at the configured original path to the configured backup
	 * path and then builds a defragmented version of the file in the original
	 * place.
	 * 
	 * @param config
	 *            The configuration for this defragmentation run.
	 * @throws IOException
	 *             if the original file cannot be moved to the backup location
	 */
	public static void defrag(DefragmentConfig config) throws IOException {
		defrag(config, new NullListener());
	}

	/**
	 * Renames the file at the configured original path to the configured backup
	 * path and then builds a defragmented version of the file in the original
	 * place.
	 * 
	 * @param config
	 *            The configuration for this defragmentation run.
	 * @param listener
	 *            A listener for status notifications during the defragmentation
	 *            process.
	 * @throws IOException
	 *             if the original file cannot be moved to the backup location
	 */
	public static void defrag(DefragmentConfig config, DefragmentListener listener) throws IOException {
		Storage storage = config.db4oConfig().storage();
		ensureFileExists(storage, config.origPath());
		Storage backupStorage = config.backupStorage();
		if (backupStorage.exists(config.backupPath())) {
			if (!config.forceBackupDelete()) {
				throw new IOException("Could not use '" + config.backupPath()
						+ "' as backup path - file exists.");
			}
		}
		// Always delete, because !exists can indicate length == 0
		backupStorage.delete(config.backupPath());
		moveToBackup(config);
		
		if(config.fileNeedsUpgrade()) {
			upgradeFile(config);
		}
		
		DefragmentServicesImpl services = new DefragmentServicesImpl(config, listener);
		try {
			firstPass(services, config);
			services.commitIds();
			secondPass(services, config);
			services.commitIds();
			defragUnindexed(services);
			services.commitIds();
			services.defragIdToTimestampBtree();
			services.replaceClassMetadataRepository();
		} catch (CorruptionException exc) {
			exc.printStackTrace();
		} finally {
			services.close();
		}
	}

	private static void moveToBackup(DefragmentConfig config) throws IOException {
		Storage origStorage = config.db4oConfig().storage();
		if(origStorage == config.backupStorage()) {
			origStorage.rename(config.origPath(), config.backupPath());
			return;
		}
		copyBin(origStorage, config.backupStorage(), config.origPath(), config.backupPath());
		origStorage.delete(config.origPath());
	}

	private static void copyBin(Storage sourceStorage, Storage targetStorage,
			String sourcePath, String targetPath) throws IOException {
		Bin origBin = sourceStorage.open(new BinConfiguration(sourcePath, true, 0, true));
		try {
			Bin backupBin = targetStorage.open(new BinConfiguration(targetPath, true, origBin.length(), false));
			try {
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				int pos = 0;
				while((bytesRead = origBin.read(pos, buffer, buffer.length)) >= 0) {
					backupBin.write(pos, buffer, bytesRead);
					pos += bytesRead;
				}
			}
			finally {
				syncAndClose(backupBin);
			}
		}
		finally {
			syncAndClose(origBin);
		}
	}

	private static void syncAndClose(Bin bin) {
		try {
			bin.sync();
		}
		finally {
			bin.close();
		}
	}
	
	private static void ensureFileExists(Storage storage, String origPath) throws IOException {
		if(!storage.exists(origPath)) {
			throw new IOException("Source database file '" + origPath
					+ "' does not exist.");			
		}
	}

	private static void upgradeFile(DefragmentConfig config) throws IOException {
		copyBin(config.backupStorage(), config.backupStorage(), config.backupPath(), config.tempPath());
		Configuration db4oConfig=(Configuration)((Config4Impl)config.db4oConfig()).deepClone(null);
		db4oConfig.storage(config.backupStorage());
		db4oConfig.allowVersionUpdates(true);
		ObjectContainer db=Db4o.openFile(db4oConfig, config.tempPath());
		db.close();
	}

	private static void defragUnindexed(DefragmentServicesImpl services){
		IdSource unindexedIDs = services.unindexedIDs();
		while (unindexedIDs.hasMoreIds()) {
			final int origID = unindexedIDs.nextId();
			DefragmentContextImpl.processCopy(services, origID, new SlotCopyHandler() {
				public void processCopy(DefragmentContextImpl context){
					ClassMetadata.defragObject(context);
				}
			});
		}
	}

	private static void firstPass(DefragmentServicesImpl context,
			DefragmentConfig config) throws CorruptionException, IOException {
		// System.out.println("FIRST");
		pass(context, config, new FirstPassCommand());
	}

	private static void secondPass(final DefragmentServicesImpl context,
			DefragmentConfig config) throws CorruptionException, IOException {
		// System.out.println("SECOND");
		pass(context, config, new SecondPassCommand(config.objectCommitFrequency()));
	}

	private static void pass(DefragmentServicesImpl context,
			DefragmentConfig config, PassCommand command)
			throws CorruptionException, IOException {
		command.processClassCollection(context);
		StoredClass[] classes = context
				.storedClasses(DefragmentServicesImpl.SOURCEDB);
		for (int classIdx = 0; classIdx < classes.length; classIdx++) {
			ClassMetadata classMetadata = (ClassMetadata) classes[classIdx];
			if (!config.storedClassFilter().accept(classMetadata)) {
				continue;
			}
			processClass(context, classMetadata, command);
			command.flush(context);
			if(config.objectCommitFrequency()>0) {
				context.targetCommit();
			}
		}
		BTree uuidIndex = context.sourceUuidIndex();
		if (uuidIndex != null) {
			command.processBTree(context, uuidIndex);
		}
		command.flush(context);
		context.targetCommit();
	}

	// TODO order of class index/object slot processing is crucial:
	// - object slots before field indices (object slots register addresses for
	// use by string indices)
	// - class index before object slots, otherwise phantom btree entries from
	// deletions appear in the source class index?!?
	// reproducable with SelectiveCascadingDeleteTestCase and ObjectSetTestCase
	// - investigate.
	private static void processClass(final DefragmentServicesImpl context,
			final ClassMetadata curClass, final PassCommand command)
			throws CorruptionException, IOException {
		processClassIndex(context, curClass, command);
		if (!parentHasIndex(curClass)) {
			processObjectsForClass(context, curClass, command);
		}
		processClassAndFieldIndices(context, curClass, command);
	}

	private static boolean parentHasIndex(ClassMetadata curClass) {
		ClassMetadata parentClass = curClass.getAncestor();
		while (parentClass != null) {
			if (parentClass.hasClassIndex()) {
				return true;
			}
			parentClass = parentClass.getAncestor();
		}
		return false;
	}

	private static void processObjectsForClass(
			final DefragmentServicesImpl context, final ClassMetadata curClass,
			final PassCommand command) {
		context.traverseAll(curClass, new Visitor4() {
			public void visit(Object obj) {
				int id = ((Integer) obj).intValue();
				try {
					// FIXME bubble up exceptions
					command.processObjectSlot(context, curClass, id);
				} catch (CorruptionException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void processClassAndFieldIndices(
			final DefragmentServicesImpl context, final ClassMetadata curClass,
			final PassCommand command) throws CorruptionException, IOException {
		int sourceClassIndexID = 0;
		int targetClassIndexID = 0;
		if (curClass.hasClassIndex()) {
			sourceClassIndexID = curClass.index().id();
			targetClassIndexID = context.mappedID(sourceClassIndexID, -1);
		}
		command.processClass(context, curClass, curClass.getID(),
				targetClassIndexID);
	}

	private static void processClassIndex(final DefragmentServicesImpl context,
			final ClassMetadata curClass, final PassCommand command)
			throws CorruptionException, IOException {
		if (curClass.hasClassIndex()) {
			BTreeClassIndexStrategy indexStrategy = (BTreeClassIndexStrategy) curClass
					.index();
			final BTree btree = indexStrategy.btree();
			command.processBTree(context, btree);
		}
	}

	static class NullListener implements DefragmentListener {
		public void notifyDefragmentInfo(DefragmentInfo info) {
		}
	}
}
