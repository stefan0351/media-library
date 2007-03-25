<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.video.RecordsTable,
				 com.kiwisoft.media.video.Video,
				 com.kiwisoft.media.video.VideoManager,
				 com.kiwisoft.web.JspUtils"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String pId=request.getParameter("id");
	Video video=VideoManager.getInstance().getVideo(new Long(pId));
	request.setAttribute("recordsTable", new RecordsTable(video));
%>
<html>

<head>
<title>Video - <%=video.getUserKey()+": "+video.getName()%></title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title>Videos</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_videos_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%="Video "+video.getUserKey()+": "+video.getName()%>">
			<table>
			<tr><td class="content2"><b>Medium:</b></td><td class="content2"><%=JspUtils.render(video.getType())%></td></tr>
			<tr><td class="content2"><b>Length:</b></td><td class="content2"><%=video.getLength()%></td></tr>
			<tr><td class="content2"><b>Storage:</b></td><td class="content2"><%=video.getStorage()%></td></tr>
			</table>
			<br>
			<media:table model="recordsTable" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
