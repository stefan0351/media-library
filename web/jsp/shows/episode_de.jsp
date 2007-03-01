<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.media.Language,
				   com.kiwisoft.media.LanguageManager,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.xp.XPBean" %>

<%
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	Show show=episode.getShow();
	request.setAttribute("show", show);
	XPBean info=(XPBean)request.getAttribute("xp");
	Language language=LanguageManager.getInstance().getLanguageBySymbol("de");
%>
<html>

<head>
<title><%=show.getName()%> - <%=episode.getName()%></title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<div class="title">
<div style="margin-left:10px; margin-top:5px;"><%=show.getName(language)%></div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
<!--Navigation Start-->
		<jsp:include page="_episode_nav.jsp"/>
		<jsp:include page="_show_nav.jsp"/>
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
<!--Navigation End-->
	</td>
	<td width="800">
<!--Content Start-->
		<table class="contenttable" width="790">
		<tr><td class="header1">Episode <%=episode.getNameWithKey(language)%></td></tr>
		<tr><td class="content">
			<jsp:include page="/shows/_episode_next_de.jsp"/>
			<br/>

			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="content">Inhalt</a></td></tr>
			<tr><td class="content">
				<%=info.getValue("content") %>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	String originalTitel=show.getLanguage()!=language ? episode.getOriginalName() : null;
	if (info.getValue("credits")!=null || !StringUtils.isEmpty(originalTitel))
	{
%>
  			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="credits">Produktion</a></td></tr>
			<tr><td class="content">
				<dl>
<%
				if (!StringUtils.isEmpty(originalTitel))
				{%>
					<dt><b>Originaltitel:</b><dd><%=originalTitel%>
	<%}
				Collection writers=info.getValues("credits.writer");
				if (writers!=null)
				{%>
					<dt><b>Drehbuch:</b><dd><%=StringUtils.formatAsEnumeration(writers)%>
	<%}
				Collection storyWriters=info.getValues("credits.story");
				if (storyWriters!=null)
				{%>
					<dt><b>Idee:</b><dd><%=StringUtils.formatAsEnumeration(storyWriters)%>
	<%}
				Collection directors=info.getValues("credits.director");
				if (directors!=null)
				{%>
					<dt><b>Regie:</b><dd><%=StringUtils.formatAsEnumeration(directors)%>
	<%}
				XPBean guestCast=(XPBean)info.getValue("credits.guestCast");
				if (guestCast!=null)
				{%>
					<dt><b>Darsteller:</b><dd>
					<table cellspacing=0 cellpadding=2>
		<%
					Iterator it=guestCast.getValues("cast").iterator();
					while (it.hasNext())
					{
						XPBean cast=(XPBean) it.next();
						out.print("<tr><td class=content2>"+cast.getValue("actor")+"</td>");
						Object character=cast.getValue("character");
						if (character!=null)
						{
							out.print("<td class=content2>&#151;</td><td class=content2>"+character+"</td>");
							Object voice=cast.getValue("synchronVoice");
							if (voice!=null) out.print("<td class=content2>&#151;</td><td class=content2>"+voice+"</td>");
						}
						out.print("</tr>");
					}
		%>
					</table>
	<%}
				Collection cast=info.getValues("credits.recurringCast");
				if (cast!=null)
				{%>
					<dt><b>Wiederkehrende Darsteller:</b><dd><%=StringUtils.formatAsEnumeration(cast)%>
	<%}
				Object firstAired=info.getValue("credits.firstAired");
				if (firstAired!=null)
				{%>
					<dt><b>Erstausstrahlung:</b><dd><%=firstAired%>
	<%}
				Collection songs=info.getValues("credits.music.song");
				if (songs!=null)
				{%>
					<dt><b>Musik:</b>
	<%
					Iterator it=songs.iterator();
					while (it.hasNext())
					{
						XPBean song=(XPBean)it.next();
						Object title=song.getValue("title");
						if (title!=null) out.print("<dd>\""+title+"\"");
						else out.print("<dd>Unbekannt");
						Object album=song.getValue("album");
						if (album!=null) out.print(" ("+album+")");
						out.print(" von "+song.getValue("interpret"));
						Object note=song.getValue("note");
						if (note!=null) out.print(" &#151; "+note);
					}
	%>
	<%}
%>
				</dl>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	}
	if (info.getValue("notes")!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="notes">Bemerkungen</a></td></tr>
			<tr><td class="content">
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
			</td></tr>
			</table>
<%
	}
	if (info.getValue("comments")!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="comments">Kommentare</a></td></tr>
			<tr><td class="content">
<%
				Iterator it=info.getValues("comments.comment").iterator();
				while (it.hasNext())
				{
					XPBean comment=(XPBean) it.next();
					out.println("<p>"+comment+"</p>");
				}
%>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	}
	if (info.getValue("media")!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="media">Multimedia</a></td></tr>
			<tr><td class="content">
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
			</td></tr>
			</table>
<%
				}
%>
		</td></tr>
		</table>
<!--Content End-->
	</td>
</tr></table>
</div>

</body>
</html>
