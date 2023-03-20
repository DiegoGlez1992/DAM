/*
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

package sunw.demo.browser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Locale;
import java.io.IOException;
import javax.help.*;

/** 1.2 version of this
 */

public class HelpButton extends JApplet implements ActionListener{
    private String helpSetName = null;
    private String helpSetURL = null;
    private HelpSet hs;
    private HelpBroker hb;
    private JButton button;
    static JFrame frame;

    public HelpButton() {
	super();
    }

    public HelpButton(String hsName, String hsURL) {
	helpSetName = hsName;
	helpSetURL = hsURL;
	button = new JButton("Help");
	button.addActionListener(this);
	this.getContentPane().add(button);
    }

    public void init() {
	helpSetName = getParameter("HELPSETNAME");
	helpSetURL = getParameter("HELPSETURL");
	button = new JButton("Help");
	button.addActionListener(this);
	this.getContentPane().add(button);
    }


    public void stop() {
        if (button != null) {
            getContentPane().remove(button);
            button = null;
        }
	hs = null;
	hb = null;
    }

    public void actionPerformed(ActionEvent e){
	if (hs == null) {
	    createHelpSet();
	    hb = hs.createHelpBroker();
	}
	hb.setDisplayed(true);
    }

    private void createHelpSet() {
	ClassLoader loader = this.getClass().getClassLoader();
	URL url;
	try {
	    url = HelpSet.findHelpSet(loader, helpSetName);
	    debug ("findHelpSet url=" + url);
	    if (url == null) {
		url = new URL(getCodeBase(), helpSetURL);
		debug("codeBase url=" + url);
	    }
	    hs = new HelpSet(loader, url);
	} catch (Exception ee) {
	    System.out.println ("Trouble in createHelpSet;");
	    ee.printStackTrace();
	    return;
	}
    }

    public static void main(String args[]) throws Exception {
	frame=new JFrame("HelpButton demo");
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(new HelpButton("HolidayHistory", 
						  "HolidayHistory.hs"),
				   "Center");
	frame.show();
    }

    /**
     * For printf debugging.
     */
    private final boolean debug = false;
    private void debug(String str) {
        if (debug) {
            System.out.println("HelpButton: " + str);
        }
    }

}
