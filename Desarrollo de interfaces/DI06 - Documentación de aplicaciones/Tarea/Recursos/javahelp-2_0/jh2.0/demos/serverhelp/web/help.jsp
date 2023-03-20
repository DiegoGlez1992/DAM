<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker" scope="session" />
<%@ taglib uri="/jhlib.tld" prefix="jh" %>
<html>
<head>
<%
// only an "id" should be set.
String id = request.getParameter("id");
if (id == null) {
    // nothing to do
    // in regular java code we would return.
} else {
    if (id != null) {
        // Yep, just update the helpBroker
	// The contentsframe has already been updated
	helpBroker.setCurrentID(id);
    }
}
%>
<title>JavaHelp Server Demo</title>
<SCRIPT LANGUAGE="JavaScript1.3" src="util.js">
</SCRIPT>
</head>
<FRAMESET ROWS="120,*" NAME="helptop" BORDER=0 FRAMESPACING=0>
    <FRAMESET COLS="*,0" NAME="upperframe" NORESIZE FRAMEBORDER=NO>
	<FRAME SRC="banner.html" NAME="bannerframe" SCROLLING="NO">
	<FRAME SRC="update.jsp" NAME="updateframe">
    </FRAMESET>
    <FRAMESET COLS="30%,70%" NAME="lowerhelp" BORDER=5 FRAMESPACING=5 FRAMEBORDER=YES>
	<FRAMESET ROWS="40,*" NAME="navigatortop" BORDER=0 FRAMESPACING=0>
	    <FRAME SRC="navigator.jsp" NAME="navigatorframe" SCROLLING="NO" FRAMEBORDER=NO>
	    <FRAME SRC="loading.html" NAME="treeframe" SCROLLING="AUTO" FRAMEBORDER=NO>
	</FRAMESET>
	<FRAMESET ROWS="40,*" NAME="rightpane">
	    <FRAME SRC="toolbar.html" NAME="toolbarframe" SCROLLING="NO" FRAMEBORDER=YES>
	    <FRAME SRC="<jsp:getProperty name="helpBroker" property="currentURL" />" NAME="contentsFrame" SCROLLING="AUTO" FRAMEBORDER=Yes>
	</FRAMESET>
    </FRAMESET>
</FRAMESET>
<NOFRAMES>
<BODY bgcolor=white>
Help requires frames to be displayed
</BODY>
</NOFRAMES> 
</HTML>
