<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>

<%@ page import="java.util.Set" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.kiwisoft.media.files.MediaFileManager" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.media.show.ShowManager" %>
<%@ page import="com.kiwisoft.media.files.MediaType" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
	Set mediaFiles=MediaFileManager.getInstance().getMediaFiles(show, MediaType.VIDEO);
	request.setAttribute("mediaFiles", mediaFiles);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%> - Videos</title>
<link rel="StyleSheet" type="text/css" href="<%=request.getContextPath()%>/style.css">
<script language="JavaScript" src="<%=request.getContextPath()%>/overlib.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/popup.js"></script>
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title><%=StringEscapeUtils.escapeHtml(show.getTitle())%></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_show_nav.jsp" />
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="../_nav.jsp" />
	</media:sidebar>
	<media:content>
		<media:panel title="Videos">
			<jsp:include page="/_mediagallery.jsp"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
