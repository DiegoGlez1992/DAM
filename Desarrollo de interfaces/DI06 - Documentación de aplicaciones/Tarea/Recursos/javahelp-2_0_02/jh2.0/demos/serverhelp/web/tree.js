/*
 * @(#)tree.js	1.5 03/03/01
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved
 *
 * Author: Roger D. Brinkley
 */

/*
 * Tree constructor
 *
 * Create a Tree object 
 * 
 * param name - Name of the tree
 * param lineheight - height of a individual line in the tree
 * param selectColor - selection color
 * param showIcon - if true icons are to be displayed, false no icons are displayed
 * param expandAll - if true expands all the entries with undefined expansion, false expands only 1 level
 * 
 */
function Tree (name, lineHeight, selectColor, showIcon, expandAll) {
    // Data
    this.name = name;
    this.lineHeight = lineHeight;
    this.topNodes = new Object();
    this.topNodesCount = 0;
    this.totalNodes = 0;
    this.selectedNode = null;
    this.selectedBG = null;
    this.selectColor = selectColor;
    this.showIcon = showIcon;
    this.expandAll = expandAll;

    // methods
    this.addTreeNode = addTreeNode;
    this.findTreeNodeForIDNum = findTreeNodeForIDNum;
    this.drawTree = drawTree;
    this.drawTreeNode = drawTreeNode;
    this.refreshTree = refreshTree;
    this.refreshTreeNode = refreshTreeNode;
    this.toggle = toggleTreeNode;
    this.scrollToTreeNode = scrollToTreeNode;
    this.select = selectTreeNode;
    this.selectFromHelpID = selectTreeNodeFromHelpID;
}

/**
 * Add a TreeNode to the tree
 * 
 * param parent - Name of the parent
 * param idnum - Name for this object
 * param icon - Image to be displayed for this TreeItem - null means default
 * param content - Display content for the node. Generally the name.
 * param helpID - helpID for this TreeItem
 * param URLData - URL from the HelpSet for this TreeItem - related to helpID
 * param expandType - -1 if program depended; 1 if children should be shown; 0 otherwise
 *
 */
function addTreeNode(parent, idnum, icon, content, helpID, URLData, expandType) {
    // ignore nulls
    if (idnum == "null") {
	return;
    }
    var node = new TreeNode(idnum, icon, content, helpID, URLData, expandType);
    this.totalNodes++;
    if (parent == "null" || parent == "root") {
	this.topNodes[this.topNodesCount] = node;
	this.topNodesCount++;
    } else {
	parentNode = this.findTreeNodeForIDNum(parent);
	if (parentNode == null) {
	    alert ("parent " + parent + " for idnum " + idnum + " doesn't exist");
	} else {
	    parentNode.addChild(this, node, expandType);
	}
    }
}

/**
 * Find the Node for a given idnum
 */
function findTreeNodeForIDNum(idnum) {
    for (var i=0; i<this.topNodesCount ;i++) {
	var node = this.topNodes[i].findIDNum(idnum);
	if (node != null) {
	    return node;
	}
    }
    return null;
}

/**
 * Draw the Tree
 */
function drawTree() {

    // define the layer for the Tree depending on browser type
    if ( !browser.canDoDOM ) {
	document.writeln("<ILAYER ID='tree_" + this.name + "' HEIGHT=" + (this.totalNodes + 1)*this.lineHeight + ">");
	// create a selection layer
	document.writeln("<LAYER ID='" + this.name + "Select' visibility='hide' bgColor='" + this.selectColor + "'>");
	document.writeln("</LAYER>");
    } else {
	if ( browser.name == "Opera" ) {
	    document.writeln("<DIV ID=tree_" + this.name + " STYLE={visibility:visible;height:" + (this.totalNodes + 1) * this.lineHeight + "}>");
	}
    }
    
    IsLine = new Object();
    IsLineCount = 0;
    for (var i=0; i<this.topNodesCount ; i++) {
	this.drawTreeNode(this.topNodes[i]);
    }
    IsLine = null;

    if ( !browser.canDoDOM ) {
        document.writeln("</ILAYER>");
    } else {
	if ( browser.name == "Opera" ) {
	    document.writeln("</DIV>");
	}
    }
}

/**
 * This is an array to determine whether to put a line or a space
 * in front of a Node for hierarchy trees
 */
var IsLine = null;
var IsLineCount = 0;

/**
 * Draw the node and it's children.
 * If the node is null draw all the topNodes
 * 
 * param node - the node (and children) to draw
 *
 */
function drawTreeNode(node) {

    // create a <DIV> or <LAYER> depending on browser capability
    // notice the differences in DIV and LAYER attribute setting
    if ( browser.canDoDOM ) {
	document.writeln("<DIV ID=" + node.idnum + " STYLE={position:relative; visibility:visible}>");
    } else {
	document.writeln("<LAYER ID=" + node.idnum + " visibility='hide'>");
    }
    
    //create a table for this node with only one table row
    document.writeln("<table cellspacing=0 cellpadding=0 border=0>");
    document.writeln("<tr>");

    // create table descriptions <td> for the heirarchy lines (or spaces)
    // there are no heirarchical lines for the first node or children of 
    // the first node
    for (var i=0 ; i < IsLineCount - 1 ; i++) {
	if (IsLine[i] == true ){
	    document.writeln("<td nowrap><img src='images/tree_linevertical.gif' border=0 width=16 height=22></td>");
	} else{
	    document.writeln("<td nowrap><img src='images/tree_blank.gif' border=0 width=16 height=22></td>");
	}
    }

    // create the heirarchy line if a leaf or the turner if a node
    // Don't bother is if this is a top node
    if (IsLineCount > 0) { 
	if (node.numChildren == 0) {
	    // a leaf
	    if (IsLine[IsLineCount-1]) {
		// there are more below this leaf show a middle gif
		document.writeln("<td nowrap><img src='images/tree_linemiddlenode.gif' border=0 width=16 height=22></td>");
	    } else {
		document.writeln("<td nowrap><img src='images/tree_linelastnode.gif' border=0 width=16 height=22></td>");
	    }
	} else {
	    // a turner
	    if (node.expand) {
		// node is open adjust the handle appropriately
		// you must use a href='#' for nonDOM browsers
		// this is counteracted by returning false from toggle
		if (IsLine[IsLineCount-1]) {
		    // not the end
		    document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".toggle(\""+node.idnum+"\")'><img name='"+node.idnum+"Toggle' src='images/tree_handledownmiddle.gif' border=0 width=16 height=22></a></td>");
		} else {
		    // the last one
		    document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".toggle(\""+node.idnum+"\")'><img name='"+node.idnum+"Toggle' src='images/tree_handledownlast.gif' border=0 width=16 height=22></a></td>");
		}
	    } else {
		// node is closed adjust the handle appropriately
		if (IsLine[IsLineCount-1]) {
		    // not the end
		    document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".toggle(\""+node.idnum+"\")'><img name='"+node.idnum+"Toggle' src='images/tree_handlerightmiddle.gif' border=0 width=16 height=22></a></td>");
		} else {
		    // the last one
		    document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".toggle(\""+node.idnum+"\")'><img name='"+node.idnum+"Toggle' src='images/tree_handlerightlast.gif' border=0 width=16 height=22></a></td>");
		}
	    }
	}
    }

    // create a table definition for the image
    // use the node icon if supplied otherwised use the default folder or document
    if (this.showIcon) {
	if (node.icon != "null") {
	    document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".select(\""+node.idnum+"\")'><img src='" + node.icon + "' border=0 width=19 height=22></td></a>");
	} else {
	    if (node.numChildren == 0 ) {
		// leaf
		document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".select(\""+node.idnum+"\")'><img src='images/tree_document.gif' border=0 width=19 height=22></td></a>");
	    } else {
		document.writeln("<td nowrap><a href='#' onClick='return "+this.name+".select(\""+node.idnum+"\")'><img src='images/tree_folder.gif' border=0 width=19 height=22></td></a>");
	    }
	}
    }

    // create the content
    document.writeln("<td nowrap><a id='"+node.idnum+"Content' class='anchorStyle' href='#' onClick='return "+this.name+".select(\""+node.idnum+"\")'>"+ node.content + "</a></td>");

    
    // finish off the necessary details for this table row and table
    document.writeln("</tr>");
    if ( browser.canDoDOM ) {
	document.writeln("</table></DIV>");
    } else {
	document.writeln("</table></LAYER>");
    }

    // All done now draw any children 
    IsLineCount++;
    for (var i=0; i<node.numChildren ; i++) {
	// set the IsLine Array appropriately
	if (i == node.numChildren - 1) {
	    IsLine[IsLineCount - 1] = false;
	} else {
	    IsLine[IsLineCount - 1] = true;
	}
	this.drawTreeNode(node.children[i]);
    }
    IsLineCount--;
}

/**
 * Refresh the Tree based on the hierarchical visibility
 */
function refreshTree() {
    // call refreshTreeNode for all the topNodes
    // this will recursively call 
    LineNumber = 0;
    for (var i=0; i<this.topNodesCount ; i++) {
	this.refreshTreeNode(this.topNodes[i], true);
    }
}

var LineNumber = 0;

/**
 * Refresh a Node based on the nodes and ancestoral visibility
 *
 * param node - node to refresh
 * param ancestorOpen - true if ancestor is visible, false otherwise
 */
function refreshTreeNode(node, ancestorOpen) {
    if (ancestorOpen) {
	LineNumber ++;
	node.visible = true;
	node.lineNumber = LineNumber;
	if ( browser.canDoDOM ) {
	    if ( browser.name == "InternetExplorer" ){
		document.getElementById(node.idnum).style.display='inline';
	    } else {
                if ( browser.name == "Opera" ) {
                    document.getElementById(node.idnum).style.visibility = 'visible';
		    if ( eval('document.getElementById("' + node.idnum + '_NORM")') ){
                        document.getElementById(node.idnum + "_NORM").style.visibility='inherit';
                    }
                    document.getElementById(node.idnum).style.top=(LineNumber)*this.lineHeight;
                } else {
                    document.getElementById(node.idnum).style.position='relative';
                    document.getElementById(node.idnum).style.visibility='visible';
                }
            }
	} else {
	    eval('document.tree_' + this.name + '.layers["' + node.idnum + '"]').top=(LineNumber)*this.lineHeight;
	    eval('document.tree_' + this.name + '.layers["' + node.idnum + '"]').visibility='show';
	}
    } else {
	node.visible = false;
	node.lineNumber = 0;
	if ( browser.canDoDOM ) {
	    if ( browser.name == "InternetExplorer" ){
		document.getElementById(node.idnum).style.display='none';
	    } else {
		if ( browser.name == "Opera" ) {
                    document.getElementById(node.idnum).style.visibility = 'hidden';
                    document.getElementById(node.idnum).style.top=this.totalNodes*this.lineHeight;
                } else {
                    document.getElementById(node.idnum).style.position='absolute';
                    document.getElementById(node.idnum).style.visibility='hidden';
                }
            }
	} else {
	    eval('document.tree_' + this.name + '.layers["' + node.idnum + '"]').top=this.totalNodes*this.lineHeight;
	    eval('document.tree_' + this.name + '.layers["' + node.idnum + '"]').visibility='hide';
	}
    }

    // All done now refresh the children 

    for (var i=0; i<node.numChildren ; i++) {
	var nodeOpen = ancestorOpen;
	// if the ancestor is open but the node is not then
	// set the visibility to false for the childe
	if (ancestorOpen && !node.expand) {
	    nodeOpen = false;
	}
	this.refreshTreeNode(node.children[i], nodeOpen);
    }
}

/**
 * Toggle the tree node
 *
 * param idnum - the idnum of the Node to toggle
 */
function toggleTreeNode(idnum) {
    // get the node
    var node = this.findTreeNodeForIDNum(idnum);
    if (node == null) {
	alert ("internal error - " + idnum + " not found in toggleTreeNode");
    }

    // change the internal data structure
    if (node.expand) {
	node.expand = false;
    } else {
	node.expand = true;
    }

    // select the appropriate image depending on which child I am
    // and if I'm expanded or not
    // get a base address for images
    var base = window.location.href;
    var srcbase = base.substring(0, base.lastIndexOf("/") + 1);
    var imgsrc = null;
    if (node.expand) {
	imgsrc = srcbase + "images/tree_handledownlast.gif";
    } else {
	imgsrc = srcbase + "images/tree_handlerightlast.gif";
    }
    for (var i=0; i<parent.numChildren-1 ; i++) {
	if (parent.chilren[i] == node) {
	    if (node.expand) {
		imgsrc = srcbase + "images/tree_handledownmiddle.gif";
	    } else {
		imgsrc = srcbase + "images/tree_handlerightmiddle.gif";
	    }
	    break;
	}
    }

 
    // change the current image 
    if (browser.canDoDOM) {
	if (browser.name == "InternetExplorer") {
	    document.getElementById(node.idnum + "Toggle").src = imgsrc;
	} else {
	    document.images[node.idnum+"Toggle"].src = imgsrc;
	}
    } else {
        var holder = eval('document.tree_' + this.name + '.layers["' + node.idnum + '"]');
        holder.document.images[node.idnum+"Toggle"].src = imgsrc;
    }

    this.refreshTree();
    this.scrollToTreeNode(node);

    // this done to cancel click action
    return false;
}

function scrollToTreeNode(node) {
    if (node.visible == true) {
	var nodeTop = 0;
	var nodeBottom = 0;
	var windowTop = 0;
	var windowBottom = 0;

	// Calculate the top and bottom of the nodes display 
	if ( browser.canDoDOM ) {
            if ( browser.name == 'InternetExplorer' ) {
                nodeTop = document.body.scrollTop + (this.lineHeight * node.lineNumber);
            } else {
                nodeTop = window.pageYOffset + (this.lineHeight * node.lineNumber);
	    }
	} else {
            var nodeArea = eval('document.tree_' + this.name + '.layers["' + node.idnum + '"]');
	    nodeTop = nodeArea.pageY;
	}
        nodeBottom = nodeTop + (this.lineHeight*node.numChildren) + this.lineHeight;

	// Calculate the containing windows display top and bottom 
        if ( browser.name == 'InternetExplorer')
        {
            windowTop = document.body.scrollTop;
            windowBottom = windowTop + document.body.clientHeight
        }
        else
        {
            windowTop = window.pageYOffset;
            windowBottom = windowTop + window.innerHeight;
        }

	// determine how much to scroll by subtracting the end of the window
	// from the end of the end
        var scrollValue = nodeBottom - windowBottom;

	// Scroll to make the node viewable on the screen
        if ((nodeBottom > windowBottom) || (nodeTop < windowTop))
        {
            setTimeout('scrollBy(0, ' + scrollValue + ')', 50);
        }
    }
}

/**
 * select the tree node
 *
 * param idnum - the idnum of the Node to toggle
 */
function selectTreeNode(idnum) {

    // get the node
    var node = this.findTreeNodeForIDNum(idnum);
    if (node == null) {
	alert ("internal error - " + idnum + " not found in toggleTreeNode");
	return false;
    }

    // leave selected items
    if (this.selectedNode == node) {
	return false;
    }

    // change the background on the selected node
    if (this.selectedNode != null){
	if (browser.canDoDOM) {
	    var style = document.getElementById(this.selectedNode.idnum + "Content").style;
	    style.backgroundColor = this.selectedBG;
	}
    }

    // make the new node the slected node
    this.selectedNode = node;

    if (browser.canDoDOM) {
	var style = document.getElementById(idnum + "Content").style;
	this.selectedBG = style.backgroundColor;
	style.backgroundColor = this.selectColor;
    } else {
	// we'll use a layer for this and just move it around
	var selectLayer = eval ('document.tree_' + this.name + '.layers["' + this.name + 'Select"]');
	var layer = eval ('document.tree_' + this.name + '.layers["' + idnum + '"]');
	var layerDoc = eval('document.tree_' + this.name + '.layers["' + idnum + '"].document');
	var link = layerDoc.links[layerDoc.links.length - 1];
	selectLayer.left = link.x;
	selectLayer.top = layer.top;
	selectLayer.resizeTo(layerDoc.width - link.x, layerDoc.height);
	selectLayer.visibility='show';
    }

    // display the selection in the contents
    if ( top.invoke ) {
	if (node.URLData != "null") {
	    top.invoke(node.URLData, node.helpID);
	} else {
	}
    } else {
	alert ("selected " + node.content);
    }

    // this done to cancel click action
    return false;
}

/**
 * Select a Tree Node from a HelpID
 */
function selectTreeNodeFromHelpID(helpID) {
    var node = null;
    for (var i=0; i<this.topNodesCount ;i++) {
	node = this.topNodes[i].findHelpID(helpID);
	if (node != null) {
	    break;
	}
    }
    if (node != null) {
	node.setAncestorExpand();
	this.refreshTree();
	this.select(node.idnum);
	this.scrollToTreeNode(node);
    }
}

/**
 * TreeNode constuctor
 *
 * Create a TreeNode for use in a Tree
 *
 * param parent - Name of the parent
 * param idnum - Id number for the tree Node
 * param icon - image to display for this TreeNode - null means use default
 * param content - content to display in tree - generally the name
 * param helpID - helpID for this item
 * param URLData - URL for the helpID to display
 * param expand - should children of the TreeItem be expanded 
 */
function TreeNode(idnum, icon, content, helpID, URLData, expandType) {
    this.parent = null;
    this.idnum = idnum;
    this.icon = icon;
    this.content = content;
    this.helpID = helpID;
    this.URLData = URLData;
    this.expand = true;
    if (expandType == "0") {
	this.expand == false;
    }
    this.visible = false;
    this.isSelected = false;
    this.children = new Array();
    this.numChildren = 0;
    this.level = 0;
    this.lineNumber = 0;

    // methods
    this.addChild = addChild;
    this.findIDNum = findIDNum;
    this.findHelpID = findHelpID;
    this.setExpand = setExpand;
    this.setAncestorExpand = setAncestorExpand;
} 

/**
 * Add a child object to the TreeItem
 *
 * param child - Child TreeItem object to add
 */
function addChild(tree, child, expandType) {
    this.children[this.numChildren] = child;
    this.numChildren++;
    child.level = this.level + 1;
    if (tree.expandAll) {
	if (expandType == "-1") {
	    this.expand = true;
	}
    } else {
	if (expandType == "-1" && child.level > 1) {
	    this.expand = false;
	}
    }
}

/**
 * Recursively find an idnum that matches the parameter
 *
 * param idnum - ID number to find
 *
 * returns the matching Node or null if no matches
 */
function findIDNum(idnum) {
    // if this node matches return it
    if (this.idnum == idnum) {
	return this;
    }
    // humm no matches see if the children match
    for (var i=0; i<this.numChildren ; i++) {
	var node = this.children[i].findIDNum(idnum);
	if (node != null) {
	    return node;
	}
    }
    // no matches return null
    return null;
}

/**
 * Recursively find an idnum that matches the helpID
 *
 * param help - HelpID to find
 *
 * returns the matching Node or null if no matches
 */
function findHelpID(helpID) {
    // if this node matches return it
    if (this.helpID == helpID) {
	return this;
    }
    // humm no matches see if the children match
    for (var i=0; i<this.numChildren ; i++) {
	var node = this.children[i].findHelpID(helpID);
	if (node != null) {
	    return node;
	}
    }
    // no matches return null
    return null;
}

/**
 * Set the parent object for the TreeItem
 *
 * param parent - Parent object for this treeItem - null is valid
 */
function setParent(parent) {
    this.parent = parent;
}

/**
 * Set the expand flag for the TreeItem
 * 
 * param type - type of expansion: -1 program default; 0 close; 1 open
 */
function setExpand(type) {
    this.expand = type;
}

/**
 * recursively sets the ancestors to be expanded
 */
function setAncestorExpand() {
    if (this.parent != null) {
	parent.setExpand(true);
	parent.setAncestorExpand();
    }
}

var browser = new browserData();
function browserData()
{
    var useragnt = navigator.userAgent;
    this.canDoDOM = (document.getElementById) ? true : false;
    if ( useragnt.indexOf('Opera') >= 0) {
	this.name = 'Opera';
    } else if (  useragnt.indexOf('MSIE') >= 0 ) {
	this.name = 'InternetExplorer';
    } else {
	this.name = 'Another';
    }
}


