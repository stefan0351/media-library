<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.net.URLEncoder,
                 com.kiwisoft.utils.AlphabeticalMap,
                 java.util.*,
				 com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.fanfic.FanFicManager,
				 com.kiwisoft.media.fanfic.FanDom"%>

<html>

<head>
<title>Fanfics - FanDoms</title>
<script language="JavaScript" src="../../clipart/overlib.js"></script>
<script language="JavaScript" src="../nav.js"></script>
<script language="JavaScript" src="nav.js"></script>
<link rel="StyleSheet" type="text/css" href="../../clipart/style.css">
</head>

<body>

<a name="top"></a>

<%--<div class="logo"><img style="margin-top:13px;" src="../clipart/logo_mini.gif"></div>--%>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;">Fanfics</span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'../')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navFanfics(2)" onMouseOut="nd()">FanFics</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;FanDoms</td></tr>
</table>
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Serien</td></tr>
</table>
<ul>
<%
    Iterator it=new TreeSet(FanFicManager.getInstance().getShowDomains()).iterator();
    while (it.hasNext())
    {
        FanDom fanDom=(FanDom) it.next();
%>
        <li><a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a> (<%=fanDom.getFanFicCount()%>)
<%
    }
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>

<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Filme</td></tr>
</table>
<ul>
<%
	it=new TreeSet(FanFicManager.getInstance().getMovieDomains()).iterator();
	while (it.hasNext())
	{
		FanDom fanDom=(FanDom) it.next();
%>
        <li><a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a> (<%=fanDom.getFanFicCount()%>)
<%
	}
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>

<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Sonstige</td></tr>
</table>
<ul>
<%
	it=new TreeSet(FanFicManager.getInstance().getOtherDomains()).iterator();
	while (it.hasNext())
	{
		FanDom fanDom=(FanDom) it.next();
%>
        <li><a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a> (<%=fanDom.getFanFicCount()%>)
<%
	}
%>
</ul>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
