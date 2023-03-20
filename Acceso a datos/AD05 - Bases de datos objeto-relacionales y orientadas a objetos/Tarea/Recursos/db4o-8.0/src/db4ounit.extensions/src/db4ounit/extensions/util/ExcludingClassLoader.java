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
package db4ounit.extensions.util;

import java.io.*;
import java.net.*;
import java.util.*;

import com.db4o.foundation.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ExcludingClassLoader extends URLClassLoader {
    
    private static final boolean VERBOSE = false;
    
    private final Map _cache = new HashMap();
    
    private Collection4 _excludedNames;
    private Collection4 _delegatedNames;

    public ExcludingClassLoader(ClassLoader parent, Class... excludedClasses) {
        this(parent, collectNames(excludedClasses), new Collection4());
    }

    public ExcludingClassLoader(ClassLoader parent, Class[] excludedClasses, Class[] delegatedClasses) {
        this(parent, collectNames(excludedClasses), collectNames(delegatedClasses));
    }

    public ExcludingClassLoader(ClassLoader parent,Collection4 excludedNames) {
    	this(parent, excludedNames, new Collection4());
    }

    public ExcludingClassLoader(ClassLoader parent,Collection4 excludedNames, Collection4 delegatedNames) {
        super(new URL[]{}, parent);
        this._excludedNames = excludedNames;
        this._delegatedNames = delegatedNames;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if(_excludedNames.contains(name)) {
            print("EXCLUDED: " + name);
            throw new ClassNotFoundException(name);
        }
        if(_cache.containsKey(name)) {
            print("CACHED: " + name);
            return (Class)_cache.get(name);
        }
        if(mustDelegate(name)) {
            print("NATIVE: " + name);
            return super.loadClass(name, resolve);
        }
        if(_delegatedNames.contains(name)) {
            print("DELEGATED: " + name);
            return super.loadClass(name, resolve);
        }
        Class clazz = findRawClass(name);
        if(resolve) {
            resolveClass(clazz);
        }
        _cache.put(clazz.getName(), clazz);
        print("LOADED: " + name);
        return clazz;
    }
    
    private static Collection4 collectNames(Class[] classes) {
        Collection4 names = new Collection4();
        for (int classIdx = 0; classIdx < classes.length; classIdx++) {
            names.add(classes[classIdx].getName());
        }
        return names;
    }

    private boolean mustDelegate(String name) {
        return isPlatformClassName(name)
                || name.equals(ExcludingClassLoader.class.getName())
                || name.startsWith("db4ounit.")
                ||((name.startsWith("com.db4o.") && name.indexOf("test.")<0 && name.indexOf("com.db4o.db4ounit.")<0 && name.indexOf("samples.")<0));
    }

    private static boolean isPlatformClassName(String name) {
        return name.startsWith("java.") || name.startsWith("javax.")
                || name.startsWith("sun.");
    }

    private Class findRawClass(String className) throws ClassNotFoundException {
        try {
            String resourcePath = className.replace('.','/') + ".class";
            InputStream resourceStream = getResourceAsStream(resourcePath);
            ByteArrayOutputStream rawByteStream = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int bytesread = 0;
            while((bytesread = resourceStream.read(buf)) >= 0) {
                rawByteStream.write(buf, 0, bytesread);
            }
            resourceStream.close();
            byte[] rawBytes = rawByteStream.toByteArray();
            return super.defineClass(className, rawBytes, 0, rawBytes.length);
        } catch (Exception exc) {
            throw new ClassNotFoundException(className, exc);
        }   
    }

    public static void main(String[] args) throws Exception {
        ClassLoader parent=ExcludingClassLoader.class.getClassLoader();
        String excName=ExcludingClassLoader.class.getName();
        Collection4 excluded=new Collection4();
        ClassLoader incLoader=new ExcludingClassLoader(parent, excluded, new Collection4());
        System.out.println(incLoader.loadClass(excName));
        excluded.add(excName);
        try {
            System.out.println(incLoader.loadClass(excName));
        }
        catch(ClassNotFoundException exc) {
            System.out.println("Ok, not found.");
        }
    }
    
    private void print(String msg){
        if(! VERBOSE){
            return;
        }
        System.err.println(msg);
    }
    
    
}
