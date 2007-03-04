<%@ page import="com.kiwisoft.media.Language,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.utils.StringUtils"%>

<%
	Episode episode=(Episode)request.getAttribute("_episode");
%>			 
<%=episode.getUserKey()%>
<%
	Language language=episode.getShow().getLanguage();
	String originalTitle=null;
	if (language!=null && !"de".equals(language.getSymbol())) originalTitle=episode.getOriginalName();
	String name=episode.getName();
	if (StringUtils.isEmpty(name)) name="???";
%>
<b><a class="link" href="/shows/episode.jsp?episode=<%=episode.getId()%>&?language=de"><%=name%></a></b>
<%
	if (!StringUtils.isEmpty(originalTitle))
	{
%>
	(<a class="link" href="/shows/episode.jsp?episode=<%=episode.getId()%>&language=en"><%=originalTitle%></a>)
<%
	}
%>
