<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.show.ShowType,
				 java.util.Iterator,
				 java.util.Collection,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowManager,
				 java.util.TreeSet,
				 java.util.SortedSet,
				 com.kiwisoft.utils.StringComparator,
				 java.util.Set,
				 com.kiwisoft.media.LanguageManager,
				 com.kiwisoft.media.Language"%>

<html>

<head>
<title>Serien</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<jsp:include page="_shows_nav.jsp"/>
<link rel="StyleSheet" type="text/css" href="<%=request.getContextPath()%>/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="title">
    <table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;">Serien</span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'../')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2)" onMouseOut="nd()">Serien</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->

<%
	SortedSet types=new TreeSet(new StringComparator());
	types.addAll(ShowType.getAll());
	Iterator itTypes=types.iterator();
	Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
	while (itTypes.hasNext())
	{
		ShowType type=(ShowType)itTypes.next();
		SortedSet shows=new TreeSet(new StringComparator());
		shows.addAll(type.getShows());
		if (!shows.isEmpty())
		{
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<a name="type<%=type.getId()%>"><%=type.getName()%></a></td></tr>
</table>

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

<p align=right><a class=link href="#top">Top</a></p>
<%
		}
	}

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
