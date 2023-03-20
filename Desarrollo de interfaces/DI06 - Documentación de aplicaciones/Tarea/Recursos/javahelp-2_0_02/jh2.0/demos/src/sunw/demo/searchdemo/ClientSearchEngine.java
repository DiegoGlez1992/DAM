/*
 * @(#) ClientSearchEngine.java 1.17 - last change made 09/08/03
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

package sunw.demo.searchdemo;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.EventListener;
import java.util.Locale;
// import javax.help.*;
import javax.help.search.*;
import javax.help.search.SearchEvent;
import javax.help.search.SearchListener;


/**
 * ClientSearchEngine is the default search engine. 
 *
 * Search results are returned through SearchEvents to
 * listeners that
 * register with a ClientSearchEngine instance.
 *
 * @author Roger D. Brinkley
 * @version	1.3	10/29/97
 *
 * @see java.help.SearchEngine
 * @see java.help.SearchEvent
 * @see java.help.SearchListener
 */
public class ClientSearchEngine extends SearchEngine {

    private WordVector wordVec;
    private DocVector docVec;

    /**
     * The "and" modifier specifies that the search return pages
     * that contain both strings in the search entry.
     */
    public static int AND=1;

    /**
     * The "or" modifier specifies that the search return pages
     * that contain either of the entry strings.
     */
    public static int OR=2;

    /**
     * The "not" modifier specifies that the search return pages
     * in which the first string occurs and the second string
     * does not occur.
     */
    public static int NOT=3;

    /**
     * The "near" modifier specifies that the search return pages
     * in which the first string occurs withing 20 words of
     * the second string.  This is the default behavior when
     * no search modifiers are specified.
     */
    public static int NEAR=4;

    /**
     * ( LEFT PAREN
     */
    public static int LEFT_PAREN=5;

    /**
     * ) RIGHT PAREN
     */
    public static int RIGHT_PAREN=6;

    /**
     * The "adj" modifier specifies that the search return pages
     * that contain search entries directly adjacent to
     * each other. Equivalent to enclosing
     * the entries in quotation marks ("").
     */
    public static int ADJ=7;


    /**
     * Create a ClientSearchEngine 
     */
    public ClientSearchEngine(URL base, Hashtable params) {
	super (base, params);

	URL url;
	URLConnection uc;
	DataInputStream from;

	debug ("Loading Search Database");
	
	// Load the Queary Engine and Search DB here
	try {
	    String urldata = (String) params.get("data");
	    debug ("base="+base.toExternalForm());
	    debug ("urldata=" + urldata);
	    url = new URL(base, urldata + ".inv");
	    debug ("url: " + url);
	    uc = url.openConnection();
	    uc.setAllowUserInteraction(true);
	    from = new DataInputStream(new BufferedInputStream(uc.getInputStream()));
	    wordVec = new WordVector(from);

	    url = new URL(base, urldata + ".dat");
	    debug ("url: " + url);
	    uc = url.openConnection();
	    uc.setAllowUserInteraction(true);
	    from = new DataInputStream(new BufferedInputStream(uc.getInputStream()));
	    docVec = new DocVector(from);
	    debug ("Search Database loaded");
	} catch (Exception e) {
	    wordVec = null;
	    docVec = null;
	    debug ("Failed to load Search DataBase");
	    e.printStackTrace();
	}
    }


    public SearchQuery createQuery() {
	return new ClientSearchQuery(this);
    }


    public WordVector getWordVector() {
	return wordVec;
    }

    public DocVector getDocVector() {
	return docVec;
    }

    public URL getBase() {
	return base;
    }

    /**
     * For printf debugging.
     */
    private static boolean debugFlag = false;
    private static void debug(String str) {
        if( debugFlag ) {
            System.out.println("ClientSearchEngine: " + str);
        }
    }

}
