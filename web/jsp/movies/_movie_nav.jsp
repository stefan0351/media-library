<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.movie.Movie,
				 com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.media.Navigation"%>
<%@ page import="com.kiwisoft.media.pics.Picture" %>
<%@ page import="com.kiwisoft.media.pics.Thumbnail" %>

<%
	Movie movie=(Movie)request.getAttribute("movie");
	Show show=movie.getShow();
%>

<table class="menutable">
<tr><td class="menuheader">Movie</td></tr>
<%
	String thumbnailPath=null;
	String posterPath=null;
	Picture poster=movie.getPoster();
	if (poster!=null)
	{
		Thumbnail thumbnail=poster.getThumbnailSidebar();
		if (thumbnail!=null) thumbnailPath=thumbnail.getFile();
		else if (poster.getWidth()<=170) thumbnailPath=poster.getFile();
		if (poster.getWidth()>170) posterPath=poster.getFile();
	}
	if (!StringUtils.isEmpty(thumbnailPath))
	{
%>
<tr><td class="menuitem" align="center"><img style="margin-top:5px" src="/<%=thumbnailPath.replace('\\', '/')%>"
<%
		if (!StringUtils.isEmpty(posterPath))
		{
%>
			onMouseOver="imagePopup('Poster', '/<%=posterPath.replace('\\', '/')%>')" onMouseOut="nd()"
<%
		}
%>
	></td></tr>
<tr><td><hr size=1 color=black></td></tr>
<%
	}
%>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(movie)%>#summary">Summary</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(movie)%>#details">Details</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(movie)%>#cast">Cast</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(movie)%>#crew">Crew</a></td></tr>
<%
	if (show!=null)
	{
%>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(show)%>">TV Show</a></td></tr>
<%
	}
%>
</table>
