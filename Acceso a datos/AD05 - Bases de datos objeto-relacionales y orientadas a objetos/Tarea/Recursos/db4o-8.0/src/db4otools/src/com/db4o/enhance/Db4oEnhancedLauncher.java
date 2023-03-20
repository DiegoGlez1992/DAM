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
package com.db4o.enhance;

import java.io.*;
import java.net.*;

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.nativequery.main.*;
import com.db4o.ta.instrumentation.*;



/**
 * Launcher to start applications with 
 */
public class Db4oEnhancedLauncher {
    
    /**
     * launches an application that is to be instrumented for db4o on-the-fly
     * at class loading time.
     * @param path path to the classes (typically the bin directory)
     * @param mainClassName the name of the main class that is to be started. 
     */
    public void launch(String path, String mainClassName) throws Exception{
        URL[] urls = { new File(path).toURL() };
        launch(urls, mainClassName);
    }
    
    /**
     * launches an application that is to be instrumented for db4o on-the-fly
     * at class loading time.
     * @param classPath Array of Classpath URLs.
     * @param mainClassName the name of the main class that is to be started.
     */
    public void launch(URL[] classPath, String mainClassName) throws Exception{
        BloatClassEdit[] edits = { 
            new TranslateNQToSODAEdit() , 
            new InjectTransparentActivationEdit(new AcceptAllClassesFilter()) };
        Db4oInstrumentationLauncher.launch(edits, classPath, mainClassName, new String[]{});
    }

}
