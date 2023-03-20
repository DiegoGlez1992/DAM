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
package com.db4o.instrumentation.ant;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.types.resources.*;

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.file.*;
import com.db4o.instrumentation.main.*;

/**
 * Ant task for build time class file instrumentation.
 * 
 * @see BloatClassEdit
 */
public class Db4oFileEnhancerAntTask extends Task {
	private final List<FileSet> _sources = new ArrayList<FileSet>();
	private File _targetDir;
	private final List<Path> _classPath = new ArrayList<Path>();
	private final List<AntClassEditFactory> _editFactories = new ArrayList<AntClassEditFactory>();
	private final List<FileSet> _jars = new ArrayList<FileSet>();
	private File _jarTargetDir;
	private boolean _verbose = false;

	public void add(AntClassEditFactory editFactory) {
		_editFactories.add(editFactory);
	}
	
	public void addSources(FileSet fileSet) {
		_sources.add(fileSet);
	}

	public void addJars(FileSet fileSet) {
		_jars.add(fileSet);
	}

	public void setClassTargetDir(File targetDir) {
		_targetDir = targetDir;
	}

	public void setJarTargetdir(File targetDir) {
		_jarTargetDir=targetDir;
	}

	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}
	
	public void addClasspath(Path path) {
		_classPath.add(path);
	}

	public void execute() {
		try {
			FileSet[] sourceArr = _sources.toArray(new FileSet[_sources.size()]);
			AntFileSetPathRoot root = new AntFileSetPathRoot(sourceArr);
			ClassFilter filter = collectClassFilters(root);
			BloatClassEdit clazzEdit = collectClassEdits(filter);
			final String[] classPath = collectClassPath();

			enhanceClassFiles(root, clazzEdit, classPath);
			enhanceJars(clazzEdit, classPath);
		} catch (Exception exc) {
			throw new BuildException(exc);
		}
	}

	private static interface FileResourceBlock {
		void process(FileResource resource) throws Exception;
	}
	
	private String[] collectClassPath() throws Exception {
		final List<String> paths=new ArrayList<String>();
		for (Path path : _classPath) {
			String[] curPaths=path.list();
			for (String curPath : curPaths) {
				paths.add(curPath);
			}
		}
		forEachResource(_jars, new FileResourceBlock() {
			public void process(FileResource resource) throws Exception {
				paths.add(resource.getFile().getCanonicalPath());
			}
		});
		for (FileSet fileSet : _sources) {
			paths.add(fileSet.getDir().getCanonicalPath());
		}
		return paths.toArray(new String[paths.size()]);
	}

	private void enhanceClassFiles(AntFileSetPathRoot root,
			BloatClassEdit clazzEdit, final String[] classPath)
			throws Exception {
		logClassFiles(root);
		new Db4oFileInstrumentor(clazzEdit).enhance(root, _targetDir, classPath);
	}

	private void enhanceJars(BloatClassEdit clazzEdit, final String[] classPath)
			throws Exception {
		final Db4oJarEnhancer jarEnhancer = new Db4oJarEnhancer(clazzEdit);
		forEachResource(_jars, new FileResourceBlock() {
			public void process(FileResource resource) throws Exception {
				File targetJarFile = new File(_jarTargetDir, resource.getFile().getName());
				verboseLog("Enhancing jar: " + targetJarFile.getAbsolutePath());
				jarEnhancer.enhance(resource.getFile(), targetJarFile, classPath);
			}
		});
	}

	private ClassFilter collectClassFilters(AntFileSetPathRoot root) throws Exception {
		final List<ClassFilter> filters = new ArrayList<ClassFilter>();
		filters.add(root);
		forEachResource(_jars, new FileResourceBlock() {
			public void process(FileResource resource) throws IOException {
				JarFile jarFile = new JarFile(resource.getFile());
				filters.add(new JarFileClassFilter(jarFile));
			}
			
		});

		ClassFilter filter = new CompositeOrClassFilter(filters.toArray(new ClassFilter[filters.size()]));
		return filter;
	}

	@SuppressWarnings("unchecked")
	private void forEachResource(List<FileSet> fileSets, FileResourceBlock collectFiltersBlock) throws Exception {
		for (FileSet fileSet : fileSets) {
			for (Iterator<FileResource> resourceIter = fileSet.iterator(); resourceIter.hasNext();) {
				FileResource fileResource = resourceIter.next();
				collectFiltersBlock.process(fileResource);
			}
		}
	}

	private BloatClassEdit collectClassEdits(ClassFilter classFilter) {
		BloatClassEdit clazzEdit = null;
		switch(_editFactories.size()) {
			case 0:
				clazzEdit = new NullClassEdit();
				break;
			case 1:
				clazzEdit = _editFactories.get(0).createEdit(classFilter);
				break;
			default:
				List<BloatClassEdit> classEdits = new ArrayList<BloatClassEdit>(_editFactories.size());
				for (AntClassEditFactory curFactory : _editFactories) {
					classEdits.add(curFactory.createEdit(classFilter));
				}
				clazzEdit = new CompositeBloatClassEdit(classEdits.toArray(new BloatClassEdit[classEdits.size()]), true);
				
		}
		return clazzEdit;
	}
	
	private void verboseLog(String msg) {
		if(_verbose) {
			log(msg, Project.MSG_INFO);
		}
	}

	private void logClassFiles(AntFileSetPathRoot root) throws IOException {
		verboseLog("Enhancing class files.");
		verboseLog("Target folder: " + _targetDir.getAbsolutePath());
		verboseLog("Root folders:");
		for (String rootStr : root.rootDirs()) {
			verboseLog(new File(rootStr).getAbsolutePath());
		}
		verboseLog("Files:");
		for (Iterator<InstrumentationClassSource> fileSetIter = root.iterator(); fileSetIter.hasNext();) {
			verboseLog(fileSetIter.next().toString());
		}
	}


}
