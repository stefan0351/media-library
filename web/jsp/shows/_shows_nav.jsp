<%@ page language="java" %>
<%@ page import="java.util.Iterator,
				 java.util.SortedSet,
				 java.util.TreeSet,
				 com.kiwisoft.media.Genre"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.utils.db.DBLoader"%>


<table class="menutable">
<tr><td class="menuheader">Serien</td></tr>
<%
		SortedSet genres=new TreeSet(StringUtils.getComparator());
		genres.addAll(DBLoader.getInstance().loadSet(Genre.class));
		Iterator itGenres=genres.iterator();
		while (itGenres.hasNext())
		{
			Genre genre=(Genre)itGenres.next();
%>
			<tr><td class="menuitem"><a class="menulink" href="/shows/index.jsp#type<%=genre.getId()%>"><%=genre.getName()%></a></td></tr>
<%
		}
%>
</table>
