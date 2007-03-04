<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.Language,
				 com.kiwisoft.media.show.Episode"%>
<%
	Episode episode=(Episode)request.getAttribute("episode");
	Language language=(Language)request.getAttribute("language");
%>

<table class="menutable">
<tr><td class="menuheader">Episode</td></tr>
<tr><td class="menuitem">
	<a class="menulink" href="/shows/episode.jsp?episode=<%=episode.getId()%>&language=<%=language.getSymbol()%>">Kurzbeschreibung</a>
</td></tr>
<tr><td class="menuitem">
	<a class="menulink" href="/shows/episode.jsp?episode=<%=episode.getId()%>&language=<%=language.getSymbol()%>#production">Produktion</a>
</td></tr>
<tr><td class="menuitem">
	<a class="menulink" href="/shows/episode.jsp?episode=<%=episode.getId()%>&language=<%=language.getSymbol()%>#castAndCrew">Besetzung und Stab</a>
</td></tr>

<tr><td><hr size=1 color=black></td></tr>

<tr><td class="menuitem">
<%
	if ("de".equals(language.getSymbol()))
	{
%>
	<a class="menulink" href="/shows/episode.jsp?episode=<%=episode.getId()%>&language=en">English</a>
<%
	}
	else
	{
%>
	<a class="menulink" href="/shows/episode.jsp?episode=<%=episode.getId()%>&language=de">Deutsch</a>
<%
	}
%>
</td></tr>
</table>
