<%@ page import="com.kiwisoft.media.Language,
				 com.kiwisoft.media.show.EpisodeInfo,
				 com.kiwisoft.utils.StringUtils,
				 com.kiwisoft.media.show.Episode"%>

<%
	Episode episode=(Episode)request.getAttribute("_episode");
%>			 
<%=episode.getUserKey()%>
<%
	EpisodeInfo link=episode.getDefaultInfo();
	Language language=episode.getShow().getLanguage();
	String originalTitle=null;
	if (language!=null && !"de".equals(language.getSymbol())) originalTitle=episode.getOriginalName();
	String name=episode.getName();
	if (StringUtils.isEmpty(name)) name="???";
	if (link!=null)
	{
%>
		<b><a class="link" href="/<%=link.getPath()%>?episode=<%=episode.getId()%>"><%=name%></a></b>
<%
		if (!StringUtils.isEmpty(originalTitle))
		{
%>
			(<a class="link" href="/<%=link.getPath()%>?episode=<%=episode.getId()%>"><%=originalTitle%></a>)
<%
		}
	}
	else
	{
%>
		<b><%=name%></b>
<%
		if (!StringUtils.isEmpty(originalTitle))
		{
%>
			(<%=originalTitle%>)
<%
		}
	}
%>