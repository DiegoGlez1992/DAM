/*
 * @(#) ClientSearchQuery.java 1.6 - last change made 09/08/03
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
 * ClientSearchQuery is an example implementation of a SearchQuery. 
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
public class ClientSearchQuery extends SearchQuery implements Runnable{

    private Thread thread = null;
    private ClientSearchEngine chs;
    private Vector params;


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
     * Create a ClientSearchQuery 
     */
    public ClientSearchQuery(SearchEngine hs) {
	super(hs);
	if (hs instanceof ClientSearchEngine) {
	    chs = (ClientSearchEngine) hs;
	}
    }


    /**
     * Starts the search. The implementation is a subclasses of SearchQuery.
     * This method will invoke searchStarted on SearchListeners.
     * @exception IllegalArgumentException The parameters are not 
     * understood by this engine
     * @exception IllegalStateException There is an active search in progress in this instance
     */
    public void startSearch(String params, Locale l) 
	 throws IllegalArgumentException, IllegalStateException 
    {
	debug ("Starting Search");
	if (isActive()) {
	    throw new IllegalStateException();
	}

	// initialization
	super.start(searchparams, l);
	parseParams(params, l);

	// Actually do the search
	thread = new Thread(this, "QueryThread");
	thread.start();
    }

    /**
     * Stops the search. The implementation is up to the subcalsses of 
     * SearchEngine. This method will invoke searchStopped on 
     * SearchListeners.
     */
    public void stopSearch(SearchQuery sq) {
	debug ("Stop Search");
	if (thread == null) {
	    throw new IllegalArgumentException();
	}

	thread.stop();
	this.fireSearchFinished();
	thread = null;
    }

    // Parse the params
    private synchronized void parseParams(String searchParams, Locale l) { 
	BreakIterator boundary;
	int start, quoteStart=0;
	String word;
	boolean prevWord=false, quote=false;
	String oldword = null;

	params = new Vector();
	boundary = BreakIterator.getWordInstance(l);
	boundary.setText(searchParams);
	start = boundary.first();
	for (int end = boundary.next();
	     end != BreakIterator.DONE;
	     start = end, end = boundary.next()) {
	    word = new String(searchParams.substring(start,end));
	    word = word.toLowerCase();
	    word = word.trim();
	    if (word.length() == 0) {
	        continue;
	    }
	    if (word.compareTo("and") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(AND));
		continue;
	    } 
	    if (word.compareTo("or") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(OR));
		continue;
	    }
	    if (word.compareTo("not") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(NOT));
		continue;
	    }
	    if (word.compareTo("near") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(NEAR));
		continue;
	    }
	    if (word.compareTo("adj") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(ADJ));
		continue;
	    }
	    if (word.compareTo("(") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(LEFT_PAREN));
		continue;
	    }
	    if (word.compareTo(")") == 0 && !quote) {
		prevWord = false;
		params.addElement (new Integer(RIGHT_PAREN));
		continue;
	    }
	    if (word.compareTo("\"") == 0) {
		prevWord = false;
		quoteStart = start;
		quote = true;
		continue;
	    }
	    if (word.compareTo("*") == 0) {
		prevWord = false;
		if (oldword != null) {
		    params.removeElementAt(params.size() - 1);
		    oldword = oldword + word;
		    params.addElement (oldword);
		    prevWord = true;
		}
		continue;
	    }
	    if (prevWord == true) {
		if (quote) {
		    params.addElement (new Integer(ADJ));
		} else {
		    params.addElement (new Integer(NEAR));
		}
	    }
	    if (quote == true) {
		if (word.endsWith("\"")) {
		    word = new String(searchParams.substring(start, end-1));
		    quote = false;
		}
	    }
	    prevWord = true;
	    oldword = word;
	    params.addElement (word);
	}
    }


    public boolean isActive() {
	if (thread == null) { 
	    return false;
	}
	return thread.isAlive();
    }

    public void run() {
	WordVector wordVec = chs.getWordVector();
	DocVector docVec = chs.getDocVector();
	debug ("Threaded search");
	Vector wordInventory = new Vector();
	int term=0, j, k, size;
      
	for (int i=0; i < params.size() ; i++) {
	    Object obj = (Object)params.elementAt(i);
	    if (obj instanceof Integer) {
		term = ((Integer)obj).intValue();
	    } else {
		// This is a word so find the word 
		Word word = wordVec.findWord((String)obj);
		if (word == null) {
		    // Do some special processing here for operators but eventually
		    // continue
		    continue;
		}
	  
		// Look at the boolean operator and do the right thing
		if (term == AND) {
		    wordInventory = word.AndIntersection(wordInventory);
		} else if (term == OR) {
		    wordInventory = word.OrIntersection(wordInventory);
		} else if (term == NOT) {
		    wordInventory = word.NotIntersection(wordInventory);
		} else if (term == NEAR) {
		    wordInventory = word.NearWord(wordInventory, 20);
		} else if (term == ADJ) {
		    wordInventory = word.NearWord(wordInventory, 1);
		} else if (term == LEFT_PAREN) {
		} else if (term == RIGHT_PAREN) {
		} else {
		    wordInventory = new Vector(20,10);
		    Vector tmpInventory = word.getWordInventory();
		    size = tmpInventory.size();
		    for (k=0; k < size ; k++) {
			wordInventory.addElement (tmpInventory.elementAt(k));
		    }
		}
	    }
	}
	// The inventory is in place loop through and build a list of documents
	// First clear out the weigth in each document in docVec;
	size = docVec.size();
	for (j=0; j < size; j++) {
	    ((Doc)docVec.elementAt(j)).resetWeight();
	}

	size = wordInventory.size();
	// Loop through the word inventories and increment the weight;
	for (j=0; j < size ; j++) {
	    WordInventory wordInv = (WordInventory)wordInventory.elementAt(j);
	    int docId = wordInv.getDocId();
	    ((Doc)docVec.elementAt(docId)).addToWeight(wordInv.getWeight());
	}
      
	Vector docHash = new Vector();
	size = docVec.size();
	for (j=0; j < size; j++) {
	    Doc doc = (Doc)docVec.elementAt(j);
	    if (doc.getWeight() > 0) {
		SearchItem item = new SearchItem(chs.getBase(),
						 doc.getTitle(),
						 Locale.getDefault().toString(),
						 doc.getURLString(),
						 doc.getWeight(),
						 0, 0, null);
		docHash.addElement(item);
	    }
	}

	debug("returning hits");
	
	fireItemsFound(true, docHash);
	fireSearchFinished();
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
