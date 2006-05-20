<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.*,
				   com.kiwisoft.media.movie.Movie,
				   com.kiwisoft.media.movie.MovieManager" %>

<%@ taglib prefix="xp" uri="http://www.kiwisoft.de/media/xp" %>

<%
	Movie movie=MovieManager.getInstance().getMovie(new Long(request.getParameter("movie")));
	request.setAttribute("movie", movie);
	XPBean info=(XPBean)request.getAttribute("xp");
%>
<html>

<head>
<title>Filme - <%=movie.getName()%></title>
<script language="JavaScript" src="../../clipart/overlib.js"></script>
<script language="JavaScript" src="../../clipart/window.js"></script>
<script language="JavaScript" src="../../nav.js"></script>
<script language="JavaScript" src="../nav.js"></script>
<jsp:include page="_movie_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="../../clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo"><img style="margin-top:13px;" src="/movies/clipart/logo_mini.gif"></div>
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
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<%=movie.getName()%></td></tr>
</table>
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="content">Inhalt</a></td></tr>
</table>

<%=info.getValue("content")%>
<br clear=all>
<p align=right><a class=link href="#top">Top</a></p>

<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="credits">Produktion</a></td></tr>
</table>

<dl>
<%
	String otitle=movie.getOriginalName();
	if (!StringUtils.isEmpty(otitle)) out.println("<dt><b>Originaltitel:</b><dd>"+otitle);
%>
	<xp:set name="credits" value="credits"/>
	<xp:notEmpty bean="credits" value="country">
		<dt><b>Land:</b><dd><xp:out bean="credits" value="country"/>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="year">
		<dt><b>Jahr:</b><dd><xp:out bean="credits" value="year"/></dd></dt>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="category">
		<dt><b>Kategorie:</b><dd><xp:out bean="credits" value="category"/></dd></dt>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="length">
		<dt><b>L&auml;nge:</b><dd><xp:out bean="credits" value="length"/></dd></dt>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="director">
		<dt><b>Regie:</b><dd><xp:out bean="credits" value="director"/></dd></dt>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="writer">
		<dt><b>Drehbuch:</b><dd><xp:out bean="credits" value="writer"/></dd></dt>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="camera">
		<dt><b>Kamera:</b><dd><xp:out bean="credits" value="camera"/></dd></dt>
	</xp:notEmpty>
	<xp:notEmpty bean="credits" value="guestCast">
		<dt><b>Darsteller:</b><dd>
		<table cellspacing=0 cellpadding=2>
			<xp:iterate element="cast" bean="credits" collection="guestCast.cast">
				<tr>
					<td><xp:out bean="cast" value="actor"/></td>
					<xp:notEmpty bean="cast" value="character">
						<td>&#151;</td><td><xp:out bean="cast" value="character"/></td>
					</xp:notEmpty>
				</tr>
			</xp:iterate>
		</table>
	</xp:notEmpty>
</dl>

<p align=right><a class=link href="#top">Top</a></p>

<%
	if (info.getValue("awards")!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="awards">Auszeichnungen</a></td></tr>
</table>
<br>
<table cellspacing=5>
<%
	Iterator it=info.getValues("awards.award").iterator();
	while (it.hasNext())
	{
		XPBean award=(XPBean)it.next();
		String type=String.valueOf(award.getValue("type"));
		if ("golden_globe".equals(type))
		{
	%>
			<tr><td><img src="/clipart/awards/golden_globe.jpg" alt="Golden Globe" title="Golden Globe" /></td><td><%=award%></td></tr>
	<%
		}
		else if ("oscar".equals(type))
		{
	%>
			<tr><td><img src="/clipart/awards/oscar.jpg" alt="Oscar" title="Oscar" /></td><td><%=award%></td></tr>
	<%
		}
	}
%>
</table>

<p align=right><a class=link href="#top">Top</a></p>

<%
	}

	XPBean ratings=(XPBean)info.getValue("ratings");
	if (ratings!=null)
	{
		Object humor=ratings.getValue("humor");
		if (humor==null) humor="0";
		Object pretension=ratings.getValue("pretension");
		if (pretension==null) pretension="0";
		Object suspense=ratings.getValue("suspense");
		if (suspense==null) suspense="0";
		Object action=ratings.getValue("action");
		if (action==null) action="0";
		Object erotic=ratings.getValue("erotic");
		if (erotic==null) erotic="0";
%>

<table cellspacing=0 width=100%>
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="ratings">Bewertung</a></td></tr>
</table>
<br>
<table>
	<tr><td>Humor:</td><td><img src="/clipart/rate<%=humor%>.gif"></td></tr>
	<tr><td>Anspruch:</td><td><img src="../../clipart/rate<%=pretension%>.gif"></td></tr>
	<tr><td>Action:</td><td><img src="../../clipart/rate<%=action%>.gif"></td></tr>
	<tr><td>Spannung:</td><td><img src="../../clipart/rate<%=suspense%>.gif"></td></tr>
	<tr><td>Erotik</td><td><img src="../../clipart/rate<%=erotic%>.gif"></td></tr>
</table>

<p align=right><a class=link href="#top">Top</a></p>

<%
	}

	if (info.getValue("media")!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="media">Multimedia</a></td></tr>
</table>

<%
	Collection videos=info.getValues("media.video");
	if (videos!=null)
	{
%>
		<p><u><b>Videos:</b></u></p>

		<table cellspacing=0 cellpadding=10>
<%
		int column=0;
		Iterator it=videos.iterator();
		while (it.hasNext())
		{
			XPBean video=(XPBean)it.next();
			if (column==0) out.print("<tr>");
%>
			<td align=center><a href="<%=video.getValue("href")%>"><img src="<%=video.getValue("preview")%>" border=0></a></td>
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
<%
	}
	Collection images=info.getValues("media.image");
	if (images!=null)
	{
%>
		<p><u><b>Bilder:</b></u></p>

		<table cellspacing=0 cellpadding=10>
<%
		int column=0;
		Iterator it=images.iterator();
		while (it.hasNext())
		{
			XPBean image=(XPBean)it.next();
			if (column==0) out.print("<tr>");
%>
			<td align=center><a href="<%=image.getValue("href")%>"><img src="<%=image.getValue("preview")%>" border=0></a></td>
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
<%
	}
%>

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
