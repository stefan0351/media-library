<%@ page language="java" %>
<%@ page import="java.util.Iterator,
				 java.util.Map,
				 java.util.TreeMap,
				 com.kiwisoft.media.video.MediumType"%>

<table class="menutable">
<tr><td class="menuheader">Videos</td></tr>
<tr><td class="menuitem"><a class="menulink" href="/videos/index.jsp">All</a></td></tr>
<%
	Map types=new TreeMap(String.CASE_INSENSITIVE_ORDER);
	for (Iterator it=MediumType.getAll().iterator(); it.hasNext();)
	{
		MediumType type=(MediumType)it.next();
		types.put(type.getPluralName(), type);
	}
	for (Iterator it=types.keySet().iterator(); it.hasNext();)
	{
		MediumType type=(MediumType)types.get(it.next());
%>
<tr><td class="menuitem"><a class="menulink" href="/videos/index.jsp?type=<%=type.getId()%>"><%=type.getPluralName()%></a></td></tr>
<%
	}
%>
<tr><td><hr size=1 color=black></td></tr>
<tr><td class="menuitem"><a class="menulink" href="/videos/print.pdf?xsl=/videos/print.xsl">Printable Version</a></td></tr>
</table>
