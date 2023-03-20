<jsp:useBean id="helpBroker" class="javax.help.ServletHelpBroker" scope="session" />
<%@ taglib uri="/jhlib.tld" prefix="jh" %>
<html>
<head>
<jh:validate helpBroker="<%= helpBroker %>" helpSetName="JavaHelpDemo/animals/Animals.hs"/>
<jh:validate merge="<%= true %>" helpSetName="JavaHelpDemo/invertebrates/Invertebrates.hs" helpBroker="<%= helpBroker %>" />
<jh:validate merge="<%= true %>" helpSetName="JavaHelpDemo/vertebrates/Vertebrates.hs" helpBroker="<%= helpBroker %>" />
<title>Sample JavaHelpServer Application</title>
<SCRIPT>
      function openNewWindow (URL, windowName, windowOptions)
      {
          var window = getWindow (URL, windowName, windowOptions);
      }

      function getWindow(URL, windowName, windowOptions)
      {
          var newWindow = open (URL, windowName, windowOptions)
          newWindow.focus();
          top.allOpenWindows[top.allOpenWindows.length] = newWindow;
          return window;
      }
</SCRIPT>
</head>
<body bgcolor=white>

<table border="0">
<tr>
<td>
<img src="images/tomcat.gif">
</td>
<td>
<h1>Sample JavaHelp JSP Application</h1>
<p>This is the home page for a sample JavaHelp JSP used to illustrate the
usage of JavaHelp in a web application.
</td>
</tr>
</table>

<p>To prove that they work, you can execute either of the following links:
<ul>
<li>To <a href="javascript: openNewWindow('help.jsp', 'helpWindow', 'WIDTH=700,HEIGHT=500,resizable=yes');">Animals - A merged Helpset</a>.

<li>To <a href="javascript: openNewWindow('help.jsp?id=blackbear.picture', 'helpWindow', 'WIDTH=700,HEIGHT=500,resizable=yes');">Animals - Black Bear</a>.

</ul>

<br>
<p>An alternative presentation is in the following links:

<ul>
<li>To <a href="javascript: openNewWindow('altpres/help.jsp', 'helpWindow', 'WIDTH=700,HEIGHT=500,resizable=yes');">Animals - A merged Helpset</a>.

<li>To <a href="javascript: openNewWindow('altpres/help.jsp?id=blackbear.picture', 'helpWindow', 'WIDTH=700,HEIGHT=500,resizable=yes');">Animals - Black Bear</a>.

</ul>


</body>
</html>
