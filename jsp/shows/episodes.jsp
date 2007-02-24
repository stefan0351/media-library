<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.TreeSet,
				   java.util.Iterator,
				   com.kiwisoft.media.show.*,
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
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;"><%=show.getName()%></div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

<jsp:include page="/_nav.jsp"/>
<jsp:include page="_shows_nav.jsp"/>
<jsp:include page="_show_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<%
	if (!seasons.isEmpty())
	{
		Iterator it=seasons.iterator();
		while (it.hasNext())
		{
			Season season=(Season)it.next();
			Iterator itEpisodes=new TreeSet(season.getEpisodes()).iterator();
%>
<table class="contenttable" width="790">
<tr><td class="header1"><a name="season<%=season.getNumber()%>"><%=season%></a></td></tr>
<tr><td class="content"><ul>
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
</td></tr>
</table>
<%
		}
	}
	else
	{
%>
<table class="contenttable" width="790">
<tr><td class="header1">Episoden</td></tr>
<tr><td class="content"><ul>
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
</td></tr>
</table>
<%
	}

	Collection movies=show.getMovies();
	if (!movies.isEmpty())
	{
%>
<table class="contenttable" width="790">
<tr><td class="header1"><a name="movies">Filme</a></td></tr>
<tr><td class="content"><ul>
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
</td></tr>
</table>
<%
	}
%>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
