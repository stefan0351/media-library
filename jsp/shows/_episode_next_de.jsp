<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.show.Episode"%>

<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table width=690><tr>
<td><%
	Episode prev=episode.getPreviousEpisode();
	if (prev!=null && prev.getDefaultInfo()!=null)
	{%>
		<a class=link href="/<%=prev.getDefaultInfo().getPath()%>?episode=<%=prev.getId()%>">&laquo; Vorherige Folge &laquo;</a>
	<%}
%></td>
<td align=right><%
	Episode next=episode.getNextEpisode();
	if (next!=null && next.getDefaultInfo()!=null)
	{%>
		<a class=link href="/<%=next.getDefaultInfo().getPath()%>?episode=<%=next.getId()%>">&raquo; N&auml;chste Folge &raquo;</a>
	<%}
%></td>
</tr></table>
