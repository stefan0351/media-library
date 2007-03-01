<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.utils.WebUtils,
				   java.io.File,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.media.MediaManagerApp,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.Language,
				   com.kiwisoft.media.LanguageManager" %>

<%
	XPBean xp=(XPBean)request.getAttribute("xp");
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	Show show=episode.getShow();
	request.setAttribute("show", show);
	Language language=LanguageManager.getInstance().getLanguageBySymbol("en");
%>
<html>

<head>
<title><%=show.getName(language)%> - <%=episode.getNameWithKey(language)%> - Transcript</title>
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
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<jsp:include page="/shows/_show_logo.jsp"/>
	</td></tr></table>
</div>
<div class="title">
    <table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;"><%=show.getName(language)%></span>
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
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Episode <%=episode.getNameWithKey(language)%></td></tr>
</table>
<br>
<jsp:include page="/shows/_episode_next.jsp" />
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Transcript</td></tr>
</table>
<%
	Collection transcriber=xp.getValues("transcriber");
	Collection editors=xp.getValues("editor");
	Collection notes2=xp.getValues("note");
	if (transcriber!=null || notes2!=null || editors!=null)
	{
%>
		<dl>
<%
		if (transcriber!=null)
		{
			out.print("<dt><b>Transcribed by:</b><dd>");
			for (Iterator it=transcriber.iterator(); it.hasNext();)
			{
				XPBean bean=(XPBean)it.next();
				Object mail=bean.getValue("mail");
				if (mail!=null) out.print("<a class=\"link\" href=\"mailto:"+mail+"\">");
				out.print(bean);
				if (mail!=null) out.print("</a>");
				if (it.hasNext()) out.print(", ");
			}
		}
		if (editors!=null)
		{
%>
			<dt><b>Edited by:</b><dd><%=StringUtils.formatAsEnumeration(editors)%>
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

<%=xp.getValue("text")%>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
