/*
 *
 * Copyright 2003 Sun Microsytems, Inc. All Rights Reserved.
 *
 */

   var staticCurrentURL = null;

   /*
    * check and see if any change in the content has occured
    * if it has fire an update with change
    */
    function checkContentsFrame() {
	var url;

	url = top.contentsFrame.document.URL;

	// if the staticCurrentURL hasn't been set then just set it
	if (staticCurrentURL == null) {
	    staticCurrentURL = url;
	} else {
	    // test if the staticCurrentURL equal the url
	    if (staticCurrentURL.indexOf(url) == -1) {
		// they are not equal
		// reload the updateFrame based on the url
		staticCurrentURL = url;
		top.updateframe.location = "update.jsp?url=" + url;
		return;
	    }
	}
	top.setTimeout("top.checkContentsFrame( );", 2000);
    }

function invoke(theURL, id) {
    top.contentsFrame.location = theURL;
    top.updateframe.location = "update.jsp?id=" + id;
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

    this.OS = ''
    var platform;
    if (typeof(window.navigator.platform) != 'undefined')
    {
	platform = window.navigator.platform.toLowerCase();
	if (platform.indexOf('win') != -1) {
	    this.OS = 'win';
	} else if (platform.indexOf('mac') != -1) {
	    this.OS = 'mac';
	} else if (platform.indexOf('unix') != -1 || platform.indexOf('linux') != -1 || platform.indexOf('sun') != -1) {
	    this.OS = 'nix';
	}
    }
}
