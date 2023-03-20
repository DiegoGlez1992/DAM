/*
 * @(#) Runner.java 1.3 - last change made 09/08/03
 *
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

package sunw.demo.jhdemo;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

/**
 * Convience class for executing an application with parameters from a 
 * jar file. This allows a single application to be invoked from multiple
 * jar files. Two additonal manifest attributes are utilized:
 * <UL>
 * <LI>Run-Class - The actual class to run
 * <LI>Arguments - a set of arguements to pass to the run class
 * </UL>
 * An example is below:
 * <pre>
 * Main-Class: Runner
 * Class-Path: ../../javahelp/lib/jh.jar hsviewer.jar ../lib/classviewer.jar ../hsjar/idehelp.jar ../hsjar/apidoc.jar
 * Run-Class: sunw.demo.jhdemo.JHLauncher
 * Arguments: -helpset api
 * </pre>
 * 
 */
public class Runner {

    public static void main(String[] args) {
	Runner run = new Runner();
	ClassLoader cl = ClassLoader.getSystemClassLoader();
	Manifest mf;
	String runClass = null;
	String arguments = null;
	try {
	    Enumeration enum = cl.getResources("META-INF/MANIFEST.MF");
	    while (enum.hasMoreElements()) {
		URL url = (URL)enum.nextElement();
		InputStream manifestIS = url.openStream();
		mf = new Manifest(manifestIS);
		Attributes main = mf.getMainAttributes();
		runClass = main.getValue("Run-Class");
		arguments = main.getValue("Arguments"); 
		if (runClass != null) {
		    break;
		}
	    }
	    String[] args2 = getArgs(arguments);
	    Class klass = cl.loadClass(runClass);
	    Method m = klass.getMethod("main", 
				       new Class[] { args2.getClass() });
	    m.setAccessible(true);
	    int mods = m.getModifiers();
	    if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
		!Modifier.isPublic(mods)) {
		throw new NoSuchMethodException("main");
	    }
	    m.invoke(null, new Object[] { args2 });
	    } catch (IllegalAccessException e) {
		// This should not happen, as we have disable access checks
	} catch (IOException ex) {
	    ex.printStackTrace();
	} catch (ClassNotFoundException cex) {
	    cex.printStackTrace();
	} catch (NoSuchMethodException nex) {
	    nex.printStackTrace();
	} catch (InvocationTargetException iex) {
	    iex.printStackTrace();
	}
    }

    /**
     * Parse the arguments passed in the jar file into standard args format.
     * Quoted argument "like this" are treated as a single argument.
     */
    private static String[] getArgs (String arguments) {
	Vector args = new Vector();
	int startPos=0, endPos = arguments.length();
	while (true) {
	    int spacePos = arguments.indexOf(" ", startPos);
	    int quotePos = arguments.indexOf("\"", startPos);
	    if ((quotePos != -1 && spacePos < quotePos) ||
		(quotePos == -1 && spacePos != -1)) {
		args.add(arguments.substring(startPos,spacePos));
		startPos = spacePos + 1;
	    } else if ((spacePos != 1 && quotePos < spacePos) ||
		       (spacePos == -1 && quotePos != -1)){
		int quotePos2 = arguments.indexOf("\"", quotePos + 1);
		if (quotePos2 == -1) {
		    // skip the " and move on
		    // user error in arguments
		    startPos = quotePos + 1;
		} else {
		    // get the stuff between the quotes
		    args.add(arguments.substring(quotePos+1, quotePos2-1));
		    startPos = quotePos2 + 1;
		}
	    } else {
		// spaces will always have something at the end. Check for this
		if (endPos != startPos) {
		    args.add(arguments.substring(startPos, endPos));
		}
		// now we are assuming we're at the end
		break;
	    }
	}
	// time to return
	String [] returnArgs = new String[args.size()];
	Enumeration enum = args.elements();
	for (int count = 0;  enum.hasMoreElements(); count ++)  {
	    returnArgs[count] = (String)enum.nextElement();
	}
	return returnArgs;
    }
}
