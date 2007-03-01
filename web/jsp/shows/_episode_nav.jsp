<%@ page language="java" %>
<%@ page import="java.util.Iterator,
				 java.util.TreeSet,
				 java.util.Set,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.EpisodeInfo"%>
<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table class="menutable">
<tr><td class="menuheader">Episode</td></tr>
<%
	Set infos=new TreeSet(new StringComparator());
	infos.addAll(episode.getInfos());
	for (Iterator it=infos.iterator(); it.hasNext();)
	{
		EpisodeInfo info=(EpisodeInfo)it.next();
%>
<tr><td class="menuitem"><a class="menulink" href="/<%=info.getPath()%>?episode=<%=episode.getId()%>"><%=info.getName()%></a></td></tr>
<%
	}
%>
</table>
