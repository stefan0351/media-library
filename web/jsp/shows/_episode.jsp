<%@ page import="com.kiwisoft.media.Language,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.media.Navigation"%>

<%
	Episode episode=(Episode)request.getAttribute("_episode");
%>			 
<%=episode.getUserKey()%>
<%
	Language language=episode.getShow().getLanguage();
	String germanTitle=null;
	if (language!=null && !"de".equals(language.getSymbol())) germanTitle=episode.getGermanTitle();
	String title=episode.getTitle();
	if (StringUtils.isEmpty(title)) title="???";
%>
<b><a class="link" href="<%=Navigation.getLink(episode)%>"><%=title%></a></b>
<%
	if (!StringUtils.isEmpty(germanTitle))
	{
%>
	(<%=germanTitle%>)
<%
	}
%>
