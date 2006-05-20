<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "com.kiwisoft.media.MediaManagerApp,
				   java.util.Collection,
				   java.util.TreeSet,
				   java.util.Iterator,
				   com.kiwisoft.media.show.*,
				   com.kiwisoft.media.movie.Movie,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.movie.Movie" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
	Collection seasons=new TreeSet(show.getSeasons());
%>
<html>

<head>
<title><%=show.getName()%> - Episoden</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<jsp:include page="_shows_nav.jsp"/>
<jsp:include page="_show_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<jsp:include page="/shows/_show_logo.jsp"/>
	</td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
		<span style="font-weight:bold;font-size:24pt;"><%=show.getName()%></span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2)" onMouseOut="nd()">Serien</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<%
	if (!seasons.isEmpty())
	{
		Iterator it=seasons.iterator();
		while (it.hasNext())
		{
			Season season=(Season)it.next();
			Iterator itEpisodes=new TreeSet(season.getEpisodes()).iterator();
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<a name="season<%=season.getNumber()%>"><%=season%></a></td></tr>
</table>

<ul>
<%
			while (itEpisodes.hasNext())
			{
				Episode episode=(Episode)itEpisodes.next();
				request.setAttribute("_episode", episode);
%>
				<li><jsp:include page="_episode.jsp"/>
<%
			}
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>

<%
		}
	}
	else
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Episoden</td></tr>
</table>

<ul>
<%
		Iterator it=show.getEpisodes().iterator();
		while (it.hasNext())
		{
			Episode episode=(Episode)it.next();
			request.setAttribute("_episode", episode);
%>
			<li><jsp:include page="_episode.jsp"/>
<%
		}
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>
<%
	}

	Collection movies=show.getMovies();
	if (!movies.isEmpty())
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<a name="movies">Filme</a></td></tr>
</table>

<ul>
<%
		Iterator itMovies=movies.iterator();
		while (itMovies.hasNext())
		{
			Movie movie=(Movie)itMovies.next();
			String link=movie.getLink();
			if (!StringUtils.isEmpty(link))
			{
%>
				<li><b><a class="link" href="<%=link%>"><%=movie.getName()%></a></b>
<%
			}
			else
			{
%>
				<li><b><%=movie.getName()%></b>
<%
			}
		}
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>
<%
	}
%>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
