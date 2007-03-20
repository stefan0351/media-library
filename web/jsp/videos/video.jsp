<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.MissingResourceException,
				 java.util.ResourceBundle,
				 com.kiwisoft.media.Language,
				 com.kiwisoft.media.MediaTableConfiguration,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.media.video.Recording,
				 com.kiwisoft.media.video.RecordsTable,
				 com.kiwisoft.media.video.Video,
				 com.kiwisoft.media.video.VideoManager,
				 com.kiwisoft.utils.gui.table.TableConstants"%>
<%@ page import="com.kiwisoft.utils.gui.table.TableSortDescription"%>
<%@ page import="com.kiwisoft.media.Navigation"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String pId=request.getParameter("id");
	Video video=null;
	if (pId!=null) video=VideoManager.getInstance().getVideo(new Long(pId));
	request.setAttribute("recordsTable", new RecordsTable(video));
%>
<html>

<head>
<title>Video<%=(video!=null ? " - "+video.getUserKey()+": "+video.getName() : "")%></title>
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
		<media:panel title="<%="Video "+(video!=null ? " - "+video.getUserKey()+": "+video.getName() : "")%>">
			<media:table model="recordsTable" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
