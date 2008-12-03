<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Collection,
				 java.util.Iterator,
				 org.apache.commons.lang.StringEscapeUtils,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowManager" %>
<%@ page import="com.kiwisoft.utils.Utils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="com.kiwisoft.xp.XPBean" %>
<%@ page import="com.kiwisoft.media.show.Episode" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>
<%
	Show show=ShowManager.getInstance().getShow(new Long(request.getParameter("show")));
	request.setAttribute("show", show);
	XPBean xp=(XPBean)request.getAttribute("xp");
%>
<html>

<head>
<title><%=show.getTitle()%> - Theme</title>
<link rel="StyleSheet" type="text/css" href="<%=request.getContextPath()%>/style.css">
<script language="JavaScript" src="<%=request.getContextPath()%>/overlib.js"></script>
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=StringEscapeUtils.escapeHtml(show.getTitle())%>
</media:title>

<media:body>
<media:sidebar>
	<jsp:include page="_show_nav.jsp"/>
	<jsp:include page="_shows_nav.jsp"/>
	<jsp:include page="../_nav.jsp"/>
</media:sidebar>
<media:content>
<media:panel title="<%="themes".equals(xp.getName()) ? "Theme" : "Music"%>">
<%
	Collection values=xp.getValues("theme");
	if (values!=null)
	{
		for (Iterator it=values.iterator(); it.hasNext();)
		{
			XPBean theme=(XPBean)it.next();
			String description=(String)theme.getValue("description");
			if (description==null) description="Theme";
%>
<table class="contenttable" width="765">
<tr>
	<td class="header2"><%=description%>
	</td>
</tr>
<tr>
	<td class="content">
		<table>
		<%
			Object title=theme.getValue("title");
			if (!Utils.isEmpty(title))
			{
		%>
		<tr>
			<td class="content2"><b>Title</b>:</td>
			<td class="content2"><%=JspUtils.render(request, title)%>
			</td>
		</tr>
		<%
			}
			Object composer=theme.getValue("composer");
			if (!Utils.isEmpty(composer))
			{
		%>
		<tr>
			<td class="content2"><b>Composer:</b></td>
			<td class="content2"><%=JspUtils.render(request, composer)%>
			</td>
		</tr>
		<%
			}
			Object interpret=theme.getValue("interpret");
			if (!Utils.isEmpty(interpret))
			{
		%>
		<tr>
			<td class="content2"><b>Interpret:</b></td>
			<td class="content2"><%=JspUtils.render(request, interpret)%>
			</td>
		</tr>
		<%
			}
			Object source=theme.getValue("source");
			if (source!=null)
			{
		%>
		<tr>
			<td class="content2"><b>File:</b></td>
			<td class="content2"><a href="<%=source%>"><img src="<%=request.getContextPath()%>/icons/sound.gif" alt="Sound" border="0"></a> (<%=theme.getValue("length")%>)</td>
		</tr>
		<%
			}
			Object lyrics=theme.getValue("lyrics");
			if (lyrics!=null)
			{
		%>
		<tr valign=top>
			<td class="content2"><b>Lyrics:</b></td>
			<td class="content2"><%=JspUtils.render(request, lyrics.toString(), "preformatted")%>
			</td>
		</tr>
		<%
			}
		%>
		</table>
	</td>
</tr>
</table>
<br>
<%
		}
	}
	values=xp.getValues("song");
	if (values!=null)
	{
		for (Iterator it=values.iterator(); it.hasNext();)
		{
			XPBean song=(XPBean)it.next();

%>
<table class="contenttable" width="765">
<tr>
	<td class="header2"><%=JspUtils.render(request, song.getValue("title"))%>
	</td>
</tr>
<tr>
	<td class="content">
	<table>
	<%
		Object episodeKey=song.getValue("episode");
		if (episodeKey!=null)
		{
			Episode episode=ShowManager.getInstance().getEpisode(show.getUserKey(), episodeKey.toString());
	%>
	<tr><td class="content2"><b>Episode:</b></td><td class="content2"><%=JspUtils.render(request, episode)%></td></tr>
	<%
		}
		if (song.getValue("composer")!=null)
		{
	%>
	<tr><td class="content2"><b>Composer:</b></td><td class="content2"><%=JspUtils.render(request, song.getValue("composer"))%></td></tr>
	<%
		}
		if (song.getValue("interpret")!=null)
		{
	%>
	<tr><td class="content2"><b>Interpret:</b></td><td class="content2"><%=JspUtils.render(request, song.getValue("interpret"))%></td></tr>
	<%
		}
		if (song.getValue("source")!=null)
		{
	%>
	<tr><td class="content2"><b>File:</b></td><td class="content2"><a href="<%=song.getValue("source")%>"><img src="<%=request.getContextPath()%>/icons/sound.gif" alt="Sound" border="0"></a> (<%=song.getValue("length")%>)</td></tr>
	<%
		}
		if (song.getValue("lyrics")!=null)
		{
	%>
	<tr valign=top><td class="content2"><b>Lyrics:</b></td><td class="content2"><%=JspUtils.render(request, song.getValue("lyrics").toString(), "preformatted")%></td></tr>
	<%
		}
	%>
	</table>
	</td>
</tr>
</table>
		<%
				}
			}

		%>
	</media:panel>
	</media:content>
	</media:body>

</body>
</html>
