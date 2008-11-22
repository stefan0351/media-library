<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>

<%@ page import="java.util.Set" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.kiwisoft.media.files.MediaFileManager" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.person.PersonManager" %>
<%@ page import="com.kiwisoft.media.files.MediaType" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long personId=new Long(request.getParameter("person"));
	Person person=PersonManager.getInstance().getPerson(personId);
	request.setAttribute("person", person);
	Set mediaFiles=MediaFileManager.getInstance().getMediaFiles(person, MediaType.IMAGE);
	request.setAttribute("mediaFiles", mediaFiles);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(person.getName())%> - Images</title>
<link rel="StyleSheet" type="text/css" href="<%=request.getContextPath()%>/style.css">
<script language="JavaScript" src="<%=request.getContextPath()%>/overlib.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/popup.js"></script>
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title><%=StringEscapeUtils.escapeHtml(person.getName())%></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Images">
			<jsp:include page="/_mediagallery.jsp"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
