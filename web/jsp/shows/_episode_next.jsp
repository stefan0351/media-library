<%@ page language="java" %>

<%@ page import="com.kiwisoft.media.Navigation" %>
<%@ page import="com.kiwisoft.media.show.Episode" %>

<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table width="765">
<tr>
	<td class="content2">
		<%
			Episode prev=episode.getPreviousEpisode();
			if (prev!=null)
			{
		%>
		<a class=link href="<%=Navigation.getLink(prev)%>">&laquo; Previous Episode &laquo;</a>
		<%
			}
		%>
	</td>
	<td class="content2" align=right>
		<%
			Episode next=episode.getNextEpisode();
			if (next!=null)
			{
		%>
		<a class=link href="<%=Navigation.getLink(next)%>">&raquo; Next Episode &raquo;</a>
		<%
			}
		%>
	</td>
</tr>
</table>
