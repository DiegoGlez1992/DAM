/*
 * @(#) JHLauncher.java 1.44 - last change made 10/22/04
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

import java.util.Locale;
import java.awt.*;
import java.awt.event.*;
import javax.help.*;
import javax.swing.border.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.security.AccessControlException;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * This class displays JavaHelp HelpSets.
 *
 * @author Eduardo Pelegri-Llopart
 * @author Roger D. Brinkley
 * @version	1.44	10/22/04
 */

public class JHLauncher {

    private static JFrame frame;

    // The initial width and height of the frame
    public static int WIDTH = 645;
    public static int HEIGHT = 495;

    protected static boolean debug = false;
    protected static boolean setHS = false;
    private static boolean on12;
    private static Font fonts [];

    private static JHelp jh = null;
    HelpSet hs = null;
    private JFrame elementTreeFrame;

    private static String hsName = null; // name for the HelpSet 
    private static String hsPath = null; // URL spec to the HelpSet
    private static String id = null;

    public JHLauncher() {
    }

    protected void initialize(String name, ClassLoader loader) {
	URL url = HelpSet.findHelpSet(loader, name, "", Locale.getDefault());
	if (url == null) {
	    url = HelpSet.findHelpSet(loader,name, ".hs", Locale.getDefault());
	    if (url == null) {
		// could not find it!
		JOptionPane.showMessageDialog(null,
					      "HelpSet not found", 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	}
	initialize(url, loader);
    }

    protected void initialize(URL url, ClassLoader loader) {
	try {
	    hs = new HelpSet(loader, url);
	} catch (Exception ee) {
	    JOptionPane.showMessageDialog(null,
					  "HelpSet not found", 
					  "Error", 
					  JOptionPane.ERROR_MESSAGE);
	    return;
	}
	// The JavaHelp can't be added to a BorderLayout because it
	// isnt' a component. For this demo we'll use the embeded method
	// since we don't want a Frame to be created.

	jh = new JHelp(hs);
    }

    private static String spec = "";

    private class OpenPageListener implements ActionListener {
	JDialog opDialog = null;
	JButton okButton;
	JButton cancelButton;
	JButton chooseFileButton;
	JTextField page;

	public void actionPerformed(ActionEvent e) {
	    if (opDialog == null) {
		opDialog = new JDialog(JHLauncher.this.getFrame(),
				       "Open Page", false);
		initOpenPageComponents();
		opDialog.pack();
		
		cancelButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			opDialog.setVisible(false);
			opDialog.dispose();
		    }
		});
		
		okButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			try {
			    URL url = new URL(page.getText());
			    jh.setCurrentURL(url);
			} catch (MalformedURLException ee) {
			}
			opDialog.setVisible(false);
			opDialog.dispose();
		    }
		});
		
		chooseFileButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(jh) == 0) {
			    File theFile = fc.getSelectedFile();
			    if (theFile != null) {
				String path = theFile.getPath();
				if (path.startsWith("//")) {
				    path = path.substring(1);
				}
				page.setText(new String("file:" + path));
			    }
			}
		    }
		});
		
	    }
	    opDialog.show();
	}
	private void initOpenPageComponents() {
		    
				// playing with Boxes
	    Box topBox = Box.createVerticalBox();

	    Box box3 = Box.createHorizontalBox();
	    JLabel introLabel = new JLabel("Enter the WWW location (URL) or specify the local file you would like to open");
	    box3.add(Box.createHorizontalStrut(5));
	    box3.add(introLabel);
	    box3.add(Box.createHorizontalStrut(5));

	    topBox.add(Box.createVerticalStrut(5));
	    topBox.add(box3);

	    Box box1 = Box.createHorizontalBox();
	    JLabel findLabel = new JLabel("URL: ");
	    page = new JTextField(20);
	    page.setEditable(true);
	    chooseFileButton = new JButton("Choose File...");
	    chooseFileButton.setAlignmentY(0.5f);
	    box1.add(Box.createHorizontalStrut(5));
	    box1.add(findLabel);
	    box1.add(page);
	    box1.add(Box.createHorizontalStrut(5));
	    box1.add(chooseFileButton);
	    box1.add(Box.createHorizontalStrut(5));

	    topBox.add(Box.createVerticalStrut(10));
	    topBox.add(box1);

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
	    opDialog.getContentPane().add(topBox);
	}
    }


    private class SetFontListener implements ActionListener, 
	TreeSelectionListener, ItemListener{
	JDialog sfDialog = null;
	JComboBox cb;
	JTree fontTree;
	JTextArea preview;
	JButton okButton;
	JButton cancelButton;
	Font font;

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
		sfDialog = new JDialog(JHLauncher.this.getFrame(),
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
			jh.setFont(font);
			sfDialog.setVisible(false);
			sfDialog.dispose();
		    }
		});
		
	    }
	    font = jh.getFont();
	    preview.setFont(font);
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
		font = myf.getFont().deriveFont(Integer.valueOf(size).floatValue());
		
		preview.setFont(font);
	    }
	}

	// The size of the Font changed
	public void itemStateChanged(ItemEvent e) {
	    debug("ValueChanged: "+e);

	    String size = (String)cb.getSelectedItem();
	    font = font.deriveFont(Integer.valueOf(size).floatValue());
	    
	    preview.setFont(font);
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

    /**
     * Action that brings up a JFrame with a JTree showing the structure
     * of the document.
     */
    class ShowElementTreeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
	    elementTreeFrame = null;
	    if(elementTreeFrame == null) {
		// Create a frame containing an instance of 
		// ElementTreePanel.
		try {
		    String    title = "Element Tree For Current Document";
		    elementTreeFrame = new JFrame(title);
		} catch (MissingResourceException mre) {
		    elementTreeFrame = new JFrame();
		}

		elementTreeFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent weeee) {
			elementTreeFrame.setVisible(false);
		    }
		});
		Container fContentPane = elementTreeFrame.getContentPane();

		fContentPane.setLayout(new BorderLayout());
		fContentPane.add(new ElementTreePanel(getEditor()));
		elementTreeFrame.pack();
	    }
	    elementTreeFrame.show();
	}
    }

    /**
     * MenuBar
     */
    private JMenuBar createMenuBar() {
	// MenuBar
	JMenuBar menuBar = new JMenuBar();

	JMenuItem mi;

	// File Menu
	JMenu file = (JMenu) menuBar.add(new JMenu("File"));
        file.setMnemonic('F');

	mi = (JMenuItem) file.add(new JMenuItem("Open page"));
	ActionListener openPageListener = new OpenPageListener();
	mi.addActionListener(openPageListener);

	if (setHS) {
	    mi = (JMenuItem) file.add(new JMenuItem("Set HelpSet"));
	    mi.setMnemonic('s');
	    mi.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		if (selectionDialog == null) {
		    initializeSDGUI();
		}
		showSD();
		}
	    });
	}

	mi = (JMenuItem) file.add(new JMenuItem("Exit"));
	mi.setMnemonic('x');
	mi.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
	    }
	});


	// Option Menu
	JMenu options = (JMenu) menuBar.add(new JMenu("Options"));
        options.setMnemonic('O');

	mi = (JMenuItem) options.add(new JMenuItem("Set Font..."));
	ActionListener setFontListener = new SetFontListener();
	mi.addActionListener(setFontListener);

	if (debug) {
	    mi = (JMenuItem) options.add(new JMenuItem("Show Element Tree"));
	    ActionListener elementTreeListener = new ShowElementTreeListener();
	    mi.addActionListener(elementTreeListener);
	}

	return menuBar;
    }

    public Frame getFrame() {
	return frame;
    }

    private JTextComponent getEditor() {
	JHelpContentViewer viewer = jh.getContentViewer();
	JScrollPane sp = (JScrollPane) viewer.getComponent(0);
	JViewport vp = sp.getViewport();
	return (JTextComponent) vp.getView();
    }

    /**
     * We support two arguments; one of the two should be present (for now)
     * Later on, we will add a JHLauncher.data file, to be got using
     * getResource() that will have HelpSet, etc, etc.
     *
     * -helpset "helpset"	the HelpSet to look using the existing classloader
     * -classpath "classpath" 	A class path to use (default is the ClassLoader of JHLauncher)
     * -hsURL "URL to helpset" Where to look for the HelpSet.
     */
    public static void main(String args[]) {
        boolean canContinue = true;
	String version = System.getProperty("java.version");
        if (version.startsWith("1.0")) {
            System.out.println("!!!WARNING: JavaHelp & Swing must be run" +
                               "with JDK 1.1.2 or higher version VM!!!");
        }

	try {
	    // Add the following code if you want the Look and Feel
	    // to be set to the Look and Feel of the native system.
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} 
	catch (Exception e) { 
	}
	
	// Code to activate to try out our EditorKit
	//
	//	JEditorPane.registerEditorKitForContentType(
	//			"text/html",
	//			"javax.help.impl.JHEditorKit",
	//			JHLauncher.class.getClassLoader());


	try {
	    Class c;
	    c = Class.forName("javax.swing.JComponent");
	} catch (ClassNotFoundException e) {
	    Frame frame = new Frame("Error");
	    TextArea text = new TextArea("Java Foundation Classes not found.\n\nThe program can only be run using JDK 1.2 and higher.\nIf you are using JDK 1.1 use the hsviewer1_1 utility", 4, 53, TextArea.SCROLLBARS_NONE );
	    frame.add(text);
	    WindowListener l = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    System.exit(0);
		}
		public void windowClosed(WindowEvent e) {
		    System.exit(0);
		}
	    };
	    frame.addWindowListener(l);
	    frame.pack();
	    frame.show();
            canContinue = false;
	}
        if(canContinue){
            JHLauncher launcher = new JHLauncher();
	    launcher.setup(args);
        }
    }

    /**
     * Parse the arguments
     */
    private String[] shiftArgs(String args[], int step) {
	int count = args.length;
	String back[] = new String[count-step];
	for (int i=0; i<count-step; i++) {
	    back[i] = args[i+step];
	}
	return back;
    }

    // It doesn't really parse the URLs it just creates a URL from a string
    // and then puts it into a URL array
    private static URL[] parseURLs(String spec) {
	Vector v = new Vector();
	try {
	    URL url = new URL(spec);
	    v.addElement(url);
	} catch (Exception ex) {
	    System.err.println("cannot create URL for "+spec);
	}
	URL back[] = new URL[v.size()];
	v.copyInto(back);
	return back;
    }

    private void setup(String args[]) {
	ClassLoader loader = this.getClass().getClassLoader();
	String path = null;	// the classpath to use (1.2-only)

	URL hsURL = null;
	String hsSpec = null;

	// Get the arguments
	while (args.length > 0) {
	    if (args[0].equals("-helpset")) {
		// Name of HelpSet.  Look for it in the classpath
		args = shiftArgs(args, 1);
		if (on12) { 
		   File file = new File(args[0]);
		   handleHSFile(file);
		   URL urls[] = parseURLs(hsPath);
		   // This is a 1.2 only feature.
		   try {
		      loader = URLClassLoader.newInstance(urls, loader);
		   } catch (NoClassDefFoundError err) {
		   } catch (NoSuchMethodError err) {
		   }
		} else {
		   // on 1.1 systems just take the arg as a name
		   hsName = args[0];
		}
		args = shiftArgs(args, 1);
	    } else if (args[0].equals("-classpath")) {
		// This is a 1.2-only feature
		args = shiftArgs(args, 1);
		String hspath = args[0];
		if (hspath.startsWith("//")) {
		    hspath = hspath.substring(1);
		}

		if (hspath.startsWith("..")) {
		    String cpath = System.getProperty("user.dir");
		    hspath = cpath + File.separator + hspath;
		}

		hspath = new String("file:" + hspath);
		URL urls[] = parseURLs(hspath);
		args = shiftArgs(args, 1);
		try {
		    loader = URLClassLoader.newInstance(urls, loader);
		} catch (NoClassDefFoundError err) {
		    System.err.println("-classpath not supported in 1.1");
		} catch (NoSuchMethodError err) {
		    System.err.println("-classpath not supported in 1.1");
		}
	    } else if (args[0].equals("-hsURL")) {
		// This is the given URL for the HelpSet
		args = shiftArgs(args, 1);
		hsSpec = args[0];
		args = shiftArgs(args, 1);
	    } else if (args[0].equals("-debug")) {
		debug = true;
		args = shiftArgs(args, 1);
	    } else if (args[0].equals("-ID")) {
		// This is the given URL for the HelpSet
		args = shiftArgs(args, 1);
		id = args[0];
		args = shiftArgs(args, 1);
	    } else if (args[0].equals("-contentViewer")) {
		// Use the native browser
		args = shiftArgs(args, 1);
		SwingHelpUtilities.setContentViewerUI(args[0]);
		args = shiftArgs(args, 1);
	    } else {
		usage();
	    }
	}

	if (hsSpec != null) {
	    try {
		debug ("hsSpec=" + hsSpec);
		hsURL = new URL(hsSpec);
	    } catch (Exception ex) {
		usage("Invalid URL spec for HelpSet");
	    }
	}

	if (hsName != null && hsURL != null) {
	    usage();
	}

	if (hsName == null && hsURL == null) {
	    URL resource;
	    if (loader == null) {
		resource = ClassLoader.getSystemResource("JHLauncher.data");
	    } else {
		resource = loader.getResource("JHLauncher.data");
	    }
	    if (resource != null) {
		System.err.println("Found JHLauncher.data at "+resource);
	    } else {
		// OK, get data from the user in the selectionDialog
		setHS = true;
		initializeSDGUI();
		showSD();
		return;
	    }
	}

	if (debug) {
	    debug("hsName: "+hsName);
	    debug("hsURL: "+hsURL);
	    debug("loader: "+loader);
	}

	if (hsURL != null) {
	    initialize(hsURL, loader);
	} else {
	    initialize(hsName, loader);
	}
	if (hs == null) {
	    setHS = true;
	    initializeSDGUI();
	    showSD();
	    return;
	}
	if (id != null) {
	    try {
		jh.setCurrentID(id);
	    } catch (BadIDException ex) {
		System.out.println("ID "+ id + " doesn't exist in Helpset");
	    }
	}
	createFrame(null, null); // defaults
	launch();
    }

    // selection dialog code
    private JDialog selectionDialog=null;
    private JTextField helpSetName;
    private JTextField helpSetURL;

    private void showSD() {
	if (selectionDialog != null) {
	    selectionDialog.pack();
	    selectionDialog.show();
	}
    }

    private void initializeSDGUI() {
	Box box;
	JPanel panel;

	if (!on12) {
	    JOptionPane.showMessageDialog(null,
					  "Cannot set the HelpSet on JDK1.1 Platforms", 
					  "Error", 
					  JOptionPane.ERROR_MESSAGE);
	    if (jh == null) 
		System.exit(0);
	    else
		return;
	}

	selectionDialog = new JDialog ((Frame)null, "Set HelpSet...", true);
	Box topBox = Box.createVerticalBox();

	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
					 "HelpSet Information"));

	box = Box.createHorizontalBox();
	box.add(Box.createRigidArea(new Dimension(15,0)));
	box.add(new JLabel("HelpSet Name: "));
	helpSetName = new JTextField("HolidayHistory.hs", 40);
	helpSetName.setEditable(true);
	box.add(helpSetName);
	box.add(Box.createRigidArea(new Dimension(15,0)));
	panel.add(box);
	panel.add(Box.createVerticalStrut(5));

	box = Box.createHorizontalBox();
	box.add(Box.createRigidArea(new Dimension(15,0)));
	box.add(new JLabel("HelpSet URL:  "));
	helpSetURL = new JTextField(40);
	box.add(helpSetURL);
	box.add(Box.createRigidArea(new Dimension(15,0)));
	JButton b = new JButton("Browse...");
	b.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(selectionDialog) == 0) {
		    File theFile = fc.getSelectedFile();
		    if (theFile != null) {
			handleHSFile(theFile);
			helpSetURL.setText(hsPath);
			helpSetName.setText(hsName);
		    }
		}
	    }
	});
	box.add(b);
	box.add(Box.createRigidArea(new Dimension(15,0)));
	panel.add(box);
	panel.add(Box.createVerticalStrut(5));

	Box bottomBox = Box.createHorizontalBox();

	b = new JButton("Display");
	b.setEnabled(true);
	b.addActionListener(new DisplayAction());
	bottomBox.add(b);
	bottomBox.add(Box.createRigidArea(new Dimension(15,0)));
	
	b = new JButton("Cancel");
	b.addActionListener(new CancelAction());
	bottomBox.add(b);
	bottomBox.add(Box.createRigidArea(new Dimension(15,0)));

	// later
	//b = new JButton("Help");
	//bottomBox.add(b);
	//CSH.setHelpIDString(b, "help");
	//	    b.addActionListener(new CSH.DisplayHelpFromSource(mainHB));

	topBox.add(Box.createRigidArea(new Dimension(0,10)));
	topBox.add(panel);
	topBox.add(Box.createRigidArea(new Dimension(0,10)));
	topBox.add(bottomBox);
	topBox.add(Box.createRigidArea(new Dimension(0,10)));

	selectionDialog.getContentPane().add(topBox);
    }

    private void handleHSFile(File hsFile) {
	String path=null;
	hsName = hsFile.getName();
	if (hsName.endsWith(".jar")) {
	    // jar file
	    path = hsFile.getPath();
	    hsName = "";
	    try {
		JarFile jar = new JarFile(hsFile);
		Enumeration entries = jar.entries();
		while (entries.hasMoreElements()) {
		    ZipEntry entry = (ZipEntry) entries.nextElement();
		    String entryName = entry.getName();
		    if (entryName.endsWith(".hs")) {
			hsName = entryName;
			break;
		    }
		}
	    } catch (IOException ee) {
				// ingore it
	    }
	} else {
	    path = hsFile.getParent();
	    if (path == null) {
		path = File.separator;
	    } else {
		path = path.concat(File.separator);
	    }
	}
	if (path.startsWith("//")) {
	    path = path.substring(1);
	}

	if (path.startsWith("..")) {
	    String cpath = System.getProperty("user.dir");
	    path = cpath + File.separator + path;
	}

	hsPath = new String("file:" + path);
    }

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

    private class CancelAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    if (frame == null) {
		System.exit(0);
	    }
	    selectionDialog.setVisible(false);
	}
    }

    private class DisplayAction implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    ClassLoader cl;
	    HelpSet hs = null;
	    String path = helpSetURL.getText();
	    String name = (String) helpSetName.getText();
	    if (on12) {
		URL x[] = parseURLs(path);
		cl = new URLClassLoader(x);
	    } else {
		cl = null;
	    }
	    URL url = HelpSet.findHelpSet(cl, name);
	    if (url == null) {
		JOptionPane.showMessageDialog(selectionDialog,
					      "HelpSet not found", 
					      "Error", 
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }

	    try {
		hs = new HelpSet(cl, url);
	    } catch (HelpSetException ex) {
		System.err.println("Could not create HelpSet for "+url);
	    }
	    if (jh == null) {
		jh = new JHelp(hs);
	    } else {
		jh.setHelpSetPresentation(hs.getDefaultPresentation());
		DefaultHelpModel m = new DefaultHelpModel(hs);
		jh.setModel(m);
	    }
	    createFrame (hs.getTitle(), null);
	    launch();
	    selectionDialog.hide();
	}
    }

    private String title = "";

    public void setTitle(String s) {
	title = s;
    }

    public String getTitle() {
	return title;
    }

    protected JFrame createFrame(String title, JMenuBar bar) {
	if (jh == null) return null;
	if (title == null || title.equals("")) {
	    TextHelpModel m = jh.getModel();
	    HelpSet hs = m.getHelpSet();
	    String hsTitle = hs.getTitle();
	    if (hsTitle == null || hsTitle.equals("")) {
		setTitle("Unnamed HelpSet"); // maybe based on HS?
	    } else {
		setTitle (hsTitle);
	    }
	} else {
	    setTitle(title);
	}
	if (frame == null) {
	    WindowListener closer = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    System.exit(0);
		}
		public void windowClosed(WindowEvent e) {
		    System.exit(0);
		}
	    };

	    frame = new JFrame(getTitle());
	    frame.setSize(WIDTH, HEIGHT);
	    frame.setForeground(Color.black);
	    frame.setBackground(Color.lightGray);
	    frame.addWindowListener(closer);
	    frame.getContentPane().add(jh);	// the JH panel
	    if (bar == null) {
		bar = createMenuBar();
	    }
	    frame.setJMenuBar(bar);
	} else {
	    frame.setTitle(getTitle());
	}
	frame.pack();
	return frame;
    }

    // HERE -- This needs some revisiting... epll.

    protected void setMenuBar(JMenuBar bar) {
	frame.setJMenuBar(bar);
    }
    
    protected void launch() {
	if (frame == null) return;
	frame.setVisible(true);
    }

    protected void usage() {
	usage(null);
    }

    protected void usage(String msg) {
	if (msg != null) {
	    System.err.println("JHLauncher: "+msg);
	}
	System.err.println("Usage: [-helpset name | -classpath path | -hsURL spec | -ID id | -contentViewer viewerclass]");
	System.exit(1);
    }


    static protected void debug(String msg) {
	if (debug) {
	    System.err.println("JHLauncher: "+msg);
	}
    }

    // static initialization
    static {
	try {
	    AccessControlException ex = new AccessControlException("");
	    on12 = true;
	} catch (NoClassDefFoundError ex) {
	    on12 = false;
	}
    }

}

