<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils,
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
<script language="JavaScript" src="/overlib.js"></script>
<script language="JavaScript" src="/window.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;"><%=movie.getName()%></div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

	<jsp:include page="_movie_nav.jsp"/>
	<jsp:include page="/_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1">Inhalt</td></tr>
<tr><td class="content">
	<%=info.getValue("content")%>
	<br clear=all>
	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>

<table class="contenttable" width="790">
<tr><td class="header1">Produktion</td></tr>
<tr><td class="content">
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
					<tr class="content2">
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
</td></tr>
</table>

<%
	if (info.getValue("awards")!=null)
	{
%>
<table class="contenttable" width="790">
<tr><td class="header1">Auszeichnungen</td></tr>
<tr><td class="content">
	<table>
<%
		Iterator it=info.getValues("awards.award").iterator();
		while (it.hasNext())
		{
			XPBean award=(XPBean)it.next();
			String type=String.valueOf(award.getValue("type"));
			if ("golden_globe".equals(type))
			{
%>
	<tr class="content2"><td><img src="/icons/awards/golden_globe.jpg" alt="Golden Globe" title="Golden Globe" /></td><td><%=award%></td></tr>
<%
			}
			else if ("oscar".equals(type))
			{
%>
	<tr class="content2"><td><img src="/icons/awards/oscar.jpg" alt="Oscar" title="Oscar" /></td><td><%=award%></td></tr>
<%
			}
		}
%>
	</table>
	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>
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
<table class="contenttable" width="790">
<tr><td class="header1">Bewertung</td></tr>
<tr><td class="content">
	<table>
		<tr class="content2"><td>Humor:</td><td><img src="/clipart/rate<%=humor%>.gif"></td></tr>
		<tr class="content2"><td>Anspruch:</td><td><img src="/clipart/rate<%=pretension%>.gif"></td></tr>
		<tr class="content2"><td>Action:</td><td><img src="/clipart/rate<%=action%>.gif"></td></tr>
		<tr class="content2"><td>Spannung:</td><td><img src="/clipart/rate<%=suspense%>.gif"></td></tr>
		<tr class="content2"><td>Erotik</td><td><img src="/clipart/rate<%=erotic%>.gif"></td></tr>
	</table>
	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>
<%
	}
%>

<%
	if (info.getValue("media")!=null)
	{
%>
<table class="contenttable" width="790">
<tr><td class="header1">Multimedia</td></tr>
<tr><td class="content">
<%
		Collection videos=info.getValues("media.video");
		if (videos!=null)
		{
%>
	<table class="contenttable" width="765">
	<tr><td class="header2">Videos</td></tr>
	<tr><td class="content">
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
	</td></tr>
	</table>
<%
		}

		Collection images=info.getValues("media.image");
		if (images!=null)
		{
%>
	<table class="contenttable" width="765">
	<tr><td class="header2">Bilder</td></tr>
	<tr><td class="content">
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
	</td></tr>
	</table>
<%
		}
%>

	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>
<%
	}
%>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
