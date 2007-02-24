<%@ page language="java" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils" %>

<%
	XPBean transcript=(XPBean)request.getAttribute("xp");
	XPBean episode=(XPBean)transcript.getValue("episode");
	request.setAttribute("episode", episode);
	XPBean show=(XPBean)episode.getValue("show");
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getValue("title")%> - <%=episode.getValue("title")%> - Abschrift</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<jsp:include page="_show_nav.jsp" />
<jsp:include page="_episode_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
		<img src="<%=show.getValue("logo-mini.href")%>">
	</td></tr></table>
</div>
<div class="title">
    <table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;"><%=show.getValue("title") %></span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2,'/shows/')" onMouseOut="nd()">Serien</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<div class="nav_pos4"><a class=link_nav href="javascript:void(0)" onMouseOver="navEpisode(4)" onMouseOut="nd()">Episode</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Episode <%=episode.getValue("number")%>: "<%=episode.getValue("title")%>"</td></tr>
</table>
<br>
<table width=690><tr>
<td><%
	Object prev=episode.getValue("prev");
	if (prev!=null)
	{%>
		<a class=link href="<%=prev%>">&laquo; Vorherige Folge &laquo;</a>
	<%}
%></td>
<td align=right><%
	Object next=episode.getValue("next");
	if (next!=null)
	{%>
		<a class=link href="<%=next%>">&raquo; Nächste Folge &raquo;</a>
	<%}
%></td>
</tr></table>
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Abschrift</td></tr>
</table>
<%
	Collection writers=episode.getValues("credits.writer");
	Collection story=episode.getValues("credits.story");
	Collection transcriber=transcript.getValues("transcriber");
	Collection notes2=transcript.getValues("note");
	if (writers!=null || story!=null || transcriber!=null || notes2!=null)
	{%>
		<dl>
	<%
		if (writers!=null)
		{%>
			<dt><b>Drehbuch:</b><dd><%=StringUtils.formatAsEnumeration(writers)%>
		<%}
		if (story!=null)
		{%>
			<dt><b>Idee:</b><dd><%=StringUtils.formatAsEnumeration(story)%>
		<%}
		if (transcriber!=null)
		{%>
			<dt><b>Abschrift von:</b><dd><%=StringUtils.formatAsEnumeration(transcriber)%>
		<%}
		if (notes2!=null)
		{%>
			<dt><b>Bemerkungen:</b>
		<%
			Iterator it=notes2.iterator();
			while (it.hasNext()) out.println("<dd>"+it.next());
		}
	%>
		</dl>

		<hr>
	<%}
%>

<%=transcript.getValue("text")%>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
