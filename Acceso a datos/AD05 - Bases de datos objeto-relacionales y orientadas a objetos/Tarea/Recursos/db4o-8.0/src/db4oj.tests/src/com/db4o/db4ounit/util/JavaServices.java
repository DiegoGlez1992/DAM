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
import java.util.*;

import com.db4o.Db4oVersion;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.Assert;
import db4ounit.extensions.util.IOServices;
import db4ounit.extensions.util.IOServices.ProcessRunner;


/**
 * @sharpen.ignore
 */
public class JavaServices {

    public static String java(String className) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className));
    }
    
    public static String java(String className, String[] args) throws IOException, InterruptedException{
        return IOServices.exec(javaExecutable(), javaRunArguments(className, args, false));
    }

    public static ProcessRunner startJava(String className, String[] args) throws IOException {
        return IOServices.start(javaExecutable(), javaRunArguments(className, args, false));
    }

	public static String javac(String srcFile) throws IOException, InterruptedException
	{
			String[] javacArgs =
				new String[]{
					"-classpath",
					IOServices.joinArgs(
	        				File.pathSeparator,
	        				new String[]{
	        						currentClassPath(),
	        						db4oCoreJarPath(), 
	        						db4oJarPath("-optional"),
	        						db4oJarPath("-cs"),
	        				}, runningOnWindows()),
	        		"-source", "1.3",
	        		"-target", "1.3",
	        		srcFile};

			return IOServices.exec(WorkspaceServices.javacPath(),javacArgs);
	}	
	
    public static String startAndKillJavaProcess(String className, String expectedOutput, long timeout) throws IOException{
        return IOServices.execAndDestroy(javaExecutable(), javaRunArguments(className), expectedOutput, timeout);
    }

    private static String javaExecutable() {
        for (int i = 0; i < vmTypes.length; i++) {
            if(vmTypes[i].identified()){
                return vmTypes[i].executable();
            }
        }
        throw new NotImplementedException("VM " + vmName() + " not known. Please add as JavaVM class to end of JavaServices class.");
    }
    
    private static String[] javaRunArguments(String className) {
    	return javaRunArguments(className, new String[0], true);
    }

    private static String[] javaRunArguments(String className, String[] args, boolean includeDb4oJars) {
    	String[] allArgs = new String[args.length + 3];
    	allArgs[0] = "-classpath";
    	
    	List<String> cp = new ArrayList<String>();
    	cp.add(JavaServices.javaTempPath());
    	cp.add(currentClassPath());
    	if (includeDb4oJars) {
    		cp.add(db4oCoreJarPath());
    		cp.add(db4oJarPath("-optional"));
    		cp.add(db4oJarPath("-cs"));
    	}
    	
    	allArgs[1] = IOServices.joinArgs(
						File.pathSeparator,
						cp.toArray(new String[cp.size()]), 
						runningOnWindows());
        allArgs[2] = className;
        System.arraycopy(args, 0, allArgs, 3, args.length);
        return allArgs;        
    }

    private static String currentClassPath(){
        return property("java.class.path");
    }
    
    static String javaHome(){
        return property("java.home");
    }
    
    static String vmName(){
        return property("java.vm.name");
    }
    
    static String property(String propertyName){
        return System.getProperty(propertyName);
    }
    
    private static final JavaVM[] vmTypes = new JavaVM[]{
        new JavaRumtime(),
        new DalvikRuntime(),
    };
    
    static interface JavaVM {
        boolean identified();
        String executable();
    }
    
    static class JavaRumtime implements JavaVM{
        public String executable() {
            return  Path4.combine(Path4.combine(javaHome(), "bin"), "java");
        }
        public boolean identified() {
            return System.getProperty("java.vm.name").contains("Java");
        }
    }
    
    static class DalvikRuntime implements JavaVM{
        public String executable() {
            return  Path4.combine(Path4.combine(javaHome(), "bin"), "dalvikvm");
        }
        public boolean identified() {
            return "Dalvik".equals(System.getProperty("java.vm.name"));
        }
    }
    
    public static String db4oCoreJarPath()
    {
        return db4oJarPath("-core");
    }

	public static String db4oJarPath(String extension)
	{
		String db4oVersion =  
				Db4oVersion.MAJOR + "." + Db4oVersion.MINOR + "." + 
            Db4oVersion.ITERATION + "." + Db4oVersion.REVISION;
		String distDir = WorkspaceServices.readProperty(WorkspaceServices.machinePropertiesPath(), "dir.dist", true);
		if(distDir == null || distDir.length() == 0)
		{
			distDir = "db4obuild/dist";
		}
		return WorkspaceServices.workspacePath(distDir + "/java/lib/db4o-" + db4oVersion + extension + "-java1.2.jar");
	}
	
	public static String javaTempPath()
	{
		return IOServices.buildTempPath("java");
	}
	
	private static boolean runningOnWindows() {
		String osName = System.getProperty("os.name");
		if(osName == null) {
			return false;
		}
		return osName.indexOf("Win") >= 0;
	}
	
}
