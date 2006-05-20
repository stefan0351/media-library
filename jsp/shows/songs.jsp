<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import = "com.kiwisoft.media.MediaManagerApp,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   java.text.DateFormat,
				   java.text.SimpleDateFormat,
				   java.util.SortedSet,
				   java.util.TreeSet,
				   com.kiwisoft.media.AirdateComparator,
				   java.util.Iterator,
				   com.kiwisoft.media.Airdate,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.show.EpisodeInfo,
				   com.kiwisoft.xp.XPBean,
				   java.util.Collection" %>

<%
	Show show=ShowManager.getInstance().getShow(new Long(request.getParameter("show")));
	request.setAttribute("show", show);
	XPBean xp=(XPBean)request.getAttribute("xp");
%>
<html>

<head>
<title><%=show.getName()%> - Titelsong</title>
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
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2,'/shows/')" onMouseOut="nd()">Serien</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<%="themes".equals(xp.getName()) ? "Titelmusik" : "Musik"%></td></tr>
</table>
<%
	Collection values=xp.getValues("theme");
	if (values!=null)
	{
	for (Iterator it=values.iterator(); it.hasNext();)
	{
		XPBean theme=(XPBean)it.next();

%>
		<br>
		<table style="border: solid 1px lightgray" width="100%">
<%
		if (theme.getValue("description")!=null)
		{
%>		
			<tr><td colspan=2 bgcolor="#eeeeee"><b><%=theme.getValue("description")%></b></td></tr>
<%
		}
		if (theme.getValue("title")!=null)
		{
%>
			<tr><td width="100">Titel:</td><td><%=theme.getValue("title")%></td></tr>
<%
		}
		if (theme.getValue("composer")!=null)
		{
%>
			<tr><td width="100">Komponist:</td><td><%=theme.getValue("composer")%></td></tr>
<%
		}
		if (theme.getValue("interpret")!=null)
		{
%>
			<tr><td width="100">Interpret:</td><td><%=theme.getValue("interpret")%></td></tr>
<%
		}
		if (theme.getValue("source")!=null)
		{
%>
			<tr><td width="100">Datei:</td><td><a href="<%=theme.getValue("source")%>"><img src="/clipart/icons/sound.gif" border="0"></a> (<%=theme.getValue("length")%>)</td></tr>
<%
		}
		if (theme.getValue("lyrics")!=null)
		{
%>
			<tr valign=top><td width="100">Text:</td><td><%=theme.getValue("lyrics")%></td></tr>
<%
		}
%>
		</table>
<%
	}
	}
	values=xp.getValues("song");
	if (values!=null)
	{
	for (Iterator it=values.iterator(); it.hasNext();)
	{
		XPBean theme=(XPBean)it.next();

%>
		<br>
		<table style="border: solid 1px lightgray" width="100%">
<%
		Object title=theme.getValue("title");
		if (title!=null)
		{
			%><tr><td colspan=2 bgcolor="#eeeeee"><b><%=title%></b></td></tr><%
		}
		Object episode=theme.getValue("episode");
		if (episode!=null)
		{
			request.setAttribute("_episode", ShowManager.getInstance().getEpisode(show.getUserKey(), episode.toString()));
			%><tr><td width="100">Episode:</td><td><jsp:include page="_episode.jsp"/></td></tr><%
		}
		if (theme.getValue("composer")!=null)
		{
%>
			<tr><td width="100">Komponist:</td><td><%=theme.getValue("composer")%></td></tr>
<%
		}
		if (theme.getValue("interpret")!=null)
		{
%>
			<tr><td width="100">Interpret:</td><td><%=theme.getValue("interpret")%></td></tr>
<%
		}
		if (theme.getValue("source")!=null)
		{
%>
			<tr><td width="100">Datei:</td><td><a href="<%=theme.getValue("source")%>"><img src="/clipart/icons/sound.gif" border="0"></a> (<%=theme.getValue("length")%>)</td></tr>
<%
		}
		if (theme.getValue("lyrics")!=null)
		{
%>
			<tr valign=top><td width="100">Text:</td><td><%=theme.getValue("lyrics")%></td></tr>
<%
		}
%>
		</table>
<%
	}
	}
%>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
