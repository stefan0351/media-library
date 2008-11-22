<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%> - Images</title>
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
		<media:panel title="Images">
			<jsp:include page="_gallery.jsp"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
