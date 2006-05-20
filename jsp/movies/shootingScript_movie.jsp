<%@ page language="java" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils" %>

<%
	XPBean script=(XPBean)request.getAttribute("xp");
	XPBean movie=(XPBean)script.getValue("movie");
	request.setAttribute("movie", movie);
%>
<html>

<head>
<title>Filme - <%=movie.getValue("title")%> - Shooting Script</title>
<script language="JavaScript" src="../../clipart/overlib.js"></script>
<script language="JavaScript" src="../../clipart/window.js"></script>
<script language="JavaScript" src="../../nav.js"></script>
<script language="JavaScript" src="../nav.js"></script>
<jsp:include page="_movie_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="../../clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo"><img style="margin-top:13px;" src="../clipart/logo_mini.gif"></div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;">Filme</span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'../../')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navFilms(2,'../')" onMouseOut="nd()">Films</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navFilm(3)" onMouseOut="nd()">Film</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width=100%>
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<%=movie.getValue("title")%></td></tr>
</table>
<br>
<table cellspacing=0 width=100%>
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Shooting Script</td></tr>
</table>

<%
	Collection writers=movie.getValues("credits.writer");
	Collection story=movie.getValues("credits.story");
	Collection notes2=script.getValues("note");
	if (writers!=null || story!=null || notes2!=null)
	{%>
		<dl>
	<%
		if (writers!=null)
		{%>
			<dt><b>Written by:</b><dd><%=StringUtils.formatAsEnumeration(writers)%>
		<%}
		if (story!=null)
		{%>
			<dt><b>Story by:</b><dd><%=StringUtils.formatAsEnumeration(story)%>
		<%}
		if (notes2!=null)
		{%>
			<dt><b>Notes:</b>
		<%
			Iterator it=notes2.iterator();
			while (it.hasNext()) out.println("<dd>"+it.next());
		}
	%>
		</dl>

		<hr>
	<%}
%>

<%=script.getValue("text")%>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
