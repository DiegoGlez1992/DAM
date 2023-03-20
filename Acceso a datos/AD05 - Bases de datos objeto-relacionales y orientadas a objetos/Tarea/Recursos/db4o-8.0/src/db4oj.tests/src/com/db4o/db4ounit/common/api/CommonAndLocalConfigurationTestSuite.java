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
package com.db4o.db4ounit.common.api;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.config.encoding.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.internal.config.*;
import com.db4o.internal.ids.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class CommonAndLocalConfigurationTestSuite extends FixtureBasedTestSuite {
	
	public static class BaseConfigurationProviderTestUnit implements TestCase {
		public static final class Item {
			
		}
		
		public void test() {
			final CommonConfigurationProvider config = subject();
			final Config4Impl legacy = Db4oLegacyConfigurationBridge.asLegacy(config);
			
			final CommonConfiguration common = config.common();
			common.activationDepth(42);		
			Assert.areEqual(42, legacy.activationDepth());
			Assert.areEqual(42, common.activationDepth());

			// TODO: assert
			common.add(new ConfigurationItem() {
				public void apply(InternalObjectContainer container) {
				}

				public void prepare(Configuration configuration) {
				}
			});
			
			final TypeAlias alias = new TypeAlias("foo", "bar");
			common.addAlias(alias);
			Assert.areEqual("bar", legacy.resolveAliasStoredName("foo"));
			Assert.areEqual("foo", legacy.resolveAliasRuntimeName("bar"));
			
			common.removeAlias(alias);
			Assert.areEqual("foo", legacy.resolveAliasStoredName("foo"));

			
			common.allowVersionUpdates(false);
			Assert.isFalse(legacy.allowVersionUpdates());
			
			common.automaticShutDown(false);
			Assert.isFalse(legacy.automaticShutDown());
			
			common.bTreeNodeSize(42);
			Assert.areEqual(42, legacy.bTreeNodeSize());
			
			common.callbacks(false);
			Assert.areEqual(CallBackMode.NONE, legacy.callbackMode());
			
			common.callConstructors(false);
			Assert.isTrue(legacy.callConstructors().definiteNo());
			
			common.detectSchemaChanges(false);
			Assert.isFalse(legacy.detectSchemaChanges());
			
			final DiagnosticCollector collector = new DiagnosticCollector();
			common.diagnostic().addListener(collector);
			final Diagnostic diagnostic = dummyDiagnostic();
			legacy.diagnosticProcessor().onDiagnostic(diagnostic);
			collector.verify(diagnostic);

			common.exceptionsOnNotStorable(true);
			Assert.isTrue(legacy.exceptionsOnNotStorable());
			
			common.internStrings(true);
			Assert.isTrue(legacy.internStrings());

			// TODO: assert
			common.markTransient("Foo");
			
			common.messageLevel(3);
			Assert.areEqual(3, legacy.messageLevel());
			
			ObjectClass objectClass = common.objectClass(Item.class);
			objectClass.cascadeOnDelete(true);
			Assert.isTrue(((Config4Class)legacy.objectClass(Item.class)).cascadeOnDelete().definiteYes());
			Assert.isTrue(((Config4Class)common.objectClass(Item.class)).cascadeOnDelete().definiteYes());
			
			common.optimizeNativeQueries(false);
			Assert.isFalse(legacy.optimizeNativeQueries());
			Assert.isFalse(common.optimizeNativeQueries());
			
			common.queries().evaluationMode(QueryEvaluationMode.LAZY);
			Assert.areEqual(QueryEvaluationMode.LAZY, legacy.evaluationMode());
			
			// TODO: test reflectWith()
			
			// TODO: this probably won't sharpen :/
			PrintStream outStream = System.out;
			common.outStream(outStream);
			Assert.areEqual(outStream, legacy.outStream());

			StringEncoding stringEncoding = new StringEncoding() {
				public String decode(byte[] bytes, int start, int length) {
					return null;
				}

				public byte[] encode(String str) {
					return null;
				}			
			};
			common.stringEncoding(stringEncoding);
			Assert.areEqual(stringEncoding, legacy.stringEncoding());
			
			common.testConstructors(false);
			Assert.isFalse(legacy.testConstructors());
			common.testConstructors(true);
			Assert.isTrue(legacy.testConstructors());
			
			common.updateDepth(1024);
			Assert.areEqual(1024, legacy.updateDepth());
			
			common.weakReferences(false);
			Assert.isFalse(legacy.weakReferences());
			
			common.weakReferenceCollectionInterval(1024);
			Assert.areEqual(1024, legacy.weakReferenceCollectionInterval());
			
			// TODO: test registerTypeHandler()
		}

		private DiagnosticBase dummyDiagnostic() {
			return new DiagnosticBase() {
				@Override
				public String problem() {
					return null;
				}

				@Override
				public Object reason() {
					return null;
				}

				@Override
				public String solution() {
					return null;
				}
			};
		}
	}
	
	public static class LocalConfigurationProviderTestUnit implements TestCase {
		public void test() throws Exception {
			if (CommonAndLocalConfigurationTestSuite.<Object>subject() instanceof ClientConfiguration) {
				return;
			}
			
			final FileConfigurationProvider config = subject();
			final FileConfiguration fileConfig = config.file();
			final Config4Impl legacyConfig = Db4oLegacyConfigurationBridge.asLegacy(config);
			
			fileConfig.blockSize(42);
			Assert.areEqual(42, legacyConfig.blockSize());
			
			fileConfig.databaseGrowthSize(42);
			Assert.areEqual(42, legacyConfig.databaseGrowthSize());
			
			fileConfig.disableCommitRecovery();
			Assert.isTrue(legacyConfig.commitRecoveryDisabled());
			
			fileConfig.freespace().discardSmallerThan(8);
			Assert.areEqual(8, legacyConfig.discardFreeSpace());
			
			fileConfig.generateUUIDs(ConfigScope.GLOBALLY);
			Assert.areEqual(ConfigScope.GLOBALLY, legacyConfig.generateUUIDs());

			fileConfig.generateCommitTimestamps(true);
			Assert.isTrue(legacyConfig.generateCommitTimestamps().definiteYes());
			
			Storage storageFactory = new FileStorage();
			fileConfig.storage(storageFactory);
			Assert.areSame(storageFactory, legacyConfig.storage());
			Assert.areSame(storageFactory, fileConfig.storage());
			
			fileConfig.lockDatabaseFile(true);
			Assert.isTrue(legacyConfig.lockFile());
			
			fileConfig.reserveStorageSpace(1024);
			Assert.areEqual(1024, legacyConfig.reservedStorageSpace());
			
			fileConfig.blobPath(Path4.getTempPath());
			Assert.areEqual(Path4.getTempPath(), legacyConfig.blobPath());
			
			fileConfig.readOnly(true);
			Assert.isTrue(legacyConfig.isReadOnly());
			
			CacheConfigurationProvider cacheProvider = subject();
			final CacheConfiguration cache = cacheProvider.cache();
			
			IdSystemConfigurationProvider idSystemConfigurationProvider = subject();
			IdSystemConfiguration idSystemConfiguration = idSystemConfigurationProvider.idSystem();
			Assert.areEqual(StandardIdSystemFactory.DEFAULT, legacyConfig.idSystemType());
			idSystemConfiguration.useStackedBTreeSystem();
			Assert.areEqual(StandardIdSystemFactory.STACKED_BTREE, legacyConfig.idSystemType());
			idSystemConfiguration.usePointerBasedSystem();
			Assert.areEqual(StandardIdSystemFactory.POINTER_BASED, legacyConfig.idSystemType());
		}

		public void testUnspecifiedUpdateDepthIsIllegal() {
			final CommonConfigurationProvider common = subject();
			Assert.expect(IllegalArgumentException.class, new CodeBlock() {
				public void run() throws Throwable {
					common.common().updateDepth(Const4.UNSPECIFIED);
				}
			});
		}
	}
	
	@Override
    public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] { subjects(Db4oEmbedded.newConfiguration(), Db4oClientServer.newClientConfiguration(), Db4oClientServer.newServerConfiguration()) };
    }

	private FixtureProvider subjects(Object... subjects) {
		return new SubjectFixtureProvider(subjects);
    }

	@Override
    public Class[] testUnits() {
		return new Class[] {
			BaseConfigurationProviderTestUnit.class,
			LocalConfigurationProviderTestUnit.class,
		};
    }
	
	public static <T> T subject() {
		return (T) SubjectFixtureProvider.value();
	}

}
