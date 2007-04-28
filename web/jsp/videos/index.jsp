<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.video.VideoManager,
				 com.kiwisoft.media.video.VideosTable"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String groupParameter=request.getParameter("group");
	int group=0;
	if ("all".equals(groupParameter)) group=-1;
	else if (groupParameter!=null) group=Integer.parseInt(groupParameter);
	int groupCount=VideoManager.getInstance().getGroupCount();
	request.setAttribute("videosTable", new VideosTable(group));
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

<media:title>Videos</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_videos_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Videos">
			<table>
			<tr valign="top"><td class="content2" width="100" align="center"><div style="border: 1px solid black; background: url(/clipart/trans10.png)"><small>
<%
				for (int i=0;i<groupCount;i++)
				{
%>
					<a class=link href="/videos/index.jsp?group=<%=i%>"><%=VideoManager.getGroupName(i)%></a><br/>
<%
				}
%>
				</small></div></td>
				<td><media:table model="videosTable" alternateRows="true"/></td>
			</tr></table>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
