<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.Iterator,
				 java.util.SortedSet,
				 java.util.TreeSet,
				 org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.kiwisoft.media.*"%>
<%@ page import="com.kiwisoft.media.show.Show"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Genre genre=GenreManager.getInstance().getGenre(new Long(request.getParameter("genre")));
	SortedSet shows=new TreeSet(StringUtils.getComparator());
	shows.addAll(genre.getShows());
	Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
%>

<html>

<head>
<title>Shows - <%=StringEscapeUtils.escapeHtml(genre.getName())%></title>
<link type="text/css" rel="stylesheet" href="/style.css"/>
<script language="JavaScript" src="/overlib.js"></script>
</head>

<body>
<div id="overDiv" class="over_lib"></div>

<media:title>Shows</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%=StringEscapeUtils.escapeHtml(genre.getName())%>">
			<ul>
<%
				Iterator itShows=shows.iterator();
				while (itShows.hasNext())
				{
					Show show=(Show)itShows.next();
%>
					<li><b><a class=link href="<%=Navigation.getLink(show)%>"><%=StringEscapeUtils.escapeHtml(show.getTitle())%></a></b>
<%
					if (show.getLanguage()!=german)
					{
%>
						(<a class=link href="<%=Navigation.getLink(show)%>"><%=StringEscapeUtils.escapeHtml(show.getGermanTitle())%></a>)
<%
					}
				}
%>
			</ul>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
