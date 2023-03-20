<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker" scope="session" />
<%@ page import="javax.help.NavigatorView" %>
<%@ taglib uri="/jhlib.tld" prefix="jh" %>
<HTML>
<STYLE type="text/css">
    .tabbedBodyStyle { margin-left:-2; margin-right:-2; margin-top:-2; margin-bottom:-2; background-color:white; }
    .tableStyle {border-width:10 10 0 10; border-color:#CCCCCC; border-style:solid; }
    .tabbedAnchorStyle { text-decoration:none; color:black;}
    .tableDefStyle {padding-top:5; padding-left:5; padding-right:5; padding-bottom:0;}
</STYLE>
<BODY CLASS="tabbedBodyStyle">
    <TABLE CLASS="tableStyle" WIDTH=100% BORDER=1 CELLSPACING=0 CELLPADDING=5>
      <TR BGCOLOR="#CCCCCC">

<jh:navigators helpBroker="<%= helpBroker %>" >
<td classtableDefStyle BGCOLOR="<%= isCurrentNav.booleanValue() ? "white" : "#E5E5E5" %>" ALIGN="center">
<A class=tabbedAnchorStyle HREF="navigator.jsp?nav=<%= name %>">
<IMG src="<%= iconURL!=""? iconURL : "images/" + className +".gif" %>" Alt="<%= tip %>" border=0>
</A>
</td>
</jh:navigators>
      </TR>
    </TABLE>
<%@ page import="javax.help.HelpSet,javax.help.NavigatorView" %>
<%
NavigatorView curNav = helpBroker.getCurrentNavigatorView();
if (curNav != null) {
%>
<SCRIPT>
     top.treeframe.location = "<%= curNav.getClass().getName() %>.jsp"
</SCRIPT
<%
} else {
%>
<SCRIPT> 
    top.treeframe.location = "nonavigator.jsp"
</SCRIPT>
<%
}
%>
</BODY>
</HTML>
