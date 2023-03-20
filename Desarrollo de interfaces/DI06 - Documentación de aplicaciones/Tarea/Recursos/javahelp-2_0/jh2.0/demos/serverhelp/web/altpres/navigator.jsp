<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker" scope="session" />
<%@ page import="javax.help.NavigatorView" %>
<%@ taglib uri="/jhlib.tld" prefix="jh" %>
<HTML>
<STYLE type="text/css">
    .tabbedBodyStyle { margin-left:-2; margin-right:-2; margin-top:-2; margin-bottom:-2; background-color:#CCCCCC; }
    .tabbedAnchorStyle { text-decoration:none; color:black;}
</STYLE>
<BODY CLASS="tabbedBodyStyle">
    <TABLE WIDTH=100% CELLSPACING=5 CELLPADDING=0>
      <TR>

<jh:navigators helpBroker="<%= helpBroker %>" >
<td classtableDefStyle BGCOLOR="<%= isCurrentNav.booleanValue() ? "white" : "#E5E5E5" %>" ALIGN="center">
<A class=tabbedAnchorStyle HREF="navigator.jsp?nav=<%= name %>">
<%= tip %>
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
     top.treeframe.location = "../<%= curNav.getClass().getName() %>.jsp"
</SCRIPT
<%
} else {
%>
<SCRIPT> 
    top.treeframe.location = "../nonavigator.jsp"
</SCRIPT>
<%
}
%>
</BODY>
</HTML>
