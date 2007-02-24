<%@ page language="java" %>
<%@ page import="java.util.Iterator,
				 java.util.SortedSet,
				 java.util.TreeSet,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.ShowType"%>

<table class="menutable">
<tr><td class="menuheader">Serien</td></tr>
<%
		SortedSet types=new TreeSet(new StringComparator());
		types.addAll(ShowType.getAll());
		Iterator itTypes=types.iterator();
		while (itTypes.hasNext())
		{
			ShowType type=(ShowType)itTypes.next();
%>
			<tr><td class="menuitem"><a class="menulink" href="/shows/index.jsp#type<%=type.getId()%>"><%=type.getName()%></a></td></tr>
<%
		}
%>
</table>
