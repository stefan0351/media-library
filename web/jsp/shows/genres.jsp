<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.Genre,
				 com.kiwisoft.media.GenreManager,
				 com.kiwisoft.utils.StringUtils,
				 com.kiwisoft.web.HTMLRenderer" %>
<%@ page import="com.kiwisoft.web.HTMLRendererManager" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kiwisoft.web.WebContext" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<html>

<head>
<title>Shows - Genres</title>
<link type="text/css" rel="stylesheet" href="../style.css"/>
<script language="JavaScript" src="../overlib.js"></script>
</head>

<body>
<div id="overDiv" class="over_lib"></div>

<media:title>Shows</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Genres">
			<ul>
			<%
				SortedSet genres=new TreeSet(StringUtils.getComparator());
				genres.addAll(GenreManager.getInstance().getGenres());
				HTMLRenderer renderer=HTMLRendererManager.getInstance().getRenderer(Genre.class);
				for (Iterator it=genres.iterator(); it.hasNext();)
				{
					Genre genre=(Genre)it.next();
					out.print("<li>");
					out.print(renderer.getContent(genre, new WebContext(request), 0, 0));
					out.println("</li>");
				}
			%>
			</ul>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
