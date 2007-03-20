<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowManager,
				 com.kiwisoft.media.show.ShowRecordsTable"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Show show=ShowManager.getInstance().getShow(new Long(request.getParameter("show")));
	request.setAttribute("show", show);
	ShowRecordsTable model=new ShowRecordsTable(show);
	request.setAttribute("recordsTable", model);
%>
<html>

<head>
<title>Videos</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;"><%=show.getTitle()%></div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

	<jsp:include page="_show_nav.jsp"/>
	<jsp:include page="_shows_nav.jsp"/>
	<jsp:include page="/_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1">Records</td></tr>
<tr><td class="content">
	<media:table model="recordsTable" alternateRows="true"/>
	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
