<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.Navigation,
				 com.kiwisoft.media.movie.Movie" %>
<%@ page import="com.kiwisoft.media.pics.Picture"%>
<%@ page import="com.kiwisoft.media.pics.PictureFile" %>
<%@ page import="com.kiwisoft.media.show.Show" %>

<%
	Movie movie=(Movie)request.getAttribute("movie");
	Show show=movie.getShow();
%>

<table class="menutable">
<tr><td class="menuheader">Movie</td></tr>
<%
	PictureFile thumbnail=null;
	Picture poster=movie.getPoster();
	if (poster!=null)
	{
		thumbnail=poster.getThumbnailSidebar();
		if (thumbnail==null && poster.getWidth()<=170) thumbnail=poster;
	}
	if (thumbnail!=null)
	{
%>
<tr><td class="menuitem" align="center">
<%
		out.println(renderPicture(request, "Poster", poster, thumbnail, null));
%>
	</td></tr>
<tr><td><hr size=1 color=black></td></tr>
<%
	}
%>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, movie)%>#summary">Summary</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, movie)%>#details">Details</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, movie)%>#cast">Cast</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, movie)%>#crew">Crew</a></td></tr>
<%
	if (show!=null)
	{
%>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, show)%>">TV Show</a></td></tr>
<%
	}
%>
</table>
