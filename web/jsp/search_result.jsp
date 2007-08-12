<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Collections,
				 java.util.Iterator" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.kiwisoft.media.movie.Movie" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.show.Episode" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.collection.SetMap" %>
<%@ page import="com.kiwisoft.web.HTMLRenderer" %>
<%@ page import="com.kiwisoft.web.HTMLRendererManager" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<html>

<head>
<title>Media Manager - Search Result</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title>Media Manager</media:title>

<media:body>
<media:sidebar>
	<jsp:include page="/_nav.jsp"/>
</media:sidebar>
<media:content>
<media:panel title="Search Result">
	<%
		Set shows=(Set)request.getAttribute("shows");
	%>
	<p><b><%=shows!=null ? shows.size() : 0%> Show(s) found</b>
		<%
			HTMLRendererManager rendererManager=HTMLRendererManager.getInstance();
			if (shows!=null && !shows.isEmpty())
			{
		%>
		<ol>
		<%
			HTMLRenderer renderer=rendererManager.getRenderer(Show.class);
			for (Iterator it=shows.iterator(); it.hasNext();)
			{
				Show show=(Show)it.next();
				out.print("<li>");
				out.print(renderer.getContent(show, Collections.EMPTY_MAP, 0, 0));
				out.println("</li>");
			}
		%>
		</ol>
		<%
			}
		%>
	</p>
	<%
		SetMap episodes=(SetMap)request.getAttribute("episodes");
	%>
	<p><b><%=episodes!=null ? episodes.getChildrenCount() : 0%> Episode(s) found</b>
		<%
			if (episodes!=null && !episodes.isEmpty())
			{
		%>
		<ol>
		<%
			HTMLRenderer showRenderer=rendererManager.getRenderer(Show.class);
			HTMLRenderer episodeRenderer=rendererManager.getRenderer(Episode.class);
			for (Iterator itShows=episodes.keySet().iterator(); itShows.hasNext();)
			{
				Show show=(Show)itShows.next();
				out.print("<li>");
				out.println(showRenderer.getContent(show, Collections.EMPTY_MAP, 0, 0));
				for (Iterator itEpisodes=episodes.get(show).iterator(); itEpisodes.hasNext();)
				{
					Episode episode=(Episode)itEpisodes.next();
					out.print("<br>- ");
					out.print(episodeRenderer.getContent(episode, Collections.EMPTY_MAP, 0, 0));
				}
				out.println("</li>");
			}
		%>
		</ol>
		<%
			}
		%>
	</p>
	<%
		Set movies=(Set)request.getAttribute("movies");
	%>
	<p><b><%=movies!=null ? movies.size() : 0%> Movie(s) found</b>
		<%
			if (movies!=null && !movies.isEmpty())
			{
		%>
		<ol>
		<%
			HTMLRenderer renderer=rendererManager.getRenderer(Movie.class);
			for (Iterator it=movies.iterator(); it.hasNext();)
			{
				Movie movie=(Movie)it.next();
				out.print("<li>");
				out.print(renderer.getContent(movie, Collections.EMPTY_MAP, 0, 0));
				Integer year=movie.getYear();
				if (year!=null)
				{
					out.print(" (");
					out.print(year);
					out.print(")");
				}
				out.println("</li>");
			}
		%>
		</ol>
		<%
			}
		%>
	</p>
	<%
		Set persons=(Set)request.getAttribute("persons");
	%>
	<p><b><%=persons!=null ? persons.size() : 0%> Person(s) found</b>
		<%
			if (persons!=null && !persons.isEmpty())
			{
		%>
		<ol>
		<%
			HTMLRenderer renderer=rendererManager.getRenderer(Person.class);
			for (Iterator it=persons.iterator(); it.hasNext();)
			{
				Person person=(Person)it.next();
				out.print("<li>");
				out.print(renderer.getContent(person, Collections.EMPTY_MAP, 0, 0));
				out.println("</li>");
			}
		%>
		</ol>
		<%
			}
		%>
	</p>
</media:panel>
</media:content>
</media:body>

</body>
</html>
