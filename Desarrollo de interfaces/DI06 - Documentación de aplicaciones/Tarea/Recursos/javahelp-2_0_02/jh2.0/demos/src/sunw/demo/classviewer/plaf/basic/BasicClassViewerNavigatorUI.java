/*
 * @(#) BasicClassViewerNavigatorUI.java 1.42 - last change made 10/22/04
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

package sunw.demo.classviewer.plaf.basic;

import javax.help.*;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.HelpUI;
import javax.help.plaf.basic.BasicHelpUI;
import javax.help.plaf.basic.BasicIndexNavigatorUI;
import javax.help.plaf.basic.BasicTOCCellRenderer;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import com.sun.java.help.impl.*;
import java.util.Locale;
import java.util.EventObject;
import java.util.Stack;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import sunw.demo.classviewer.ClassViewerNavigator;
import javax.help.Map.ID;

/**
 * This class is the ClassViewer navigator for the basic UI type.
 *
 * @author Paul Dumais
 * @author Eduardo Pelegri-Llopart
 * @author Roger Brinkley
 * @version	1.42	10/22/04
 */

public class BasicClassViewerNavigatorUI extends HelpNavigatorUI
implements HelpModelListener, TreeSelectionListener, ParserListener,
PropertyChangeListener
{
    protected ClassViewerNavigator treenav;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    protected JTree classtree;
    protected JScrollPane sp;
    protected boolean startedtoc;
    protected boolean startedfields;
    protected boolean startedmethods;
    protected boolean startedconstructors;
    protected boolean startedclass;
    protected Stack nodeStack;
    protected Stack itemStack;
    protected Stack tagStack;
    private Locale defaultLocale;
    private Locale lastLocale;
   

    public static ComponentUI createUI(JComponent x) {
        return new BasicClassViewerNavigatorUI((ClassViewerNavigator) x);
    }
    
    public BasicClassViewerNavigatorUI(ClassViewerNavigator b) {
	setIcon(UIManager.getIcon("TOCNav.icon"));
    }

    public void installUI(JComponent c) {
	debug("BasicClassViewerNavigatorUI.installUI("+c+")-begin");

	treenav = (ClassViewerNavigator)c;
	HelpModel helpModel = treenav.getModel();

	treenav.setLayout(new GridLayout(2,1));
	treenav.addPropertyChangeListener(this);
	if (helpModel != null) {
	    helpModel.addHelpModelListener(this); // changes to our model
	}

	topNode = new DefaultMutableTreeNode();

	tree = new JTree(topNode);
	//	tree.setBackground(Color.white);
	TreeSelectionModel tsm = tree.getSelectionModel();
	tsm.addTreeSelectionListener(this);
	tree.setShowsRootHandles(true);
	tree.setRootVisible(false);

	sp = new JScrollPane();
	sp.getViewport().add(tree);

	treenav.add(sp);

	classtree = new JTree();
	classtree.setModel(null);
	TreeSelectionModel tsm2 = classtree.getSelectionModel();
	tsm2.addTreeSelectionListener(this);
	JScrollPane sp2 = new JScrollPane();
	sp2.getViewport().add(classtree);
	treenav.add(sp2);
	classtree.setShowsRootHandles(true);
	classtree.setRootVisible(false);

	reloadData();

	debug("BasicClassViewerNavigatorUI.installUI("+c+")-end");
    }

    public void uninstallUI(JComponent c) {
	HelpModel helpModel = treenav.getModel();

	treenav.removePropertyChangeListener(this);
	TreeSelectionModel tsm = tree.getSelectionModel();
	tsm.removeTreeSelectionListener(this);
	treenav.setLayout(null);
	treenav.removeAll();

	if (helpModel != null) {
	    helpModel.removeHelpModelListener(this);
	}

	treenav = null;
    }

    /**
     * Merge in the navigational data from another NavigatorView. Not
     * implemented in Search Navigator.
     */

    public void merge(NavigatorView view) {
	debug("merging "+view);
    }

    /**
     * Remove the navigational data from another NavigatorView. Not
     * implemented in SearchNavigator
     */

    public void remove(NavigatorView view) {
	debug("removing "+view);
    }

    public Dimension getPreferredSize(JComponent c) {
	/*
	if (sp != null) {
	    return ((ScrollPaneLayout)sp.getLayout()).preferredLayoutSize(sp);
	} else {
	    return new Dimension(200,100);
	}
	*/
	return new Dimension(200,100);
    }

    public Dimension getMinimumSize(JComponent c) {
	    return new Dimension(100,100);
    }

    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    public void reloadData() {
	debug("reloadData");

	topNode.removeAllChildren();
	parseTOC();
	DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
	treeModel.reload();
	setVisibility(topNode);

	Map map = treenav.getModel().getHelpSet().getCombinedMap();
        tree.setCellRenderer(new BasicTOCCellRenderer(map));
	classtree.setCellRenderer(new BasicTOCCellRenderer(map));
    }

    private void setVisibility (DefaultMutableTreeNode node) {
	int max = node.getChildCount();
	for (int i=0; i<max; i++) {
	    DefaultMutableTreeNode subnode = 
		(DefaultMutableTreeNode)node.getChildAt(i);
	    tree.expandPath(new TreePath(subnode.getPath()));
	}
    }

    public void changeClass(ID classID) {

	DefaultMutableTreeNode classTopNode = new DefaultMutableTreeNode();

	BufferedInputStream in;
	HelpModel helpModel = treenav.getModel();
	nodeStack = new Stack();
	nodeStack.push(classTopNode);
	itemStack = new Stack();
	tagStack = new Stack();

	// this might look strange but the spec doesn't say what the fallback 
	// for lang is so we'll make it null and not do anything when it is 
	//set.	
	defaultLocale = null;
	lastLocale = null;

	Reader src;
	try {
	    HelpSet hs = helpModel.getHelpSet();
	    URL url = hs.getCombinedMap().getURLFromID(classID);

	    URLConnection uc = url.openConnection();
	    src = XmlReader.createReader(uc);
	    Parser p = new Parser (src);
	    p.addParserListener(this);
	    p.parse();
	    src.close();
	} catch (Exception e) {
	    debug ("exception thrown" + e.toString());
	    e.printStackTrace();
	    return;
	}

	tagStack = null;
	itemStack = null;

	classtree.setModel(new DefaultTreeModel(classTopNode));
	classtree.expandRow(0);
	classtree.setSelectionRow(1);
    }

    public void fillFields(DefaultMutableTreeNode top) {
	top.add(new DefaultMutableTreeNode("Field1"));
    }

    public void fillConstructors(DefaultMutableTreeNode top) {
	top.add(new DefaultMutableTreeNode("Constructor1"));
    }

    public void fillMethods(DefaultMutableTreeNode top) {
	top.add(new DefaultMutableTreeNode("Method1"));
    }


    private void parseTOC() {
	HelpModel helpModel = treenav.getModel();
	if (helpModel == null) {
	    return;
	}

	HelpSet hs = helpModel.getHelpSet();
	NavigatorView view = treenav.getNavigatorView();

	debug ("parseTOC - " + view.getName());

	Hashtable params = view.getParameters();
	URL url;

	try {
	    url = new URL(hs.getHelpSetURL(), (String) params.get("data"));
	} catch (Exception ex) {
	    throw new Error("Trouble getting URL to TOC data; "+ex);
	}

	BufferedInputStream in;

	nodeStack = new Stack();
	nodeStack.push(topNode);
	tagStack = new Stack();
	itemStack = new Stack();

	// this might look strange but the spec doesn't say what the fallback 
	// for lang is so we'll make it null and not do anything when it is 
	//set.	
	defaultLocale = null;
	lastLocale = null;

	Reader src;
	try {
	    URLConnection uc = url.openConnection();
	    src = XmlReader.createReader(uc);
	    Parser p = new Parser (src);
	    currentParseHS = hs;
	    p.addParserListener(this);
	    p.parse();
	    src.close();
	} catch (Exception e) {
	    debug ("exception thrown" + e.toString());
	    e.printStackTrace();
	    return;
	}

	tagStack = null;
	itemStack = null;
    }

    private HelpSet currentParseHS;

    // Ignore the following two
    public void piFound(ParserEvent e) {
    }
    public void doctypeFound(ParserEvent e) {
    }

    /**
     * A Tag was parsed. Tags from standardard TOC xml documents
     * and ClassNavigator xml documents are processed here. 
     */
    public void tagFound(ParserEvent e) {
	Locale locale = null;
	Tag tag = e.getTag();

	TagProperties attr = tag.atts;

	if (attr != null) {
	    String lang = attr.getProperty("xml:lang");
	    locale = HelpUtilities.localeFromLang(lang);
	}
	if (locale == null) {
	    locale = lastLocale;
	}

	if (tag.name.equals("tocitem")) {
	    if (!startedtoc) {
		// System.out is wrong here but we'll figure that out later
		debug("TOC data incorrect");
	    }
	    if (tag.isEnd && !tag.isEmpty) {
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
		return;
	    }
	    String id=null;
	    String imageID=null;
	    String text = null;
	    if (attr != null) {
		id = attr.getProperty("target");
		imageID = attr.getProperty("image");
		text = attr.getProperty("text");
	    }
	    TOCItem item = null;
	    try {
		item = new TOCItem(ID.create(id, currentParseHS),
				   ID.create(imageID, currentParseHS),
				   locale);
		if (text != null) {
		    item.setName(text);
		}
	    } catch (Exception ex) {
	    }
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
	    DefaultMutableTreeNode parent =
		(DefaultMutableTreeNode) nodeStack.peek();
	    parent.add(node);
	    if (! tag.isEmpty) {
		itemStack.push(item);
		nodeStack.push(node);
		addTag(tag, locale);
	    }
	    return;
	} else if (tag.name.equals("toc")) {
	    if (!tag.isEnd) {
		if (startedtoc) {
		    System.out.println ("TOC data incorrect");
		}
		startedtoc = true;
		addTag(tag, locale);
	    } else {
		if (startedtoc) {
		    startedtoc = false;
		}
		removeTag(tag);
	    }
	    return;
	} else if (tag.name.equals("field")) {
	    if (!startedfields) {
		debug("Class field data incorrect");
	    }
	    if (tag.isEnd && !tag.isEmpty) {
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
		return;
	    }
	    String id=null;
	    String text = null;
	    if (attr != null) {
		id = attr.getProperty("target");
		text = attr.getProperty("text");
	    }
	    TOCItem item = null;
	    try {
		item = new TOCItem(ID.create(id, currentParseHS),
				   null,
				   locale);
		if (text != null) {
		    item.setName(text);
		}
	    } catch (Exception ex) {
	    }
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
	    DefaultMutableTreeNode parent = 
		(DefaultMutableTreeNode) nodeStack.peek();
	    parent.add(node);
	    if (!tag.isEmpty) {
		itemStack.push(item);
		nodeStack.push(node);
		addTag(tag, locale);
	    }
	    return;
	} else if (tag.name.equals("fields")) {
	    if (!tag.isEnd) {
		if (startedfields && !startedclass) {
		    System.out.println("Class fields data incorrect");
		}
		startedfields = true;
		TOCItem item = new TOCItem(null, null, locale);
		item.setName("Fields");
		itemStack.push(item);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		DefaultMutableTreeNode parent = 
		    (DefaultMutableTreeNode) nodeStack.peek();
		parent.add(node);
		nodeStack.push(node);
		addTag(tag, locale);
	    } else {
		if (startedfields) {
		    startedfields = false;
		}
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
	    }
	    return;
	} else if (tag.name.equals("constructor")) {
	    if (!startedconstructors) {
		debug("Class constructor data incorrect");
	    }
	    if (tag.isEnd && !tag.isEmpty) {
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
		return;
	    }
	    String id=null;
	    String text = null;
	    if (attr != null) {
		id = attr.getProperty("target");
		text = attr.getProperty("text");
	    }
	    TOCItem item = null;
	    try {
		item = new TOCItem(ID.create(id, currentParseHS),
				   null,
				   locale);
		if (text != null) {
		    item.setName(text);
		}
	    } catch (Exception ex) {
	    }
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
	    DefaultMutableTreeNode parent = 
		(DefaultMutableTreeNode) nodeStack.peek();
	    parent.add(node);
	    if (!tag.isEmpty) {
		itemStack.push(item);
		nodeStack.push(node);
		addTag(tag, locale);
	    }
	    return;
	} else if (tag.name.equals("constructors")) {
	    if (!tag.isEnd) {
		if (startedconstructors && !startedclass) {
		    System.out.println("Class constructors data incorrect");
		}
		startedconstructors = true;
		TOCItem item = new TOCItem(null, null, locale);
		item.setName("Constructors");
		itemStack.push(item);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		DefaultMutableTreeNode parent = 
		    (DefaultMutableTreeNode) nodeStack.peek();
		parent.add(node);
		nodeStack.push(node);
		addTag(tag, locale);
	    } else {
		if (startedconstructors) {
		    startedconstructors = false;
		}
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
	    }
	    return;
	} else if (tag.name.equals("method")) {
	    if (!startedmethods) {
		System.out.println("Class method data incorrect");
	    }
	    if (tag.isEnd && !tag.isEmpty) {
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
		return;
	    }
	    String id=null;
	    String text = null;
	    if (attr != null) {
		id = attr.getProperty("target");
		text = attr.getProperty("text");
	    }
	    TOCItem item = null;
	    try {
		item = new TOCItem(ID.create(id, currentParseHS),
				   null,
				   locale);
		if (item != null) {
		    item.setName(text);
		}
	    } catch (Exception ex) {
	    }
	    DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
	    DefaultMutableTreeNode parent = 
		(DefaultMutableTreeNode) nodeStack.peek();
	    parent.add(node);
	    if (!tag.isEmpty) {
		itemStack.push(item);
		nodeStack.push(node);
		addTag(tag, locale);
	    }
	    return;
	} else if (tag.name.equals("methods")) {
	    if (!tag.isEnd) {
		if (startedmethods && !startedclass) {
		    System.out.println("Class methods data incorrect");
		}
		startedmethods = true;
		TOCItem item = new TOCItem(null, null, locale);
		item.setName("Methods");
		itemStack.push(item);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		DefaultMutableTreeNode parent = 
		    (DefaultMutableTreeNode) nodeStack.peek();
		parent.add(node);
		nodeStack.push(node);
		addTag(tag, locale);
	    } else {
		if (startedmethods) {
		    startedmethods = false;
		}
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
	    }
	    return;
	} else if (tag.name.equals("description")) {
	    // Looks a little strange but it's ok 
	    // Descriptions are self contained. The end tag is at the end
	    // of the tag itself.
	    if (tag.isEnd) {
		if (!startedclass) {
		    System.out.println("Class description data incorrect");
		}
		String id=null;
		if (attr != null) {
		    id = attr.getProperty("target");
		}
		TOCItem item = null;
		try {
		    item = new TOCItem(ID.create(id, currentParseHS),
				       null,
				       locale);
		    item.setName("Description");
		} catch (Exception ex) {
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		DefaultMutableTreeNode parent = 
		    (DefaultMutableTreeNode) nodeStack.peek();
		parent.add(node);
	    } else {
		debug ("description must have internal end tag");
	    }
	    return;
	} else if (tag.name.equals("class")) {
	    if (!tag.isEnd) {
		if (startedclass) {
		    System.out.println("Class <class> data incorrect");
		}
		startedclass = true;
		String id=null;
		String text = null;
		if (attr != null) {
		    id = attr.getProperty("target");
		    text = attr.getProperty("text");
		}
		TOCItem item = null;
		try {
		    item = new TOCItem(ID.create(id, currentParseHS),
				       null,
				       locale);
		    if (text != null) {
			item.setName(text);
		    }
		} catch (Exception ex) {
		}
		itemStack.push(item);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		DefaultMutableTreeNode parent = 
		    (DefaultMutableTreeNode) nodeStack.peek();
		parent.add(node);
		nodeStack.push(node);
		addTag(tag, locale);
	    } else {
		if (startedclass) {
		    startedclass = false;
		}
		nodeStack.pop();
		itemStack.pop();
		removeTag(tag);
	    }
	    return;
	} 
    }

    /**
     * A continous block of text was parsed
     */
    public void textFound(ParserEvent e) {
	// Ignore text if there isn't a tag
	if (tagStack.empty()) {
	    return;
	}
	LangElement le = (LangElement) tagStack.peek();
	Tag tag = (Tag) le.getTag();
	debug ("Can't add text for " + tag.name);
	debug ("  text is >"+e.getText()+"<");
    }

    // The remaing events from Parser are ignored
    public void commentFound(ParserEvent e) {}

    public void errorFound(ParserEvent e){
	System.out.println (e.getText());
    }

    /**
	 * addTag keeps track of tags and their locale attributes
	 */
    protected void addTag(Tag tag, Locale locale) {
	LangElement el = new LangElement(tag, locale);
	tagStack.push(el);
	// It's possible for lastLocale not be specified ergo null.
	// If it is then set lastLocale to null even if locale is null.
	// It is impossible for locale to be null
	if (lastLocale == null) {
	    lastLocale = locale;
	    return;
	}
	if (locale == null) {
	    lastLocale = locale;
	    return;
	}
	if (! lastLocale.equals(locale)) {
	    lastLocale = locale;
	}
    }

    /**
	 * removeTag removes a tag from the tagStack. The tagStack is
	 * used to keep track of tags and locales
	 */
    protected void removeTag(Tag tag) {
	LangElement el;
	String name = tag.toString();
	Locale newLocale =null;

	for (;;) {
	    if (tagStack.empty()) 
		break;
	    el = (LangElement) tagStack.pop();
	    if (el.getTag().toString().equals(name)) {
		if (tagStack.empty()) {
		    newLocale = defaultLocale;
		} else {
		    el = (LangElement) tagStack.peek();
		    newLocale = el.getLocale();
		}
		break;
	    }
	}
	// It's possible for lastLocale not be specified ergo null.
	// If it is then set lastLocale to null even if locale is null.
	// It also possible for locale to be null so if lastLocale is set
	// then reset lastLocale to null;
	// Otherwise if lastLocale doesn't equal locale reset lastLocale to locale
	if (lastLocale == null) {
	    lastLocale = newLocale;
	    return;
	}
	if (newLocale == null) {
	    lastLocale = newLocale;
	    return;
	}
	if (! lastLocale.equals(newLocale)) {
	    lastLocale = newLocale;
	}
    }

    public void idChanged(HelpModelEvent e) {
	debug ("idChanged");
	ID id = e.getID();
	URL url = e.getURL();
	HelpModel helpModel = treenav.getModel();

	if (e.getSource() != helpModel) {
	    System.err.println("Internal inconsistency!");
	    System.err.println("  "+e.getSource()+" != "+helpModel);
	    throw new Error("Internal error");
	}

	if (id == null) {
	    //return;
	}
	TreePath s = tree.getSelectionPath();
	if (s != null) {
	    Object o = s.getLastPathComponent();
	    // should require only a TreeNode
	    if (o instanceof DefaultMutableTreeNode) {
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
		TOCItem item = (TOCItem) tn.getUserObject();
		if (item != null) {
		    ID nId = item.getID();
		    if (nId != null && nId.equals(id)) {
			return;
		    }
		}
	    }
	}

	debug("finding: "+id);
	DefaultMutableTreeNode node = findID(topNode, id);
	if (node == null) {
	    // probably should do something other than return here.
	    // perhaps need to clear the selection
	    return;
	}
	TreePath path = new TreePath(node.getPath());
	tree.expandPath(path);
	tree.setSelectionPath(path);
    }

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode node,
					  ID id)
    {
	if (id == null) {
	    return null;
	}
	TOCItem item = (TOCItem) node.getUserObject();
	if (item != null) {
	    ID testID = item.getID();
	    debug("  testID: "+testID);
	    if (testID != null && testID.equals(id)) {
		return node;
	    }
	}
	int size = node.getChildCount();
	for (int i=0; i<size ; i++) {
	    DefaultMutableTreeNode tmp = 
		(DefaultMutableTreeNode) node.getChildAt(i);
	    DefaultMutableTreeNode test = findID(tmp, id);
	    if (test != null) {
		return node;
	    }
	}
	return null;
    }

    public void valueChanged(TreeSelectionEvent e) {
	if (e.getSource()==tree.getSelectionModel()) {
	    TreePath path = tree.getSelectionPath();
	    DefaultMutableTreeNode node = 
		(DefaultMutableTreeNode) path.getLastPathComponent();
      
	    TOCItem tocEl = (TOCItem) node.getUserObject();

	    if (tocEl != null && tocEl.getID() != null) {
		debug ("Setting CurrentID -" + tocEl.getID());
		changeClass(tocEl.getID());
	    }

	}
	else if(e.getSource()==classtree.getSelectionModel()) {
	    
	    TreePath path = classtree.getSelectionPath();

	    // if there is no path selected, just ignore it (for now?) - epll
	    if (path == null) {
		return;
	    }

	    DefaultMutableTreeNode node = 
		(DefaultMutableTreeNode) path.getLastPathComponent();
      
	    TOCItem tocEl = (TOCItem) node.getUserObject();

	   
	    if (tocEl != null && tocEl.getID() != null) {
		try {
		    debug ("Setting CurrentID -" + tocEl.getID());
		    treenav.getModel().setCurrentID(tocEl.getID());
		} catch (InvalidHelpSetContextException e2) {
		    debug ("BadID");
		    return;
		}
	    }
	}
    }

    public void propertyChange(PropertyChangeEvent event)
    {
	debug("propertyChange");
	// Ignore -- for now at least - epll
    }

    /**
     * For printf debugging.
     */
    private static final boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicClassViewerNavigatorUI: " + str);
        }
    }
}
