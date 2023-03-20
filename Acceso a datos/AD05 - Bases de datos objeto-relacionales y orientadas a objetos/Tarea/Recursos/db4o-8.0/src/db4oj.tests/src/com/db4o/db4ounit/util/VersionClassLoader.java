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
import java.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class VersionClassLoader extends URLClassLoader {

    private Map cache = new HashMap();

    private String[] prefixes;

    public VersionClassLoader(URL[] urls, String[] prefixes) {
        super(urls);
        this.prefixes = prefixes;
    }

    protected synchronized Class loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        
        if (cache.containsKey(name)) {
            return (Class) cache.get(name);
        }

        if (!knownPrefix(name)) {
            return super.loadClass(name, resolve);
        }

        String resourceName = name.replace('.', '/') + ".class";
        URL resourceURL = findResource(resourceName);
        if (resourceURL == null) {
            System.out.println("Warning: Cannot find resource " + resourceName);
            return super.loadClass(name, resolve);
        }

        byte[] byteCode = null;
        try {
            byteCode = readBytes(resourceURL);
        } catch (IOException e) {
            e.printStackTrace();
            super.loadClass(name, resolve);
        }
        
        Class clazz = defineClass(name, byteCode, 0, byteCode.length);
        if (resolve) {
            resolveClass(clazz);
        }
        cache.put(name, clazz);
        return clazz;
    }
    
    private byte[] readBytes(URL resURL) throws IOException {
        InputStream in = resURL.openStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = in.read(buf)) >= 0) {
            out.write(buf, 0, bytesRead);
        }
        in.close();
        out.close();
        byte[] full = out.toByteArray();
        return full;
    }

    private boolean knownPrefix(String className) {
        for (int idx = 0; idx < prefixes.length; idx++) {
            if (className.startsWith(prefixes[idx])) {
                return true;
            }
        }
        return false;
    }

}
