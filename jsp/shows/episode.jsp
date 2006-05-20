<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.show.Episode,
				   java.io.PrintWriter,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.media.MediaManagerApp,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.Language,
				   com.kiwisoft.media.LanguageManager" %>

<%
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	Show show=episode.getShow();
	request.setAttribute("show", show);
	XPBean info=(XPBean)request.getAttribute("xp");
	Language language=LanguageManager.getInstance().getLanguageBySymbol("en");
%>
<html>

<head>
<title><%=show.getName()%> - <%=episode.getName()%></title>
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
	<span style="margin-top:10px;font-weight:bold;font-size:24pt;"><%=show.getName(language)%></span>
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
<%
	Object content=info.getValue("content");
	if (content!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="content">Content</a></td></tr>
</table>

<%=content%>

<p align=right><a class=link href="#top">Top</a></p>

<%
	}

	XPBean credits=(XPBean)info.getValue("credits");
	if (credits!=null)
	{
%>

<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="credits">Credits</a></td></tr>
</table>

<dl>
<%
	Collection altTitles=info.getValues("credits.altTitle");
	if (altTitles!=null)
	{%>
		<dt><b>Alternative Titles:</b>
		<%
		Iterator it=altTitles.iterator();
		while (it.hasNext()) out.println("<dd>"+it.next());
		%>
	<%}
	Collection writers=info.getValues("credits.writer");
	if (writers!=null)
	{%>
		<dt><b>Written by:</b><dd><%=StringUtils.formatAsEnumeration(writers)%>
	<%}
	Collection storyWriters=info.getValues("credits.story");
	if (storyWriters!=null)
	{%>
		<dt><b>Story by:</b><dd><%=StringUtils.formatAsEnumeration(storyWriters)%>
	<%}
	Collection directors=info.getValues("credits.director");
	if (directors!=null)
	{%>
		<dt><b>Directed by:</b><dd><%=StringUtils.formatAsEnumeration(directors)%>
	<%}
	XPBean guestCast=(XPBean)info.getValue("credits.guestCast");
	if (guestCast!=null)
	{
		%><dt><b>Guest Cast:</b><dd>
		<table cellspacing=0 cellpadding=2><%
		Iterator it=guestCast.getValues("cast").iterator();
		while (it.hasNext())
		{
			XPBean cast=(XPBean) it.next();
			out.print("<tr><td>"+cast.getValue("actor")+"</td>");
			Object character=cast.getValue("character");
			if (character!=null) out.print("<td>&#151;</td><td>"+character+"</td>");
			out.print("</tr>");
		}
		%></table><%
	}
	Collection cast=info.getValues("credits.recurringCast");
	if (cast!=null)
	{
		%><dt><b>Recurring Cast:</b><dd><%=StringUtils.formatAsEnumeration(cast)%><%
	}
	Object firstAired=info.getValue("credits.firstAired");
	if (firstAired!=null)
	{%>
		<dt><b>First Aired:</b><dd><%=firstAired%>
	<%}
%>
</dl>

<p align=right><a class=link href="#top">Top</a></p>

<%
	}

	if (info.getValue("notes")!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="notes">Notes</a></td></tr>
</table>

<%
	Collection facts=info.getValues("notes.fact");
	if (facts!=null)
	{
		out.print("<dl>");
		Iterator it=facts.iterator();
		while (it.hasNext())
		{
			XPBean fact=(XPBean) it.next();
			out.println("<dt><b>"+fact.getValue("name")+":</b><dd>"+fact);
		}
		out.println("</dl>");
	}
	Collection noteList=info.getValues("notes.note");
	if (noteList!=null)
	{
		Iterator it=noteList.iterator();
		while (it.hasNext()) out.println("<p>"+it.next()+"</p>");
	}
%>

<p align=right><a class=link href="#top">Top</a></p>
<%
	}

	if (info.getValue("comments")!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="comments">Comments</a></td></tr>
</table>

<%
	Iterator it=info.getValues("comments.comment").iterator();
	while (it.hasNext())
	{
		XPBean comment=(XPBean) it.next();
		out.println("<p>"+comment+"</p>");
	}
%>

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
			if (column==3)
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
		<p><u><b>Images:</b></u></p>

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
			if (column==3)
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

	XPBean summary2=(XPBean)info.getValue("summary");
	if (summary2!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="summary">Summary</a></td></tr>
</table>

<%=summary2%>

<p align=right><a class=link href="#top">Top</a></p>
<%
	}

	if (info.getValue("quotes")!=null)
	{
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="quotes">Quotes</a></td></tr>
</table>

<%
		Iterator it=info.getValues("quotes.quote").iterator();
		while (it.hasNext()) out.println(it.next());
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
