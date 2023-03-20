/**
 * @(#) Merge.java 1.41 - last change made 09/08/03
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

package sunw.demo.merge;

import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.help.*;
import java.security.AccessControlException;

/**
 * This demonstrates how to merge different HelpSets.
 *
 * The application creates a master helpset and provides a button to show it on
 * a HelpBroker.  The application then permits to add and remove HelpSets from
 * this master HelpSet.
 *
 * A HelpSet is identified by a base URL to the HelpSet and a ClassLoader instance.
 *
 * For our purposes a ClassLoader can be thought as a collection of JAR files and
 * directories.  This colection will contain the documents to be presented.  The collection
 * may also contain some non-default classes, like Navigators and Search Engines.
 *
 * On JDK1.1, this application will only let you use the collection in CLASSPATH
 * (also called the system classloader)
 *
 * On JDK1.2, this application lets you give an explicit list of URLs.  CLASSPATH is
 * appended to the list.
 *
 * For each HelpSet, start by defining (or not) the list of directories/JARs.
 * Next either give a HelpSet name and let the application find you a corresponding URL
 * in that collection, or explicitly give a URL to the HelpSet file.
 * Finally, load the HelpSet.
 *
 * The HelpSet will be added to the list of loaded HelpSets (so you can remove it later)
 * and the "master" HelpSet will be updated.
 *
 * NOTE: HelpSet merging is only partially implemented in EA2
 *
 * @author Eduardo Pelegri-Llopart
 * @author Roger D. Brinkley
 * @version	1.41	09/08/03
 */

/*
 * TODO:
 *
 * - We need a better way of listing the URLs for ClassLoader
 * - URLs should be accepted in the ClassLoader
 */


public class Merge extends JFrame {
    private HelpSet masterHS;	// the HelpSet on which to integrate
    private URL masterURL;	// the base URL to the this HelpSet
    private ClassLoader myLoader; // our ClassLoader
    private JList hsList;	// current HelpSets as List
    private Hashtable clTable;	// table of URL ClassLoader

    // Main HelpSet & Broker
    private HelpSet mainHS = null;
    private HelpBroker mainHB;
    private Font font;

    private static boolean on12;

    public Merge() {
	super("HelpSet Merge Demo");

	// Exit on close
	this.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		System.exit(0);
	    }
	    public void windowClosed(WindowEvent e) {
		System.exit(0);
	    }
	});

	try {
	    ClassLoader cl = getMyLoader();
	    URL url = HelpSet.findHelpSet(cl, "Master");
	    mainHS = new HelpSet(cl, url);
	} catch (Exception ee) {
	    System.out.println ("Help Set Master not found");
	    return;
	}
	mainHB = mainHS.createHelpBroker();

	// Add a button to show the helpset
	initializeMasterHS();
	initializeGUI();
    }

    // Static initialization
    static {
	// Determine whether we are running on 1.2 or 1.1
	// (Should change so it checks for existance of URLClassLoader, which
	//  is what we really depend on - epll)
	try {
	    AccessControlException ex = new AccessControlException("");
	    on12 = true;
	} catch (NoClassDefFoundError ex) {
	    on12 = false;
	}
    }

    /*
     * Initialize the HelpSets
     */
    private void initializeMasterHS() {
	myLoader = getMyLoader();
	masterURL = HelpSet.findHelpSet(myLoader, "Master");
	try {
	    masterHS = new HelpSet(myLoader, masterURL);
	} catch (Exception ex) {
	    System.err.println("Could not create the master HelpSet");
	    ex.printStackTrace();
	    System.exit(1);
	}
	// Class Loader table
	if (on12) {
	    clTable = new Hashtable();
	}
    }

    /*
     * Add all the components we need
     */
    private JButton removeButton;
    private JButton addButton;
    private JButton displayButton;
    private JMenuItem add;
    private JMenuItem remove;
    private JMenuItem setfont;
    private JMenuItem menuHelp;

    private String breakPath(String s) {
	StringBuffer back = null;
	StringTokenizer tok = new StringTokenizer(s, File.pathSeparator);
	while (tok.hasMoreTokens()) {
	    String t = tok.nextToken();
	    if (back == null) {
		back = new StringBuffer();
	    } else {
		back.append("\n");
	    }
	    back.append(t);
	}
	return back.toString();
    }

    // try to locate the "demos" directory
    private File findDemos() {
	String cp = System.getProperty("java.class.path");
	StringTokenizer tok = new StringTokenizer(cp, File.pathSeparator);
	while (tok.hasMoreTokens()) {
	    String t = tok.nextToken();
	    if (t == null) {
		continue;
	    }
	    File ft = new File(t);
	    File f;
	    // It seems I cannot just do the else part on win32 ???? - epll
	    if (ft.isAbsolute()) {
		f = ft;
	    } else {
		f = new File(new File(System.getProperty("user.dir")), t);
	    }
	    String s = f.getAbsolutePath();

	    File p = new File(s);
	    while (p != null && ! p.getName().equals("demos")) {
                String ps = p.getParent(); 
		try {
		    p = new File(ps);
		} catch (Exception ex) {
		    p = null;
		}
	    }
	    if (p != null) {
		return p;
	    }
	}
	return null;
    }

    // intialize components

    private void initializeGUI() {
	Box topBox = Box.createVerticalBox();
	Box bottomBox;		// holds all the buttons
	JScrollPane sp;		// a handy Scroll Pane
	JButton b;

	hsList = new JList(new DefaultListModel());
	hsList.addListSelectionListener(new ListSelected());
	hsList.setBorder(BorderFactory.createLoweredBevelBorder()); 
	setListData(hsList, enumToArray(masterHS.getHelpSets()));

	JScrollPane scroll = new JScrollPane(hsList);
	scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
					  "Merged HelpSets"));

	bottomBox = Box.createHorizontalBox();

	addButton = b = new JButton("Add");
	b.setEnabled(true);
	b.addActionListener(new AddAction());
	bottomBox.add(b);
	
	removeButton = new JButton("Remove");
	removeButton.setEnabled(false);
	removeButton.addActionListener(new RemoveAction());
	bottomBox.add(removeButton);

	displayButton = new JButton("Display");
	displayButton.setEnabled(false);
	displayButton.addActionListener(new ShowAction());
	bottomBox.add(displayButton);

	topBox.add(Box.createRigidArea(new Dimension(0,10)));
	topBox.add(scroll);
	topBox.add(Box.createRigidArea(new Dimension(0,10)));
	topBox.add(bottomBox);
	topBox.add(Box.createRigidArea(new Dimension(0,10)));

	debug ("Creating Menus");
	this.setJMenuBar(createMenus());
	this.getContentPane().add(topBox);
    }

    public JMenuBar createMenus() {
	JMenuItem menuItem;
	JMenuBar menuBar = new JMenuBar();
	menuBar.setBackground(getBackground());
	//	menuBar.setOpaque(true);
	JMenu menu = new JMenu("File");
	CSH.setHelpIDString(menu, "menus.file");
	menu.setToolTipText("File operations");
	menuBar.add(menu);
	menuItem = addMenuItem(menu, "Exit", "file.exit");

	// Ask for confirmation on exit
	menuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e){
		System.exit(0);
	    }
	});

	menu = new JMenu("Edit");
	CSH.setHelpIDString(menu, "menus.edit");
	menuBar.add(menu);
	add = addMenuItem(menu, "Add", null);
	add.addActionListener(new AddAction());
	remove = addMenuItem(menu, "Remove", null);
	remove.addActionListener(new RemoveAction());
	remove.setEnabled(false);

	menu = new JMenu("Options");
	CSH.setHelpIDString(menu, "menus.option");
	menuBar.add(menu);
	setfont = addMenuItem(menu, "Set Font...", null);
	ActionListener setFontListener = new SetFontListener();
	setfont.addActionListener(setFontListener);

	JMenu help=new JMenu("Help");
        if((UIManager.getLookAndFeel().getName()).equals("CDE/Motif")){
            menuBar.add(Box.createGlue());
        }
        menuBar.add(help);
	menuHelp=new JMenuItem("Using Merge");
	CSH.setHelpIDString(menuHelp,"main");
	menuHelp.addActionListener(new CSH.DisplayHelpFromSource(mainHB));
	help.add(menuHelp);

	return menuBar;
    }

    private JMenuItem addMenuItem(JMenu menu, String label, String tipKey) {
	JMenuItem item = new JMenuItem(label);
	// This is wrong but resources aren't working so for no will just use 
	// the key (OK)
	menu.add(item);
	return item;
    }

    private JDialog addDialog;
    private JTextField helpSetName;
    private JTextField helpSetURL;
    private JTextArea helpSetURLs;


    // Creates the Add Dialog Box
    private void createAddDialog() {
	Box box;
	JPanel panel;

	addDialog = new JDialog (this, "Add ...");
	addDialog.setSize(new Dimension(450,400));
	Box topBox = Box.createVerticalBox();
	topBox.add(Box.createRigidArea(new Dimension(0,10)));

	box = Box.createHorizontalBox();
	box.add(Box.createRigidArea(new Dimension(15,0)));
	box.add(new JLabel("HelpSet Name: "));
	helpSetName = new JTextField("HolidayHistory.hs");
	helpSetName.setEditable(true);
	box.add(helpSetName);
	box.add(Box.createRigidArea(new Dimension(15,0)));
	topBox.add(box);
	topBox.add(Box.createRigidArea(new Dimension(0,5)));

	box = Box.createHorizontalBox();
	box.add(Box.createRigidArea(new Dimension(15,0)));
	box.add(new JLabel("HelpSet URL:  "));
	helpSetURL = new JTextField();
	box.add(helpSetURL);
	box.add(Box.createRigidArea(new Dimension(15,0)));
	topBox.add(box);
	topBox.add(Box.createRigidArea(new Dimension(0,5)));


	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
					  "Current Values"));

	box = Box.createHorizontalBox();
	box.add(new JLabel("CLASSPATH: "));
	JTextArea t = new JTextArea(4,40);
	JScrollPane sp = new JScrollPane();
	sp.getViewport().add(t);
	box.add(sp);
	t.insert(breakPath(System.getProperty("java.class.path")), 0);
	t.setEditable(false);
	panel.add(box);
	panel.add(Box.createVerticalStrut(5));

	box = Box.createHorizontalBox();
	box.add(new JLabel("Additional URLs: "));
	helpSetURLs = new JTextArea(5, 40);
	helpSetURLs.setEditable(false);
	sp = new JScrollPane();
	sp.getViewport().add(helpSetURLs);
	box.add(sp);
	panel.add(box);

	if (! on12) {
	    helpSetURLs.append("Dynamic addition of URLs not available in 1.1");
	    helpSetURL.setEnabled(false);
	} else {
	    helpSetURL.setEnabled(true);
	    File b1 = findDemos();
	    if (b1 == null) {
		helpSetURLs.append("Could not find demos directory");
	    } else {
		try {
		    // previous version of the file has cleaner code.  This one is a
		    // workaround to bug: 4157372
		    File b2 = new File(new File(b1, "hs"), "idehelp");
		    helpSetURLs.append("file:"+b2.getAbsolutePath()+File.separator);
		    helpSetURLs.append("\n");
		    b2 = new File(new File(b1, "hsjar"), "holidays.jar");
		    helpSetURLs.append("file:"+b2.getAbsolutePath());
		    helpSetURLs.append("\n");
		} catch (Exception e) {
		    helpSetURLs.setText("Caught exception: "+e);
		}
	    }
	}

	topBox.add(panel);
	topBox.add(Box.createRigidArea(new Dimension(0,5)));

	Box bottomBox = Box.createHorizontalBox();

	JButton b = new JButton("Add");
	b.setEnabled(true);
	b.addActionListener(new ListenAction());
	bottomBox.add(b);
	bottomBox.add(Box.createRigidArea(new Dimension(15,0)));
	
	b = new JButton("Cancel");
	b.addActionListener(new CancelAction());
	bottomBox.add(b);
	bottomBox.add(Box.createRigidArea(new Dimension(15,0)));

	b = new JButton("Help");
	bottomBox.add(b);
	CSH.setHelpIDString(b, "add");
	b.addActionListener(new CSH.DisplayHelpFromSource(mainHB));

	topBox.add(bottomBox);
	topBox.add(Box.createRigidArea(new Dimension(0,10)));

	addDialog.getContentPane().add(topBox);
    }

    private class AddAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (addDialog == null) {
		createAddDialog();
	    }
	    addDialog.show();
	}
    }

    private class CancelAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    addDialog.setVisible(false);
	}
    }

    private class RemoveAction implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		Object a[] = (Object[]) hsList.getSelectedValues();
		if (a == null || a.length==0) {
		    System.err.println("No selected values");
		} else {
		    debug("Removing... "+a[0]);
		    removeHelpSet((HelpSet) a[0]);
		}
	    }
	}

    private class LocateAction implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    }
	}

    private class ListenAction implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		ClassLoader cl;
		String newURL = helpSetURL.getText();
		String oldp = helpSetURLs.getText();
		helpSetURLs.append(newURL);
		helpSetURLs.append("\n");
		String p = helpSetURLs.getText();
		String n = (String) helpSetName.getText();
		if (on12) {
		    URL x[] = parseURLs(p);
		    cl = new URLClassLoader(x);
		} else {
		    cl = null;
		}
		URL url = HelpSet.findHelpSet(cl, n);
		if (url == null) {
		    JOptionPane.showMessageDialog(addDialog,
						  "HelpSet not found", 
						  "Error", 
						  JOptionPane.ERROR_MESSAGE);
		    helpSetURLs.setText(oldp);
		    return;
		}
		String u = url.toExternalForm();
		debug("hs: " + n);
		debug("url: " + u);
		debug("path: " + p);
		addHelpSet(n, u, p);
		addDialog.setVisible(false);
	    }
	}

    private class ShowAction implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
		Object a[] = (Object[]) hsList.getSelectedValues();
		if (a == null || a.length==0) {
		    System.err.println("No selected values");
		} else {
		    debug("Showing... "+a[0]);
		    HelpSet h = (HelpSet) a[0];
		    // Should really reuse the same broker, just set the HS.
		    HelpBroker hb = h.createHelpBroker();
		    if (font != null) {
			debug ("font=" + font);
			hb.setFont(font);
		    }
		    hb.setDisplayed(true);
		}
	    }
	}

    private class ListSelected implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
		debug("value changed: "+e);
		if (e.getValueIsAdjusting()) {
		    return;	// ignore
		}
		remove.setEnabled(false);
		removeButton.setEnabled(false);
		displayButton.setEnabled(false);
		// locate what entry has actually changed
		int sel[] = hsList.getSelectedIndices();
		for (int i=0 ; i<sel.length; i++) {
		    if (sel[i] > 0) {
			remove.setEnabled(true);
			removeButton.setEnabled(true);
			break;
		    }
		}
		displayButton.setEnabled(true);
	    }
	}


    // OLD code. Save for later use within a FileChooser
    private URL urlFromFileName(String fileName) throws MalformedURLException {
	URL url;
	URL cwd = new URL("file:"+System.getProperty("user.dir")+"/");

	// Now check if it is a directory to add the possibly missing "/"
	// that is needed for URLClassLoader to work

	boolean dir;
	File f = new File(fileName);
	if (! f.exists()) {
	    System.err.println("could not find file: "+f);
	    return null;
	}
	String p = f.getAbsolutePath();	// getAbsoluteFile() is a 1.2 method!
	f = new File(p);
	if (f.isDirectory()) {
	    if (! p.endsWith("/")) {
		url = new URL(cwd, p+"/");
	    } else {
		url = new URL(cwd, p);
	    }
	} else {
	    url = new URL(cwd, p);
	}
	return url;
    }


    // This takes URLs
    private URL[] parseURLs(String s) {
	Vector v = new Vector();
	StringTokenizer tok = new StringTokenizer(s, "\n");
	while (tok.hasMoreTokens()) {
	    String spec = (String) tok.nextToken();
	    try {
		URL url = new URL(spec);
		v.addElement(url);
	    } catch (Exception ex) {
		System.err.println("cannot create URL for "+spec);
	    }
	}
	URL back[] = new URL[v.size()];
	v.copyInto(back);
	return back;
    }

    /*
     * Get the ClassLoader of this class.  The spec of getClassLoader() has changed
     * recently; verify what is the current specification..
     */
    private ClassLoader getMyLoader() {
	ClassLoader back;
	back = this.getClass().getClassLoader();
	debug("current loader is "+back);
	return back;
    }

    /**
     * Add the selected HelpSet
     */

    private void addHelpSet(String name, String urlSpec, String path) {
	debug("Add HelpSet; name: "+name);
	debug("           ; url: "+urlSpec);
	debug("           ; name: "+path);

	HelpSet hs;
	try {
	    URL url = new URL(urlSpec);
	    if (on12) {
		URL urls[] = parseURLs(path);
		debug("ursl["+urls.length+"]");
		for (int i=0; i<urls.length; i++) {
		    debug(urls[i]+"");
		}

		URLClassLoader ucl = new URLClassLoader(urls);
		debug("ucl: "+ucl);
		hs = new HelpSet(ucl, url);
		debug("hs: "+hs);
		clTable.put(hs, ucl);
	    } else {
		hs = new HelpSet(null, url);
	    }
	    masterHS.add(hs);	// Add to the master HelpSet
	    setListData(hsList, enumToArray(masterHS.getHelpSets()));
	} catch (MalformedURLException ex) {
	    System.err.println("Could not create URL from "+urlSpec);
	} catch (HelpSetException ex) {
	    System.err.println("Could not create HelpSet for "+urlSpec);
	}
    }

    /**
     * Remove the selected HelpSet
     */

    private void removeHelpSet(HelpSet hs) {
	debug("Removing "+hs);
	if (!masterHS.remove(hs)) {
	    System.err.println("Warning: could not remove HelpSet "+hs);
	}
	setListData(hsList, enumToArray(masterHS.getHelpSets()));
    }

    /**
     * Get an array of Objects from an Enumeration
     */
    private Object[] enumToArray(Enumeration e) {
	Vector v = new Vector();
	while (e.hasMoreElements()) {
	    v.addElement(e.nextElement());
	}
	Object back[] = new Object[v.size()];
	v.copyInto(back);
	return back;
    }

    // WORKAROUND-land!

    private void setListData(JList list, Object[] data) {
	// equivalent to list.setListData(data)
	DefaultListModel m = (DefaultListModel)list.getModel();
	try {
	    m.removeAllElements();
	} catch (java.lang.IndexOutOfBoundsException ex) {
	    // ignore this exception. It's a bug in 1.2
	    // Turn off the special buttons since it won't happen otherwise
	    remove.setEnabled(false);
	    removeButton.setEnabled(false);
	    displayButton.setEnabled(false);
	}
	
	debug("setListData:");
	// Always add the master help Set first
	debug("  "+masterHS);
	m.addElement(masterHS);

	// Add anyother helpsets
	for (int i=0; i<data.length; i++) {
	    debug("  "+data[i]);
	    m.addElement(data[i]);
	}
    }


    /**
     * No arguments main
     */
    public static void main(String args[]) {

 	Merge m = new Merge();
	m.setResizable(false);
	m.pack();
	m.show();
    }

    private class SetFontListener implements ActionListener, 
	TreeSelectionListener, ItemListener{
	JDialog sfDialog = null;
	JComboBox cb;
	JTree fontTree;
	JTextArea preview;
	JButton okButton;
	JButton cancelButton;
	Font sffont;

	public void actionPerformed(ActionEvent e) {
	    try {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    } catch (NoClassDefFoundError err) {
		JOptionPane.showMessageDialog(null, 
					      "Setting Fonts on JDK1.1 not allowed",
					      "Set Font...", 
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    if (sfDialog == null) {
		sfDialog = new JDialog(Merge.this,
				       "Set Font...", false);
		initSetFontComponents();
		sfDialog.pack();
		
		cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			sfDialog.setVisible(false);
			sfDialog.dispose();
		    }
		});
		
		okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			Merge.this.font = sffont;
			debug ("sffont=" + font);
			sfDialog.setVisible(false);
			sfDialog.dispose();
		    }
		});
		
	    }
	    sffont = Merge.this.font;
	    if (sffont != null) {
		preview.setFont(sffont);
	    }
	    sfDialog.show();
	}

	private void initSetFontComponents() {
		    
	    Border border;
	    Vector fontFamilyNode = new Vector();
	    DefaultMutableTreeNode topNode = new DefaultMutableTreeNode();
	    Font [] fonts;

	    // Get the fonts
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    fonts = ge.getAllFonts();
	    for (int i = 0 ; i < fonts.length ; i++) {
		DefaultMutableTreeNode node = 
		    new DefaultMutableTreeNode(new MyFont(fonts[i]));
		String family = fonts[i].getFamily();
		String testFamily = null;
		DefaultMutableTreeNode test = null;
		for (Enumeration e = topNode.children() ;
		     e.hasMoreElements() ; ) {
		    test = (DefaultMutableTreeNode) e.nextElement();
		    testFamily = (String) test.getUserObject();
		    if (testFamily.compareTo(family) == 0) {
			break;
		    }
		    test = null;
		}
		if (test == null) {
		    DefaultMutableTreeNode parent = 
			new DefaultMutableTreeNode(family);
		    topNode.add(parent);
		    parent.add(node);
		} else {
		    test.add(node);
		}
	    }

	    Box topBox = Box.createVerticalBox();

	    Box upperBox = Box.createHorizontalBox();
	    fontTree = new JTree(topNode);
	    fontTree.setShowsRootHandles(true);
	    fontTree.setRootVisible(false);
	    TreeSelectionModel tsm = fontTree.getSelectionModel();
	    tsm.addTreeSelectionListener(this);
	    JScrollPane sp = new JScrollPane();
	    sp.getViewport().add(fontTree);
	    border = BorderFactory.createTitledBorder("Fonts");
	    sp.setBorder(border);
	    upperBox.add(Box.createHorizontalStrut(5));
	    upperBox.add(sp);
	    upperBox.add(Box.createHorizontalStrut(5));

	    Box box1 = Box.createHorizontalBox();
	    cb = new JComboBox();
	    border = BorderFactory.createTitledBorder("Size");
	    cb.setBorder(border);
	    cb.setEditable(true);
	    cb.addItem("8");
	    cb.addItem("9");
	    cb.addItem("10");
	    cb.addItem("11");
	    cb.addItem("12");
	    cb.addItem("13");
	    cb.addItem("14");
	    cb.addItem("16");
	    cb.addItem("18");
	    cb.addItem("20");
	    cb.addItem("24");
	    cb.addItem("28");
	    cb.addItem("32");
	    cb.addItem("36");
	    cb.addItem("48");
	    cb.addItem("72");
	    cb.setSelectedItem("12");
	    cb.addItemListener(this);
	    box1.add(Box.createHorizontalStrut(5));
	    box1.add(cb);
	    box1.add(Box.createHorizontalStrut(5));
	    upperBox.add(box1);
	    upperBox.add(Box.createHorizontalStrut(5));

	    topBox.add(Box.createVerticalStrut(10));
	    topBox.add(Box.createVerticalStrut(5));
	    topBox.add(upperBox);

	    Box bottomBox = Box.createHorizontalBox();
	    JPanel prepanel = new JPanel();
	    border = BorderFactory.createTitledBorder("Preview");
	    prepanel.setBorder(border);
	    preview = new JTextArea("\nAaBbCc...XxYyZz\n");
	    prepanel.add(preview);
	    bottomBox.add(Box.createHorizontalStrut(5));
	    bottomBox.add(prepanel);
	    bottomBox.add(Box.createHorizontalStrut(5));

	    topBox.add(Box.createVerticalStrut(10));
	    topBox.add(bottomBox);

	    Box box2 = Box.createHorizontalBox();
	    okButton = new JButton("OK");
	    cancelButton = new JButton("Cancel");

	    box2.add(okButton);
	    box2.add(cancelButton);

	    Box box6 = Box.createHorizontalBox();
	    box6.add(Box.createHorizontalStrut(5));
	    box6.add(new JSeparator());
	    box6.add(Box.createHorizontalStrut(5));

	    topBox.add(Box.createVerticalStrut(10));
	    topBox.add(box6);
	    topBox.add(box2);
	    sfDialog.getContentPane().add(topBox);
	}

	public void valueChanged(TreeSelectionEvent e) {
	    debug("ValueChanged: "+e);

	    TreePath path = fontTree.getSelectionPath();
	    // If the path is null then the selection has been removed.
	    if (path == null) {
		return;
	    }

	    DefaultMutableTreeNode node = 
		(DefaultMutableTreeNode) path.getLastPathComponent();
	
	    // only get set the font "Font" objects
	    Object obj = node.getUserObject();
	    if (obj instanceof MyFont) {
		MyFont myf = (MyFont) obj;
		String size = (String)cb.getSelectedItem();
		sffont = myf.getFont().deriveFont(Integer.valueOf(size).floatValue());
		
		preview.setFont(sffont);
	    }
	}

	// The size of the Font changed
	public void itemStateChanged(ItemEvent e) {
	    debug("ValueChanged: "+e);

	    String size = (String)cb.getSelectedItem();
	    sffont = font.deriveFont(Integer.valueOf(size).floatValue());
	    
	    preview.setFont(sffont);
	}
    }

    private class MyFont {
	private Font f;

	public MyFont (Font f) {
	    this.f = f;
	}

	public Font getFont() {
	    return f;
	}

	public String toString() {
	    String family = f.getFamily();
	    String fname = f.getFontName();
	    String name = fname.substring(family.length());
	    if (name.length() == 0) {
		name = "Plain";
	    }
	    return name;
	}
    }

    /*
     * Debug code
     */
    private final static boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("Merge: "+msg);
	}
    }
}
