<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.net.URLEncoder,
                 com.kiwisoft.utils.*,
				 com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.fanfic.*,
				 com.kiwisoft.media.ContactMedium,
				 com.kiwisoft.media.show.ShowManager,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.utils.db.Chain,
				 java.util.*,
				 com.kiwisoft.media.movie.MovieManager,
				 com.kiwisoft.media.movie.Movie"%>

<%
	Collection movies=MovieManager.getInstance().getMovies();
	AlphabeticalMap movieMap=new AlphabeticalMap();
	for (Iterator it=movies.iterator(); it.hasNext();)
	{
		Movie movie=(Movie)it.next();
		movieMap.put(movie.getName(), movie);
	}
%>
<html>

<head>
<title>Filme</title>
<script language="JavaScript" src="../../clipart/overlib.js"></script>
<script language="JavaScript" src="../nav.js"></script>
<script language="JavaScript" src="nav.js"></script>
<link rel="StyleSheet" type="text/css" href="../../clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo"><img style="margin-top:13px;" src="/movies/clipart/logo_mini.gif"></div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="margin-top:10px;font-weight:bold;font-size:24pt;">Filme</span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'../')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navFilms(2,'')" onMouseOut="nd()">Films</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Alle Filme</td></tr>
</table>
<br>
<table>
	<tr><td><small>[
<%
	for (Iterator it=movieMap.getCharacters().iterator(); it.hasNext();)
	{
        Character character=(Character)it.next();
%>
        <a class=link href="#<%=character%>"><%=character%></a>
<%
        if (it.hasNext()) out.print("|");
    }
%>
	]</small></td></tr>
</table>
<br>

<table>
<%
	for (Iterator it=movieMap.getCharacters().iterator(); it.hasNext();)
	{
		Character character=(Character)it.next();
%>
		<tr><td valign=top><b><a name="<%=character%>"><%=character%></a></b></td><td valign=top width=600><ul>
<%
		for (Iterator itMovies=movieMap.getKeys(character).iterator(); itMovies.hasNext();)
		{
			String name=(String)itMovies.next();
			Movie movie=(Movie)movieMap.get(name);
			String originalName=movie.getOriginalName();
			String link=movie.getLink();
			if (StringUtils.isEmpty(link))
			{
%>
				<li><b><%=movie.getName()%></b>
<%
			}
			else
			{
%>
				<li><b><a class="link" href="<%=link%>"><%=name%></a></b>
<%
			}
			if (!StringUtils.isEmpty(originalName)) out.print("("+originalName+")");
		}
%>
		</ul></td><td align=right valign=bottom><a class=link href="#top">Top</a></td></tr>
<%
	}
%>	
</table>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
