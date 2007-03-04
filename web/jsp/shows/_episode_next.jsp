<%@ page language="java" %>

<%@ page import="com.kiwisoft.media.show.Episode"%>
<%@ page import="com.kiwisoft.media.Language"%>
<%@ page import="com.kiwisoft.media.Resources"%>
<%@ page import="java.util.Locale"%>

<%
	Episode episode=(Episode)request.getAttribute("episode");
	Language language=(Language)request.getAttribute("language");
	Locale locale=new Locale(language.getSymbol());
%>

<table width="765"><tr>
<td class="content2">
<%
	Episode prev=episode.getPreviousEpisode();
	if (prev!=null)
	{
%>
		<a class=link href="/shows/episode.jsp?episode=<%=prev.getId()%>&language=<%=language.getSymbol()%>">
			&laquo; <%=Resources.getResource("episode.previous", locale)%> &laquo;
		</a>
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
		<a class=link href="/shows/episode.jsp?episode=<%=next.getId()%>&language=<%=language.getSymbol()%>">
			&raquo; <%=Resources.getResource("episode.next", locale)%> &raquo;
		</a>
<%
	}
%>
</td>
</tr></table>
