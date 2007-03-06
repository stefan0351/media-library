<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.movie.Movie,
				 com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.utils.StringUtils"%>

<%
	Movie movie=(Movie)request.getAttribute("movie");
	Show show=movie.getShow();
%>

<table class="menutable">
<tr><td class="menuheader">Movie</td></tr>
<%
	String posterPath=movie.getPosterMini();
	if (!StringUtils.isEmpty(posterPath))
	{
%>
<tr><td class="menuitem" align="center"><img style="margin-top:5px" src="/<%=posterPath%>"></td></tr>
<tr><td><hr size=1 color=black></td></tr>
<%
	}
%>
<tr><td class="menuitem"><a class="menulink" href="/movies/movie.jsp?movie=<%=movie.getId()%>#summary">Summary</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="/movies/movie.jsp?movie=<%=movie.getId()%>#details">Details</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="/movies/movie.jsp?movie=<%=movie.getId()%>#cast">Cast</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="/movies/movie.jsp?movie=<%=movie.getId()%>#crew">Crew</a></td></tr>
<%
	if (show!=null)
	{
%>
<tr><td class="menuitem"><a class="menulink" href="<%=show.getLink()%>">TV Show</a></td></tr>
<%
	}
%>
</table>
