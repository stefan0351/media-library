<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.Navigation,
				 com.kiwisoft.media.show.Episode"%>
<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<table class="menutable">
<tr><td class="menuheader">Episode</td></tr>
<tr><td class="menuitem">
	<a class="menulink" href="<%=Navigation.getLink(request, episode)%>">Summary</a>
</td></tr>
<tr><td class="menuitem">
	<a class="menulink" href="<%=Navigation.getLink(request, episode)%>#production">Production</a>
</td></tr>
<tr><td class="menuitem">
	<a class="menulink" href="<%=Navigation.getLink(request, episode)%>#castAndCrew">Cast and Crew</a>
</td></tr>
</table>
