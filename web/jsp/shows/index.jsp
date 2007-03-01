<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.Iterator,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowManager,
				 java.util.TreeSet,
				 java.util.SortedSet,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.LanguageManager,
				 com.kiwisoft.media.Language"%>
<%@ page import="com.kiwisoft.utils.db.DBLoader"%>
<%@ page import="com.kiwisoft.media.Genre"%>
<html>

<head>
<title>Serien</title>
<link type="text/css" rel="stylesheet" href="/style.css"/>
<script language="JavaScript" src="/overlib.js"></script>
</head>

<body>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Serien</div>
</div>

<div class="main">

<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</td>
	<td width="800">

<%
	SortedSet genres=new TreeSet(new StringComparator());
	genres.addAll(DBLoader.getInstance().loadSet(Genre.class));
	Iterator itGenres=genres.iterator();
	Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
	while (itGenres.hasNext())
	{
		Genre genre=(Genre)itGenres.next();
		SortedSet shows=new TreeSet(new StringComparator());
		shows.addAll(genre.getShows());
		if (!shows.isEmpty())
		{
%>
			<table class="contenttable" width="790">
			<tr><td class="header1"><a name="type<%=genre.getId()%>"><%=genre.getName()%></a></td></tr>
			<tr><td class="content">
				<ul>
<%
					Iterator itShows=shows.iterator();
					while (itShows.hasNext())
					{
						Show show=(Show)itShows.next();
%>
						<li><b><a class=link href="<%=show.getLink()%>"><%=show.getName()%></a></b>
<%
						if (show.getLanguage()!=german)
						{
%>
							(<a class=link href="<%=show.getLink()%>"><%=show.getOriginalName()%></a>)
<%
						}
					}
%>
				</ul>
			</td></tr>
			</table>
<%
		}
	}
%>
	</td></tr>
	</table>
</div>
</body>

</html>

<!--<div class="bg">-->
<!--<table border=0 cellspacing=0 cellpadding=0>-->
<!--<tr><td class="bg_top">&nbsp;</td></tr>-->
<!--<tr><td class=bg_middle valign=top>-->

<!--<div class="bg_page">-->
<!--Body-->

<!--


<p align=right><a class=link href="#top">Top</a></p>
<%
	SortedSet shows=new TreeSet(new StringComparator());
	shows.addAll(ShowManager.getInstance().getUntypedShows());
	if (!shows.isEmpty())
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<a name="others">Sonstige Serien</a></td></tr>
</table>

<ul>
<%
		Iterator itShows=shows.iterator();
		while (itShows.hasNext())
		{
			try {
			Show show=(Show)itShows.next();
%>
			<li><b><a class=link href="<%=show.getLink()%>"><%=show.getName()%></a></b>
<%
			if (show.getLanguage()!=german)
			{
%>
				(<a class=link href="<%=show.getLink()%>"><%=show.getOriginalName()%></a>)
<%
			}
				} catch (Exception e) { e.printStackTrace(); }
		}
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>
<%
	}
%>
-->
<!--Body Ende-->
