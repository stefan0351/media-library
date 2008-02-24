<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.Iterator,
				 java.util.SortedSet,
				 java.util.TreeSet,
				 org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.kiwisoft.media.*"%>
<%@ page import="com.kiwisoft.media.show.Show"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.web.JspUtils" %>
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
		<media:panel title="<%=StringEscapeUtils.escapeHtml(genre.getName())%>">
			<ul>
<%
				for (Iterator itShows=shows.iterator(); itShows.hasNext();)
				{
					Show show=(Show)itShows.next();
%>
					<li><b><%=JspUtils.render(request, show)%></b>
<%
					String yearString=show.getYearString();
					if (yearString!=null)
					{
						out.print(" (");
						out.print(yearString);
						out.println(")");
					}
					if (show.getLanguage()!=german)
					{
						out.print("<br>a.k.a. <i>");
						out.print(StringEscapeUtils.escapeHtml(show.getGermanTitle()));
						out.println("</i>");
					}
				}
%>
			</ul>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
