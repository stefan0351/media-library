<%@ page language="java" %>
<%@ page import="com.kiwisoft.media.movie.Movie,
				 java.util.Map,
				 java.util.TreeMap,
				 java.util.Iterator,
				 com.kiwisoft.media.movie.MovieInfo,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.movie.Movie,
				 com.kiwisoft.media.movie.MovieInfo"%>

<%
	Movie movie=(Movie)request.getAttribute("movie");
	Map links=new TreeMap(String.CASE_INSENSITIVE_ORDER);
	for (Iterator it=movie.getInfos().iterator(); it.hasNext();)
	{
		MovieInfo info=(MovieInfo)it.next();
		links.put(info.getName(), "/"+info.getPath()+"?movie="+movie.getId());
	}
	Show show=movie.getShow();
	if (show!=null) links.put("Serie", show.getLink());
%>

<table class="menutable">
<tr><td class="menuheader">Filme</td></tr>

<%
	for (Iterator itLinks=links.keySet().iterator(); itLinks.hasNext();)
	{
		String name=(String)itLinks.next();
		String ref=(String)links.get(name);
%>
<tr><td class="menuitem"><a class="menulink" href="<%=ref%>"><%=name%></a></td></tr>
<%
	}
%>
</table>
