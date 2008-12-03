<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Set,
				 org.apache.commons.lang.StringEscapeUtils,
				 com.kiwisoft.media.files.MediaFileManager,
				 com.kiwisoft.media.files.MediaType,
				 com.kiwisoft.media.show.Episode" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.media.show.ShowManager" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	request.setAttribute("season", episode.getSeason());
	MediaType mediaType=MediaType.valueOf(new Long(request.getParameter("type")));
	Set mediaFiles=MediaFileManager.getInstance().getMediaFiles(episode, mediaType);
	request.setAttribute("mediaFiles", mediaFiles);
	Show show=episode.getShow();
	request.setAttribute("show", show);
%>

<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%>
	- <%=StringEscapeUtils.escapeHtml(episode.getTitle())%>
	- <%=StringEscapeUtils.escapeHtml(mediaType.getPluralName())%></title>
<script language="JavaScript" src="../overlib.js"></script>
<script language="JavaScript" src="../popup.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title><%=StringEscapeUtils.escapeHtml(show.getTitle())%></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_episode_nav.jsp"/>
		<jsp:include page="_show_nav.jsp"/>
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%=mediaType.getPluralName()%>">
			<jsp:include page="/shows/_episode_next.jsp"/>
			<br/>
			<jsp:include page="/_mediagallery.jsp"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
