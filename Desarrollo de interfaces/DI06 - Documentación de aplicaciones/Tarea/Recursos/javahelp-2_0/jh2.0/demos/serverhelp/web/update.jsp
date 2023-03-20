<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker" scope="session" />
<%@ page import="java.net.URL, javax.help.HelpSet, javax.help.Map.ID" %>
<HTML>
<BODY STYLE="margin-left:-2;margin-right:-2;margin-top:-2;margin-bottom:-2" BGCOLOR=white>
<SCRIPT>
<%
// only a "url" or an "id" should be set.
// If for some reason both are set the url overrides the id 
String url = request.getParameter("url");
String id = request.getParameter("id");
if (url == null && id == null) {
    // nothing to do
    // in regular java code we would return.
    // we'll just else here
} else {
    // Try the URL first.
    // If a parameter has been past then there has been
    // a change in the contentframe that needs to be reflected in the
    // helpBroker and the navigator
    if (url != null) {
	URL curURL = helpBroker.getCurrentURL();
	URL testURL = new URL(url);
	if (!testURL.sameFile(curURL)) {
	    ID currentid = helpBroker.getCurrentID();
	    helpBroker.setCurrentURL(testURL);
	    ID mapid = helpBroker.getCurrentID();
	    // if the changed url translates into an id'
	    // update the navigatorframe 
	    // otherwise make sure that nothing is selected
	    // in the navigator frame
	    if (mapid != null && mapid != currentid) {
		%>
                top.findHelpID("<%= mapid.id %>");
		<%
	    } else {
		if (currentid != null) {
		    %>
 		    top.setSelected("<%= currentid.id %>", false);
		    <%
		}
	    }
	}
    } else {
	// no URL was specified how about an id?
	if (id != null) {
	    // Yep, just update the helpBroker
	    // The contentsframe has already been updated
	    helpBroker.setCurrentID(id);
	}
    }
}
%>
    top.setTimeout("top.checkContentsFrame( );", 2000);
</SCRIPT>
</BODY>
</HTML>
