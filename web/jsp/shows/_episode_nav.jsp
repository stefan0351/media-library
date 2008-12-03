<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.Navigation,
				 com.kiwisoft.media.show.Episode"%>
<%@ page import="com.kiwisoft.media.files.MediaFileManager" %>
<%@ page import="com.kiwisoft.media.files.MediaType" %>
<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table class="menutable">
<tr><td class="menuheader">Episode</td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, episode)%>">Summary</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, episode)%>#production">Production</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=Navigation.getLink(request, episode)%>#castAndCrew">Cast and Crew</a></td></tr>
<%
	if (MediaFileManager.getInstance().getNumberOfMediaFiles(episode, MediaType.IMAGE)>0)
	{
%>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()+"/shows/mediafiles_episode.jsp?type="+MediaType.IMAGE.getId()+"&episode="+episode.getId()%>">Images</a>
<%
	}
	if (MediaFileManager.getInstance().getNumberOfMediaFiles(episode, MediaType.VIDEO)>0)
	{
%>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()+"/shows/mediafiles_episode.jsp?type="+MediaType.VIDEO.getId()+"&episode="+episode.getId()%>">Videos</a>
<%
	}
%>
</table>
