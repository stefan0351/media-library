<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.video.MediumType,
				 com.kiwisoft.media.video.VideosTable,
				 com.kiwisoft.web.JspUtils"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String typeId=request.getParameter("type");
	MediumType type=null;
	if (typeId!=null) type=MediumType.get(new Long(typeId));
	request.setAttribute("videosTable", new VideosTable(type));
%>
<html>

<head>
<title>Videos - <%=type!=null ? JspUtils.prepareString(type.getPluralName()) : "All"%></title>
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
		<media:panel title="<%=type!=null ? JspUtils.prepareString(type.getPluralName()) : "All"%>">
			<media:table model="videosTable" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
