<%@ page language="java" %>
<%@ page import="com.kiwisoft.xp.XPBean,
                 java.util.Collection,
                 java.util.Iterator,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.Season,
				 java.util.Collections,
				 java.util.TreeSet,
				 com.kiwisoft.media.fanfic.FanDom,
				 java.util.Set,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.EpisodeInfo"%>

<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table width=690><tr>
<td><%
	Episode prev=episode.getPreviousEpisode();
	if (prev!=null && prev.getDefaultInfo()!=null)
	{%>
		<a class=link href="/<%=prev.getDefaultInfo().getPath()%>?episode=<%=prev.getId()%>">&laquo; Previous Episode &laquo;</a>
	<%}
%></td>
<td align=right><%
	Episode next=episode.getNextEpisode();
	if (next!=null && next.getDefaultInfo()!=null)
	{%>
		<a class=link href="/<%=next.getDefaultInfo().getPath()%>?episode=<%=next.getId()%>">&raquo; Next Episode &raquo;</a>
	<%}
%></td>
</tr></table>
