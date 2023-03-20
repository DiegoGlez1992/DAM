/*
 * @(#)searchlist.js	1.2 03/03/01
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
function SearchList (name, lineHeight, selectColor) {
    // Data
    this.name = name;
    this.lineHeight = lineHeight;
    this.nodes = new Array();
    this.selectedElement = -1;
    this.selectedBG = null;
    this.selectColor = selectColor;

    // methods
    this.addNode = addNode;
    this.drawList = drawList;
    this.refreshList = refreshList;
    this.compare = compare;
    this.scrollToNode = scrollToNode;
    this.select = selectNode;
    this.selectFromHelpID = selectNodeFromHelpID;
}

/**
 * Add a TreeNode to the tree
 * 
 * param parent - Name of the parent
 * param idnum - Name for this object
 * param icon - Image to be displayed for this TreeItem - null means default
 * param helpID - helpID for this TreeItem
 * param URLData - URL from the HelpSet for this TreeItem - related to helpID
 * param expandType - -1 if program depended; 1 if children should be shown; 0 otherwise
 *
 */
function addNode(content, confidence, hits, helpID, URLData) {
    this.nodes[this.nodes.length] = new Node(content, confidence, hits, helpID, URLData);
}

/**
 * Draw the List
 */
function drawList() {
    // bore output do a sort 
    this.nodes.sort(this.compare);
    
    // we'll have to use layers with multiple tables to do selection on Netscape 4.x
    if (!browser.canDoDOM) {
	document.writeln("<ILAYER ID='list_" + this.name + "' HEIGHT=" + (this.nodes.length)*this.lineHeight + ">");
	// create a selection layer
	document.writeln("<LAYER ID='" + this.name + "Select' visibility='hide' bgColor='" + this.selectColor + "'>");
	document.writeln("</LAYER>");
    } else {
	// for browsers that have DOM we'll do a single table
	document.writeln("<table cellspacing=0 cellpadding=0 border=0>");
    }

    // draw each node
    for (var i=0; i<this.nodes.length ; i++) {
	if ( !browser.canDoDOM ) {
	    document.writeln("<LAYER ID='" + this.name + i + "' visibility='hide'>");
	    //create a table for this node with only one table row
	    document.writeln("<table cellspacing=0 cellpadding=0 border=0>");
	}
    
	var node = this.nodes[i];
 
	//create a table for this node with only one table row
	document.writeln("<tr>");

	// create the confidence image
	var imgString = "";
	var penalty = parseFloat(node.confidence);
	if (penalty < 1) {
	    imgString += "<IMG SRC='images/high.gif'";
	} else if (penalty < 10) {
	    imgString += "<IMG SRC='images/medhigh.gif'";
	} else if (penalty < 25) {
	    imgString += "<IMG SRC='images/med.gif'";
	} else if (penalty < 50) {
	    imgString += "<IMG SRC='images/medlow.gif'";
	} else {
	    imgString += "<IMG SRC='images/low.gif'";
	}
	imgString +=" ALT='" + node.confidence + "' border=0 width=16 height=22>";
	document.writeln("<td nowrap>" + imgString + "</td>");
    
	// create the hits
	var setWidth = "";
	if (!browser.canDoDOM) {
	    setWidth = "width=20";
	}
	document.writeln("<td align='right' " + setWidth + " nowrap><a class='anchorBoldStyle' href='#' onClick='return "+this.name+".select(\""+i+"\")'>"+ node.hits + "</a></td>");


	// create the content
	document.writeln("<td nowrap>");
	document.writeln("<a id='"+i+"Content' class='anchorStyle' href='#' onClick='return "+this.name+".select(\""+i+"\")'>"+ node.content + "</a>");
	document.writeln("</td></tr>");

	if (!browser.canDoDOM) {
	    document.writeln("</table></LAYER>");
	}
    }

    if ( !browser.canDoDOM ) {
	// can't do DOM so we'll close up our layers
        document.writeln("</ILAYER>");
    } else {
	// end the table - DOM only navigators
	document.writeln("</table>");
    }
    

}

/**
 * Refresh the List based on the hierarchical visibility
 */
function refreshList() {
    // This is a noop for browsers that handle DOM
    if (browser.canDoDOM) {
	return;
    }

    for (var i=0; i<this.nodes.length ; i++) {
	var node = this.nodes[i]; 
	node.lineNumber = i + 1;
	eval('document.list_' + this.name + '.layers["' + this.name + i + '"]').top=i*this.lineHeight;
	eval('document.list_' + this.name + '.layers["' + this.name + i + '"]').visibility='show';
    }
}

/*
 * Node method
 * Compare two nodes based on the confidence value
 */
function compare (a, b)
{
    // important for sorting!!!
    // what we return is what is used
    // as the sorting key.
        
    var aconf = parseFloat(a.confidence);
    var bconf = parseFloat(b.confidence);
    if ((aconf - bconf) != 0) {
	return (aconf - bconf);
    }
    return (b.hits - a.hits);
}

function scrollToNode(node) {
    if (node.visible == true) {
	var nodeTop = 0;
	var nodeBottom = 0;
	var windowTop = 0;
	var windowBottom = 0;

	// Calculate the top and bottom of the nodes display 
	if ( browser.canDoDOM ) {
            if ( browser.name == 'InternetExplorer' ) {
                nodeTop = document.body.scrollTop + (this.lineHeight * node.lineNumber-1);
            } else {
                nodeTop = window.pageYOffset + (this.lineHeight * node.lineNumber-1);
	    }
	} else {
            var nodeArea = eval('document.list_' + this.name + '.layers["' + this.name + node.lineNumber-1 + '"]');
	    nodeTop = nodeArea.pageY;
	}
        nodeBottom = nodeTop + this.lineHeight;

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
 * select the node
 *
 * param idnum - the idnum of the Node to toggle
 */
function selectNode(element) {

    if (element >= this.nodes.length ) {
	alert ("internal error - " + element + " not found in selectTreeNode");
	return false;
    }

    // leave if selected items
    if (this.element == element) {
	return false;
    }

    // get the node
    var node = this.nodes[element];

    // change the background on the selected node
    if (this.selectedElement != -1){
	if (browser.canDoDOM) {
	    var style = document.getElementById(this.selectedElement + "Content").style;
	    style.backgroundColor = this.selectedBG;
	}
    }

    // make the new node the slected node
    this.selectedElement = element;

    if (browser.canDoDOM) {
	var style = document.getElementById(element + "Content").style;
	this.selectedBG = style.backgroundColor;
	style.backgroundColor = this.selectColor;
    } else {
	// we'll use a layer for this and just move it around
	var selectLayer = eval ('document.list_' + this.name + '.layers["' + this.name + 'Select"]');
	var layer = eval ('document.list_' + this.name + '.layers["' + this.name + element + '"]');
	var layerDoc = eval('document.list_' + this.name + '.layers["' + this.name +  element + '"].document');
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

    // this is done to cancel click action
    return false;
}

/**
 * Select a Node from a HelpID
 */
function selectNodeFromHelpID(helpID) {
    var node = null;
    for (var i=0; i<this.nodes.length ;i++) {
	node = this.nodes[i];
	if (node.helpID == helpID) {
	    this.select(i);
	    this.scrollToTreeNode(node);
	    break;
	}
    }
}

/**
 * Node constuctor
 *
 * Create a TreeNode for use in a SearchList
 *
 * param content - content to display in List. Genrally the text.
 * param confidence - float confidence value
 * param hits - number of hits
 * param helpID - helpID for this item
 * param URLData - URL for the helpID to display
 */
function Node(content, confidence, hits, helpID, URLData) {
    this.content = content;
    this.confidence = confidence;
    this.hits = hits;
    this.helpID = helpID;
    this.URLData = URLData;
    this.lineNumber = 0;

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


