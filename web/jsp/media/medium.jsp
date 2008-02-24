<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.medium.Medium,
				 com.kiwisoft.media.medium.MediumManager,
				 com.kiwisoft.media.medium.TracksTable,
				 com.kiwisoft.web.JspUtils"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String pId=request.getParameter("id");
	Medium medium=MediumManager.getInstance().getMedium(new Long(pId));
	request.setAttribute("tracksTable", new TracksTable(medium));
%>
<html>

<head>
<title>Medium - <%=medium.getFullKey()+": "+medium.getName()%></title>
<script language="JavaScript" src="../overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title>Media</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_media_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%="Medium "+medium.getFullKey()+": "+medium.getName()%>">
			<table>
			<tr><td class="content2"><b>Medium:</b></td><td class="content2"><%=JspUtils.render(request, medium.getType())%></td></tr>
			<tr><td class="content2"><b>Length:</b></td><td class="content2"><%=medium.getLength()%></td></tr>
			<tr><td class="content2"><b>Storage:</b></td><td class="content2"><%=medium.getStorage()%></td></tr>
			</table>
			<br>
			<media:table model="tracksTable" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
