<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   java.text.DateFormat,
				   java.text.SimpleDateFormat,
				   com.kiwisoft.utils.StringComparator,
				   com.kiwisoft.utils.StringUtils,
				   java.util.*,
				   com.kiwisoft.media.*" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getName()%> - Darsteller</title>
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
<%
	Set mainCast=show.getMainCast();
	if (!mainCast.isEmpty())
	{
		Set imageCast=new TreeSet(new CastComparator());
		Set noImageCast=new TreeSet(new CastComparator());
		for (Iterator it=mainCast.iterator(); it.hasNext();)
		{
			Cast cast=(Cast)it.next();
			if (StringUtils.isEmpty(cast.getImageSmall())) noImageCast.add(cast);
			else imageCast.add(cast);
		}
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Hauptdarsteller</td></tr>
</table>
<br>
<table cellspacing=5 cellpadding=5 width="100%">
<%
		int col=0;
		boolean row=true;
		for (Iterator it=imageCast.iterator(); it.hasNext();)
		{
			Cast cast=(Cast)it.next();
			String description=cast.getDescription();
			if (description==null) description="";
			if (col==0) out.print("<tr valign=top>");
%>
			<td width="50%" style="border:solid 1px lightgray"><b><%=cast.getActor()%></b> als <b><%=cast.getCharacter()%></b>
			<p>
<%
			String imageLarge=cast.getImageLarge();
			if (!StringUtils.isEmpty(imageLarge)) out.print("<a href=\"/"+imageLarge+"\">");
%>
			<img src="/<%=cast.getImageSmall()%>" align=<%=((row && col==1) || (!row && col==0)) ? "right" : "left"%> hspace="5" vspace="5" border="0">
<%
			if (!StringUtils.isEmpty(imageLarge)) out.print("</a>");
%>
			<%=description%><br clear=all></p>
<%
			if (!StringUtils.isEmpty(cast.getVoice()))
			{
%>
	             <i>Synchronstimme: <%=cast.getVoice()%></i>
<%
			}
%>
			</td>
<%
			col++;
			if (col==2)
			{
				out.print("</tr>");
				col=0;
				row=!row;
			}
		}
		col=0;
		for (Iterator it=noImageCast.iterator(); it.hasNext();)
		{
			Cast cast=(Cast)it.next();
			String description=cast.getDescription();
			if (col==0) out.print("<tr valign=top>");
%>
			<td width="50%" style="border:solid 1px lightgray"><b><%=cast.getActor()%></b> als <b><%=cast.getCharacter()%></b>
<%
			if (!StringUtils.isEmpty(description))
			{
				%><p><%=description%><br clear=all></p><%
			}
			if (!StringUtils.isEmpty(cast.getVoice()))
			{
				%><br><i>Synchronstimme: <%=cast.getVoice()%></i><%
			}
			%></td><%
			col++;
			if (col==2)
			{
				out.print("</tr>");
				col=0;
			}
		}
%>
</table>

<p align=right><a class=link href="#top">Top</a></p>
<%
	}
	Set recurringCast=show.getRecurringCast();
	if (!recurringCast.isEmpty())
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Wiederkehrende Darsteller</td></tr>
</table>
<br>
<table width=100% border=1 cellspacing=0 cellpadding=3>
<tr bgcolor="#eeeeee"><th>Charakter</th><th>Schauspieler(in)</th><th>Synchronstimme</th></tr>
<%
		Iterator it=recurringCast.iterator();
		while (it.hasNext())
		{
			Cast cast=(Cast)it.next();
%>
			<tr><td><%=cast.getCharacter()!=null ? cast.getCharacter().toString() : "&nbsp;"%></td>
				<td><%=cast.getActor()!=null ? cast.getActor().toString() : "&nbsp;"%></td>
				<td><%=cast.getVoice()!=null ? cast.getVoice() : "&nbsp;"%></td></tr>
<%
		}
%>
</table>

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
