/**
 * @(#) ApiDemo.java 1.80 - last change made 09/15/04
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

package sunw.demo.idedemo;

import java.util.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.lang.reflect.*;
import javax.help.*;
import javax.swing.*;
import javax.swing.text.*;
import sunw.demo.classviewer.ClassViewerNavigator;
import java.beans.PropertyVetoException;
import javax.help.Map.ID;

/**
 * This class is the main class of the IdeHelp demo
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.80	09/15/04
 */

public class ApiDemo extends JPanel implements ActionListener {

    private static boolean hasCustomCursor;

    static String swingPkg = "javax.swing.plaf.";
    //static String swingPkg = "com.sun.java.swing.plaf.";
    static JFrame frame;

    // Defaults for Main Help
    static final String helpsetName = "IdeHelp";
    static final String helpsetLabel = "Demo JDE - Help";

    // Main HelpSet & Broker
    HelpSet mainHS = null;
    HelpBroker mainHB;

    // The JavaDoc Help
    HelpSet apiHS = null;
    HelpBroker apiHB;


    // Auxiliaries
    JRootPane rootpane;
    JSplitPane split;
    JMenuItem menuItem, menu_help, menu_open, menu_apihelp;
    JTabbedPane messages;
    int miscTabIndex;

    // Workaround for 1.1.4 compiler bug
    JMenuItem item1, item2;

    // The initial width and height of the frame
    public static int WIDTH = 845;
    public static int HEIGHT = 495;

    public static int JH_WIDTH = 645;
    public static int JH_HEIGHT = 495;

    // The internal desktop
    JDesktopPane desktop;

    // For the Source JInternalFrame
    JInternalFrame sourceIFrame;

    // For the ClassViewer JInternalFrame
    JInternalFrame classViewerIFrame;

    JButton helpbutton;
    private final boolean showTimes = false; // should be using a timer -epll

    ApiDemo() {

	// Create the main HelpBroker, then the api HelpBroker
	// (these could be done on-demand, but they are quite fast for our
	//  case; this might be different if the HelpSet was non-local).

	if (showTimes) 
	    System.err.print("creating mainHB...");

	try {
	    ClassLoader cl = ApiDemo.class.getClassLoader();
	    URL url = HelpSet.findHelpSet(cl, helpsetName);
	    mainHS = new HelpSet(cl, url);
	} catch (Exception ee) {
	    System.out.println ("Help Set "+helpsetName+" not found");
	    return;
	} catch (ExceptionInInitializerError ex) {
	    System.err.println("initialization error:");
	    ex.getException().printStackTrace();
	}
	mainHB = mainHS.createHelpBroker();

	if (showTimes) 
	    System.err.println(" done!");

	// The api HelpBroker.

	if (showTimes)
	    System.err.print("creating apiHB...");

	try {
	    ClassLoader cl = ApiDemo.class.getClassLoader();
	    URL url = HelpSet.findHelpSet(cl, "api");
	    apiHS = new HelpSet(cl, url);
	} catch (Exception ee) {
	    System.out.println ("API Help Set not found");
	    return;
	}
	apiHB = apiHS.createHelpBroker("api"); // Use the Help Broker

	if (showTimes)
	    System.err.println(" done!");

	// locate resource bundle

	resources = ResourceBundle.getBundle("sunw.demo.idedemo.IdeDemo");
        if (resources == null) {
            System.err.println("Resources for application IdeDemo not found");
        }

	WindowListener l = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		setVisible(false);
		System.exit(0);
	    }
	    public void windowClosed(WindowEvent e) {
		setVisible(false);
		System.exit(0);
	    }
	    public void windowOpened(WindowEvent e) {
		split.setDividerLocation(0.80);
		//		split.setDividerLocation(250);
	    }
	};
	frame.addWindowListener(l);
	rootpane = frame.getRootPane();
	//	mainHB.enableHelpKey(rootpane, "top", null);
	mainHB.enableHelpKey(rootpane, "top", mainHS, "javax.help.SecondaryWindow", null);
	//	mainHB.enableHelpKey(rootpane, "top", mainHS, "javax.help.Popup", null);
	setLayout(new BorderLayout());
	
	JPanel header=new JPanel();
	header.setLayout(new BorderLayout());
	header.add(createMenus(),"North");
	header.add(createToolbar(),"South");

	add(header,"North");

	desktop = new JDesktopPane();
	desktop.setOpaque(true);
	desktop.setDoubleBuffered(true);

	createSourceIFrame();
	desktop.add(sourceIFrame, JLayeredPane.PALETTE_LAYER);
	sourceIFrame.show();
	

	JPanel panel=new JPanel();
	panel.setLayout(new BorderLayout());
	messages=new JTabbedPane();
	
	JTextArea newtext=new JTextArea();
	newtext.setBackground(Color.white);
	CSH.setHelpIDString(newtext, "build.build");
	messages.addTab("Build", newtext);
	newtext = new JTextArea();
	CSH.setHelpIDString(newtext, "debug.overview");
	messages.addTab("Debug", newtext);
	newtext = new JTextArea();
	CSH.setHelpIDString(newtext, "browse.strings");
	messages.addTab("String Search", newtext);
	miscTabIndex = messages.getTabCount();
	messages.insertTab("Misc", null, new JTextArea(), null, miscTabIndex);

	// Find out when we are selected.  When doing so...

	messages.setSelectedIndex(0);
	messages.setDoubleBuffered(true);
	panel.add(messages,"Center");
	split=new JSplitPane(JSplitPane.VERTICAL_SPLIT,desktop,panel);
	//split.setDoubleBuffered(true);
	split.setOneTouchExpandable(true);

	add(split,"Center");

    }

    /**
     * Create a JButton out of a resource name
     */
    private JButton createButton(String name) {
	java.net.URL url = this.getClass().getResource(name);
	ImageIcon icon = new ImageIcon(url);
	return new JButton(icon);
    }

    private void createClassViewer() {
	if (classViewerIFrame != null) {
	    return;
	}
	classViewerIFrame = new JInternalFrame("Classes", true, true, true, true);
	JComponent content = new JPanel();
	content.setLayout(new BorderLayout());
	content.setDoubleBuffered(true);
	classViewerIFrame.setDoubleBuffered(true);
	classViewerIFrame.setBounds(570,10,200,310);

	JHelpNavigator xnav;
	try {
	    // The content viewer
	    JHelpContentViewer viewer1 = new JHelpContentViewer(apiHS);

	    // Stuff it in the misc index of the Tab pane.
	    messages.setComponentAt(miscTabIndex, viewer1);
	    messages.setSelectedIndex(miscTabIndex);

	    // Create a ClassViewerNavigator sharing the same model
	    xnav = (JHelpNavigator) apiHS.getNavigatorView("TOC").createNavigator(viewer1.getModel());

	    // Add the viewer to the presentation
	    content.add(xnav, "Center");
	    classViewerIFrame.setContentPane(content);
	    
	} catch (Exception ee) {
	    System.err.println("Caught excpetion while creating ClassViewer: "+ee);
	    ee.printStackTrace();
	    JTextArea sourceText = new JTextArea("Trouble adding Navigator");
	    content.add(sourceText, "Center");
	}
    }


    private void createSourceIFrame() {
	if (sourceIFrame != null) {
	    return;
	}

	sourceIFrame = new JInternalFrame("Source", true, true, true, true);
	CSH.setHelpIDString(sourceIFrame, "edit.editsource");
	JComponent c = (JComponent) sourceIFrame.getContentPane();
	c.setLayout(new BorderLayout());
	c.setDoubleBuffered(true);
	sourceIFrame.setDoubleBuffered(true);
	sourceIFrame.setBounds(10,10,550,310);

	JTextArea sourceText = new JTextArea("");       
	sourceText.setFont(new Font("Courier",Font.PLAIN,12));
	sourceText.setBackground(Color.white);

	sourceText.append("/* To view JavaHelp click, Help, Java API Help */"+
		    "\n\nimport java.applet.Applet;"+
		    "\nimport java.awt.Graphics;"+
		    "\n\npublic class HelloWorld extends Applet {"+
		    "\n\n    public void paint(Graphics g) {"+
		    "\n        g.drawString(\"Hello world!\", 50, 25);"+
		    "\n    }\n}\n");

	c.add(sourceText,"Center");
    }

    ResourceBundle resources;

    private JButton addButton(JToolBar toolbar, String img, String tipKey) {
	JButton button = createButton(img);
	if (tipKey != null) {
	    try {
		String tipText =
		    resources.getString("toolbar."+tipKey+".tip");
		button.setToolTipText(tipText);
	    } catch (Exception ex) {
		System.err.println("Could not find a resource for "+tipKey);
	    }
	}
	toolbar.add(button);
	return button;
    }

    public JToolBar createToolbar() {
	JToolBar toolbar=new JToolBar();
	CSH.setHelpIDString(toolbar,"toolbar.main");


	addButton(toolbar, "images/open.gif", "open");
	addButton(toolbar, "images/save.gif", "save");
	toolbar.addSeparator();
	addButton(toolbar, "images/start.gif", "start");
	addButton(toolbar, "images/break.gif", "stop");
	addButton(toolbar, "images/setbreak.gif", "setbreak");
	addButton(toolbar, "images/resume.gif", "resume");
	addButton(toolbar, "images/goto.gif", "goto");
	addButton(toolbar, "images/goend.gif", "goend");
	addButton(toolbar, "images/skip.gif", "skip");
	toolbar.addSeparator();
	addButton(toolbar, "images/down.gif", "down");
	addButton(toolbar, "images/up.gif", "up");
	toolbar.addSeparator();
	helpbutton= addButton(toolbar, "images/help.gif", "help");
	helpbutton.addActionListener(new CSH.DisplayHelpAfterTracking(mainHB));
	helpbutton.setEnabled(hasCustomCursor); // enable only on 1.2

	return toolbar;
    }

  private JMenuItem addMenuItem(JMenu menu, String label, String tipKey) {
    JMenuItem item = new JMenuItem(label);
    if (tipKey != null) {
	try {
	    String tipText = resources.getString("menu."+tipKey+".tip");
	    item.setToolTipText(tipText);
	 } catch (Exception ex) {
	     System.err.println("Could not find a resource for "+tipKey);
	 }
    }
    menu.add(item);
    return item;
  }

    // An Option Dialog.  Not really good for help

    private void showDialog1() {
	Object options[] = {"OK", "CANCEL", "HELP"};
	int index =
	    JOptionPane.showOptionDialog(null, // parent
					 "Exit?", // message object
					 "Quit", // string title
					 JOptionPane.DEFAULT_OPTION,
					 JOptionPane.QUESTION_MESSAGE,
					 null, // Icon
					 options,
					 options[0]
					 );
	switch (index) {
	case 0:
	    System.exit(0);
	case 1:
	    break;
	case 2:
	    System.err.println("will ask for help");
	    break;
	}
    }

    public JMenuBar createMenus() {
	JMenuBar menuBar = new JMenuBar();
	menuBar.setBackground(getBackground());
	//	menuBar.setOpaque(true);
	JMenu menu = new JMenu("File");
	CSH.setHelpIDString(menu, "menus.file");
	menu.setToolTipText("File operations");
	menuBar.add(menu);
	addMenuItem(menu, "New", "file.new");
	addMenuItem(menu, "Open...", "file.open");
	menu.addSeparator();
	addMenuItem(menu, "Save", "file.save");
	addMenuItem(menu, "Save As...", "file.saveas");
	menu.addSeparator();
	menuItem = addMenuItem(menu, "Exit", "file.exit");

	// Ask for confirmation on exit
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e){

		Object options[] = {"OK", "CANCEL", "HELP"};
		int index =
		    JOptionPane.showOptionDialog(null, // parent
						 "Exit IdeDemo?", // message object
						 "Exit Confirmation", // string title
						 JOptionPane.DEFAULT_OPTION,
						 JOptionPane.QUESTION_MESSAGE,
						 null, // Icon
						 options,
						 options[0]
						 );
		switch (index) {
		case 0:
		    System.exit(0);
		case 1:
		    break;
		case 2:
		    try {
			mainHB.setCurrentID("menus.file");
		    } catch (Exception be) {
		    }
		    break;
		}
	    }
	});

	menu = new JMenu("Edit");
	CSH.setHelpIDString(menu, "menus.edit");
	menuBar.add(menu);
	addMenuItem(menu, "Undo", null);
	addMenuItem(menu, "Redo", null);
	addMenuItem(menu, "Cut", null);
	addMenuItem(menu, "Copy", null);
	addMenuItem(menu, "Paste", null);
	menu.addSeparator();

	menuItem = addMenuItem(menu, "Find", null);
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e){
		JDialog dialog = new FindDialog(ApiDemo.this, null); //  non-modal
		dialog.show();
	    }
	});
	
	menu.addSeparator();
	addMenuItem(menu, "Go to...", null);

       	menu = new JMenu("Build");      
	CSH.setHelpIDString(menu, "menus.build");
	menuBar.add(menu);
	menuItem = addMenuItem(menu, "Build", null);
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e){
		mainHB.showID("build.build", 
			      "javax.help.SecondaryWindow",
			      "main");
	    }
	});
	

	menuItem = addMenuItem(menu, "Build All", null);
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e){
		javax.help.Popup popup = 
		    (javax.help.Popup) javax.help.Popup.getPresentation(mainHS, null);
		popup.setInvoker((Component)e.getSource());
		popup.setCurrentID("build.compilefile");
		popup.setDisplayed(true);
	    }
	});
	
	menuItem = addMenuItem(menu, "Compile File", null);
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e){
		mainHB.showID("build.compilefile",
			      "javax.help.MainWindow",
			      "main");
	    }
	});

	menu = new JMenu("Debug");
	CSH.setHelpIDString(menu, "menus.debug");
	menuBar.add(menu);
	addMenuItem(menu, "Start/Restart", null);
	addMenuItem(menu, "Stop", null);

	menu = new JMenu("Window");
	CSH.setHelpIDString(menu, "menus.windows");
	menuBar.add(menu);

	item1 = menuItem = addMenuItem(menu, "Source", null);
	item1.addActionListener(this);
	item2 = menuItem = addMenuItem(menu, "Class Inspector", null);
	item2.addActionListener(this);

	JMenu help=new JMenu("Help");
	CSH.setHelpIDString(help, "menus.help");
        if((UIManager.getLookAndFeel().getName()).equals("CDE/Motif")){
            menuBar.add(Box.createGlue());
        }
	menuBar.add(help);
	menu_help=new JMenuItem(helpsetLabel);
	menu_help.addActionListener(new CSH.DisplayHelpFromSource(mainHB));
	help.add(menu_help);
	help.addSeparator();

	menu_apihelp=new JMenuItem("Java API Reference");
	CSH.setHelpIDString(menu_apihelp, "intro");
	menu_apihelp.addActionListener(new CSH.DisplayHelpFromSource(apiHB));
	help.add(menu_apihelp);

	menu_apihelp=new JMenuItem("Java API Reference - Secondary Window");
	CSH.setHelpIDString(menu_apihelp, "intro");
	menu_apihelp.addActionListener(new CSH.DisplayHelpFromSource(apiHS,
								     "javax.help.SecondaryWindow", null));
	help.add(menu_apihelp);

	menu_apihelp=new JMenuItem("Java API Reference - Popup");
	apiHB.enableHelpOnButton(menu_apihelp, "intro", apiHS,
				 "javax.help.Popup", null);
	help.add(menu_apihelp);
	help.addSeparator();

	if (hasCustomCursor) {
	    JMenuItem menu_cshHelp = new JMenuItem("Help OnItem - Main Window");
	    menu_cshHelp.addActionListener(new CSH.DisplayHelpAfterTracking(mainHB));
	    help.add(menu_cshHelp);

	    menu_cshHelp = new JMenuItem("Help OnItem - Secondary Window");
	    menu_cshHelp.addActionListener(new CSH.DisplayHelpAfterTracking(mainHS, "javax.help.SecondaryWindow", "main"));
	    help.add(menu_cshHelp);

	    menu_cshHelp = new JMenuItem("Help OnItem - Popup");
	    menu_cshHelp.addActionListener(new CSH.DisplayHelpAfterTracking(mainHS, "javax.help.Popup", null));
	    help.add(menu_cshHelp);
	    help.addSeparator();
	}

	return menuBar;
    }

    public void actionPerformed(ActionEvent e) {
	Object source = e.getSource();
	if (source == item1) {
	    try {
		if (sourceIFrame.isClosed()) {
		    createSourceIFrame();
		    desktop.add(sourceIFrame, JLayeredPane.PALETTE_LAYER);
		    sourceIFrame.setIcon(false);
		    sourceIFrame.show();
		}
	    } catch (PropertyVetoException ex) {
		// Oh well, ignore
	    }
	} else if (source == item2) {
	    try {
		if (classViewerIFrame == null) {
		    createClassViewer();
		    desktop.add(classViewerIFrame, JLayeredPane.PALETTE_LAYER);
		    classViewerIFrame.setIcon(false);
		    classViewerIFrame.show();
		} else if (classViewerIFrame.isClosed()) {
		    desktop.add(classViewerIFrame, JLayeredPane.PALETTE_LAYER);
		    classViewerIFrame.setIcon(false);
		    classViewerIFrame.show();
		}
	    } catch (PropertyVetoException ex) {
		// ignore
	    }
	}
	
	if (e.getID() == Event.WINDOW_DESTROY) {	   	       	    
	    frame.dispose();
	    System.exit(0);
	}
    }
    
    private static String[] shiftArgs(String args[], int step) {
	int count = args.length;
	String back[] = new String[count-step];
	for (int i=0; i<count-step; i++) {
	    back[i] = args[i+step];
	}
	return back;
    }

    public static void main(String args[]) throws Exception {
	if (args.length >= 1) {
	    String laf = args[0];
	    UIManager.setLookAndFeel(swingPkg + laf);
	    args = shiftArgs(args, 1);
	}

	frame=new JFrame("Java Development Environment");
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(new ApiDemo(),"Center");
	frame.setBounds(100,100,WIDTH,HEIGHT);	
	frame.show();
    }

    static private void usage() {
	System.err.println("Usage: [laf] [-helpset label name]");
	System.exit(1);
    }


    private static void printData(Container container) {
	Component k[] = container.getComponents();
	for (int i=0; i<k.length; i++) {
	    System.err.println(k[i]);
	}
	System.err.println(container);
    }

    // Static initialization
    static {
	Method m = null;
	hasCustomCursor = false;
	try {
	    Class types[] = {Image.class, Point.class, String.class};
	    m = Toolkit.class.getMethod("createCustomCursor",
					       types);
	    if (m != null) {
		hasCustomCursor = true;
	    }
	} catch (NoSuchMethodError ex) {
	    // as in JDK1.1
	} catch (NoSuchMethodException ex) {
	    // as in JDK1.1
	}
    }

}


class FindDialog extends JDialog {
    private JButton helpButton;
    private JButton closeButton;
    private JFrame frame;
    private ApiDemo demo;

    private void initComponents() {

	// playing with Boxes
	Box topBox = Box.createVerticalBox();

	Box box1 = Box.createHorizontalBox();
	JLabel findLabel = new JLabel("Find: ");
	JTextField textField = new JTextField(20);
	box1.add(Box.createHorizontalStrut(5));
	box1.add(findLabel);
	box1.add(textField);
	box1.add(Box.createHorizontalStrut(5));

	topBox.add(Box.createVerticalStrut(5));
	topBox.add(box1);

	Box box3 = Box.createHorizontalBox();
	box3.add(Box.createHorizontalGlue());
	JButton findButton = new JButton("Find Next");
	JButton prevButton = new JButton("Find Previous");
	box3.add(findButton);
	box3.add(Box.createHorizontalStrut(10));
	box3.add(prevButton);
	box3.add(Box.createHorizontalGlue());

	topBox.add(box3);
	    
	Box box4 = Box.createHorizontalBox();
	Box box5 = Box.createHorizontalBox();

	JCheckBox backwardsCheck = new JCheckBox("Find Backward");
	JCheckBox ignoreCaseCheck = new JCheckBox("Ignore Case");

	box4.add(Box.createHorizontalGlue());
	box4.add(backwardsCheck);
	box4.add(Box.createHorizontalGlue());

	box5.add(Box.createHorizontalGlue());
	box5.add(ignoreCaseCheck);
	box5.add(Box.createHorizontalGlue());

	topBox.add(box4);
	topBox.add(box5);

	Box box2 = Box.createHorizontalBox();
	closeButton = new JButton("Close");
	helpButton = new JButton("Help");

	box2.add(closeButton);
	box2.add(helpButton);

	Box box6 = Box.createHorizontalBox();
	box6.add(Box.createHorizontalStrut(5));
	box6.add(new JSeparator());
	box6.add(Box.createHorizontalStrut(5));

	topBox.add(Box.createVerticalStrut(10));
	topBox.add(box6);
	topBox.add(box2);
	getContentPane().add(topBox);
    }

    public FindDialog(ApiDemo demo, JFrame f) {
	super(f, "Find", false);
	this.frame = f;
	this.demo = demo;
	initComponents();
	pack();

	closeButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
	    }
	});
	demo.mainHB.enableHelpOnButton(helpButton, "browse.strings", null);
	placeDialog();

	show();
    }

    protected void placeDialog() {
	if (frame != null) {
	    int x = frame.getLocation().x + 30;
	    int y = frame.getLocation().y + 100;
	    setLocation(x, y);
	}
    }
}
