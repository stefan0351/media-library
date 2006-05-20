<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.text.DateFormat,
				   java.text.SimpleDateFormat,
				   java.util.*,
				   com.kiwisoft.media.show.*,
				   java.io.PrintWriter,
				   com.kiwisoft.media.*,
				   com.kiwisoft.utils.*" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getName()%> - Links</title>
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
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Links</td></tr>
</table>

<%
	SortedSetMap sortedLinks=new SortedSetMap();
	for (Iterator itLinks=show.getLinks().iterator(); itLinks.hasNext();)
	{
		Link link=(Link)itLinks.next();
		sortedLinks.add(link.getLanguage(), link);
	}
	for (Iterator itLanguages=sortedLinks.keySet().iterator(); itLanguages.hasNext();)
	{
		Language language=(Language)itLanguages.next();
		%><p><b><u><%=language.getName()%>e Webseiten</u></b></p>
		<ul><%
		for (Iterator itLinks=sortedLinks.get(language).iterator(); itLinks.hasNext();)
		{
			Link link=(Link)itLinks.next();
			%><li><b><%=link.getName()%></b><br>
				<a class="link" target="_new" href="<%=link.getUrl()%>"><%=link.getUrl()%></a></li><%
		}
		%></ul><%
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
