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

<script language="JavaScript"><!--
function navFilm(pos)
{
	text="";
<%
	for (Iterator itLinks=links.keySet().iterator(); itLinks.hasNext();)
	{
		String name=(String)itLinks.next();
		String ref=(String)links.get(name);
%>
			text+="<a onClick=\"nd()\" class=link2_nav href=\"<%=ref%>\"><%=name%></a><br>";
<%
	}
%>
	return overlib(text,STICKY,CAPTION,"Film",FIXX,pos*150-50,FIXY,140,WIDTH,150,CAPCOLOR,"white",BGCOLOR,"black",FGCOLOR,"white");
}
//--></script>
