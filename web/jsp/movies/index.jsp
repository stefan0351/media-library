<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.utils.*,
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
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Filme</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

<jsp:include page="/_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1">&Uuml;bersicht</td></tr>
<tr><td class="content">
	<table width="765">
	<tr><td class="content2"><small>[
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
	<table width="765">
<%
	for (Iterator it=movieMap.getCharacters().iterator(); it.hasNext();)
	{
		Character character=(Character)it.next();
%>
	<tr valign=top><td class="content2" width="20"><b><a name="<%=character%>"><%=character%></a></b></td><td class="content2" width=700><ul>
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
		</ul></td><td class="content2" align=right valign=bottom><a class=link href="#top">Top</a></td></tr>
<%
	}
%>
	</table>
</td></tr>
</table>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
