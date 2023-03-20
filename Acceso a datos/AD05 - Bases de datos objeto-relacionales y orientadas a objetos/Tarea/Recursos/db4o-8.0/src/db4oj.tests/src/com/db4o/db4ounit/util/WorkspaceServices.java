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
package com.db4o.db4ounit.util;

import java.io.*;
import java.net.*;

import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

/**
 * @sharpen.ignore
 */
public class WorkspaceServices {
	
	public static String workspacePath(String fname) {
		return Path4.combine(workspaceRoot(), fname);
	}
	
	public static String workspaceTestFilePath(String fname) {
		return Path4.combine(WorkspaceLocations.getTestFolder(), fname);
	}
	
	/**
	 * @sharpen.property
	 */
	public static String workspaceRoot() {
       String property = System.getProperty("dir.workspace");
        if(property != null){
            return property;
        }
		return findFolderWithChild(pathToClass(WorkspaceServices.class), "db4oj.tests");
	}
	
	static String pathToClass(Class clazz) {
		/*
		 * FIXME: This will fail for paths containing special chars (e.g. spaces).
		 * The obvious workarounds (going through URI or URLDecoder) are not available on JDK<1.2 / are not .NET compatible.
		 */ 
		final URL resource = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class");
		return new File(resource.getFile()).getParent();
	}
	
	static String findFolderWithChild(String baseFolder, String folderChild) {
		
		File test = new File(baseFolder, folderChild);		
		if (test.exists()) return test.getParent(); 
		
		if (getParentFile(test) == null) return null;
		
		// we should test against root folder... :)		
		return findFolderWithChild(getParentFile(test).getParent(), folderChild);
	}
	
	private static String findProperty(String fname, String property) {
		FileReader fileReader = null; 
		try {
			fileReader = new FileReader(new File(fname));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fileReader);
		while (true){
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(line == null){
				return null;
			}
			if (line.startsWith(property))
			{
				return line.substring(property.length() + 1);
			}
		}
	}
	
	public static String readProperty(String fname, String property) 
	{
		return readProperty(fname, property, false);
	}

	public static String readProperty(String fname, String property, boolean lenient)
	{
		String value = findProperty(fname, property);
		if (value != null) return value;
		if (lenient) return null;
		throw new IllegalArgumentException("property '" + property + "' not found in '" + fname + "'");
	}
	
	private static File getParentFile(File file){
        String path = file.getParent();
        if (path == null){
            return null;
        }
        return new File(path);
	}

	public static File configurableWorkspacePath(String configurableProperty, String defaultWorkspacePath) {
		final String path = System.getProperty(configurableProperty, workspacePath(defaultWorkspacePath));
		final File file = new File(IOServices.safeCanonicalPath(path));
		Assert.isTrue(file.exists(), path); 
		return file;
	}
	
	public static String machinePropertiesPath(){
		return propertiesPath("machine.properties");
	}
	
	public static String antPropertiesPath(){
		return propertiesPath("ant.properties");
	}

	private static String propertiesPath(String fileName) {
		String path = workspacePath("db4obuild/" + fileName);
		Assert.isTrue(File4.exists(path));
		return path;
	}
	
	public static String readAntProperty(String property, boolean lenient)
	{
		return readProperty(antPropertiesPath(), property, lenient);
	}
	
	
	public static String readMachineProperty(String property, boolean lenient)
	{
		return readProperty(machinePropertiesPath(), property, lenient);
	}

	
	public static String readMachineProperty(String property)
	{
		return readProperty(machinePropertiesPath(), property);
	}
	
	public static String readMachinePathProperty(String property)
	{
		String path = readMachineProperty(property);
		assertFileExists(path);
		return path;
	}
	
	private static void assertFileExists(String path){
		Assert.isTrue(File4.exists(path), "File '" + path + "' could not be found ");
	}
	
	public static String javacPath()
	{
		String filePropertyName = "file.compiler.jdk1.5";
		String path = readMachineProperty(filePropertyName, true);
		if(path != null){
			return path;
		}
		path = readAntProperty(filePropertyName, true);
		if(path != null){
			return path;
		}
		return "javac";
	}

}
