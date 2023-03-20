<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker" scope="session" />
<%@ page import="javax.help.IndexView, javax.help.Map.ID" %>
<%@ taglib uri="/jhlib.tld" prefix="jh" %>
<HTML>
<STYLE type="text/css">
    .anchorStyle { text-decoration:none; color:black; }
</STYLE>
<BODY BGCOLOR=white>
<SCRIPT LANGUAGE="JavaScript1.3" src="tree.js">
</SCRIPT>

<SCRIPT>
indexTree = new Tree("indexTree", 22, "ccccff", false, true);
<% IndexView curNav = (IndexView)helpBroker.getCurrentNavigatorView(); %>
<jh:indexItem indexView="<%= curNav %>" helpBroker="<%= helpBroker %>" >
indexTree.addTreeNode("<%= parentID %>", "<%= nodeID %>", "null", "<%= name %>","<%= helpID %>","<%= contentURL!=""?contentURL:"null" %>", "<%= expansionType%>");
</jh:indexItem>
indexTree.drawTree();
indexTree.refreshTree();
<% 
ID id = helpBroker.getCurrentID();
if (id != null) {
%> 
    indexTree.selectFromHelpID("<%= id.id%>");
<%
}
%>
</SCRIPT>
</BODY>


