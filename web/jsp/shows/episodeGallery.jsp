<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.show.Episode,
				   java.io.PrintWriter,
				   java.util.List,
				   com.kiwisoft.media.MediaManagerApp,
				   com.kiwisoft.media.show.ShowManager" %>

<%
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	request.setAttribute("show", episode.getShow());
	XPBean xp=(XPBean)request.getAttribute("xp");
%>
<html>

<head>
<title><%=episode.getShow().getName()%> - <%=episode.getName()%> - Bilder</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<jsp:include page="/shows/_shows_nav.jsp" />
<jsp:include page="/shows/_show_nav.jsp" />
<jsp:include page="/shows/_episode_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center></td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;"><%=episode.getShow().getName()%></span>
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
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Episode <%=episode.getUserKey()%>: "<%=episode.getName()%>"</td></tr>
</table>
<br>
<jsp:include page="/shows/_episode_next.jsp" />
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="images">Bilder</a></td></tr>
</table>
<br>

<table cellspacing=5 cellpadding=5 align="center">
<%
	int column=0;
	Iterator it=xp.getValues("image").iterator();
	while (it.hasNext())
	{
		XPBean image=(XPBean)it.next();
		if (column==0) out.print("<tr valign=top>");
%>
		<td align=center style="border:solid 1px lightgray">
		<a href="<%=image.getValue("source")%>"><img src="<%=image.getValue("preview")%>" border=0 hspace=5 vspace=5></a><br/>
<%
		if (image.getValue("text")!=null) out.print(image.getValue("text"));
%>
		</td>
<%
		if (column==2)
		{
			out.print("</tr>");
			column=0;
		}
		else column++;
	}
%>
</table>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
