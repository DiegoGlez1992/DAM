/*
 * @(#) ClassViewerNavigator.java 1.21 - last change made 09/08/03
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

package sunw.demo.classviewer;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.UIDefaults;
import javax.swing.LookAndFeel;
import javax.help.*;
import java.beans.*;

/**
 * JHelpTOCNavigator is a JHelpNavigator for a TOC
 * At this point it just inherits from JHelpTreeNavigator.
 * All of the tree navigation and selection has been delegated to the UI
 * where the JTree will be created.
 *
 * @author Paul Dumais
 * @author Eduardo Pelegri-Llopart
 * @version	1.21	09/08/03
 */

public class ClassViewerNavigator extends JHelpNavigator {
    protected boolean synch;

    public ClassViewerNavigator(NavigatorView info) {
	super(info);
    }

    public ClassViewerNavigator(NavigatorView info, HelpModel model) {
	super(info, model);
    }

    /**
     * Default on the NavigatorView
     */

    public ClassViewerNavigator(HelpSet hs, String name, String title)
	throws InvalidNavigatorViewException
    {
	super(new ClassViewerView(hs, name, title, null));
    }


    /**
     * The UID for this JComponent
     */
    public String getUIClassID() {
	return "ClassViewerNavigatorUI";
    }


    /**
     * Initialize Look and Feel  -- HERE
     */

    static private final
    PropertyChangeListener lnfCL = new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent e) {
	    if (e.getPropertyName().equals("lookAndFeel")) {
	        installLookAndFeelDefaults();
	    }
	}
    };

    static private void installLookAndFeelDefaults() {
	String basicPackageName = "sunw.demo.classviewer.plaf.basic.";
	String metalPackageName = "sunw.demo.classviewer.plaf.metal.";
	String motifPackageName = "sunw.demo.classviewer.plaf.motif.";
	String windowsPackageName = "sunw.demo.classviewer.plaf.windows.";
	LookAndFeel lnf = UIManager.getLookAndFeel();
        UIDefaults table = UIManager.getLookAndFeelDefaults();

	// All the tables could go directly to the basic ComponentUIs...

	debug("installLookAndFeelDefaults - ", lnf);
        if ((lnf != null) && (table != null)) {
	    if (lnf.getID().equals("Motif")) {
		Object[] uiDefaults = {
	           "ClassViewerNavigatorUI",
		   motifPackageName + "MotifClassViewerNavigatorUI"
		};
		table.putDefaults(uiDefaults);
	    } else if (lnf.getID().equals("Windows")) {
		Object[] uiDefaults = {
	           "ClassViewerNavigatorUI",
		   windowsPackageName + "WindowsClassViewerNavigatorUI"
		};
		table.putDefaults(uiDefaults);
	    } else {
		// Default
		Object[] uiDefaults = {
	           "ClassViewerNavigatorUI",
		   metalPackageName + "MetalClassViewerNavigatorUI"
		};
		table.putDefaults(uiDefaults);
	    }
	}
    }

    static {
	installLookAndFeelDefaults();
	UIManager.addPropertyChangeListener(lnfCL);
    }

    /**
     * What are our capabilities
     */

    private static final boolean debug = false;
    private static void debug(Object m1, Object m2, Object m3) {
	if (debug) {
	    System.err.println("ClassViewerNavigator: "+m1+m2+m3);
	}
    }
    private static void debug(Object m1) { debug(m1,null,null); }
    private static void debug(Object m1, Object m2) { debug(m1,m2,null); }
}
