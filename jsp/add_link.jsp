<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import = "java.util.Set,
				   java.util.TreeSet,
				   com.kiwisoft.utils.StringComparator,
				   com.kiwisoft.media.show.ShowManager,
				   java.util.Iterator,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.fanfic.FanFicManager,
				   com.kiwisoft.media.fanfic.FanDom" %>

<%
	String name=request.getParameter("name");
	String url=request.getParameter("url");
%>
<html>

<head>
<title>Links</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	</td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
		<span style="font-weight:bold;font-size:24pt;">Links</span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Link hinzufügen</td></tr>
</table>
<br>
<form action="/add_link">

<table border=0>
<tr><td><b>Serie:</b></td><td>
<select name="show">
	<option value="empty">Wähle Serie</option>
<%
	Set shows=new TreeSet(new StringComparator());
	shows.addAll(ShowManager.getInstance().getShows());
 	for (Iterator it=shows.iterator();it.hasNext();)
 	{
    	Show show=(Show)it.next();
		%><option value="<%=show.getId()%>"><%=show.getName()%></option><%
	}
%>
</select>
</td></tr>
<tr><td><b>FanDom:</b></td><td>
<select name="fanDom">
	<option value="empty">Wähle FanDom</option>
<%
	Set fanDoms=new TreeSet(new StringComparator());
	fanDoms.addAll(FanFicManager.getInstance().getDomains());
 	for (Iterator it=fanDoms.iterator();it.hasNext();)
 	{
    	FanDom fanDom=(FanDom)it.next();
		%><option value="<%=fanDom.getId()%>"><%=fanDom.getName()%></option><%
	}
%>
</select>
</td></tr>
<tr><td><b>Name:</b></td><td><input type="text" size="50" name="name" value="<%=name%>"></td></tr>
<tr><td><b>URL:</b></td><td><input type="text" size="50" name="url" value="<%=url%>"></td></tr>
<tr><td><b>Sprache:</b></td><td>
<select name="language">
<option value="de">Deutsch</option>
<option value="en">Englisch</option>
</select>
</td></tr>
<tr><td colspan="2" align="right"><input type="submit" value="Add"></td></tr>
</table>

</form>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
