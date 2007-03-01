<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.show.Episode"%>

<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table width="765"><tr>
<td class="content2">
<%
	Episode prev=episode.getPreviousEpisode();
	if (prev!=null && prev.getDefaultInfo()!=null)
	{
%>
	<a class=link href="/<%=prev.getDefaultInfo().getPath()%>?episode=<%=prev.getId()%>">&laquo; Vorherige Folge &laquo;</a>
<%
	}
%>
</td>
<td class="content2" align=right>
<%
	Episode next=episode.getNextEpisode();
	if (next!=null && next.getDefaultInfo()!=null)
	{
%>
	<a class=link href="/<%=next.getDefaultInfo().getPath()%>?episode=<%=next.getId()%>">&raquo; N&auml;chste Folge &raquo;</a>
<%
	}
%>
</td>
</tr></table>
