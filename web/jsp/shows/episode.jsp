<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Collection,
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
	Language language=LanguageManager.getInstance().getLanguageBySymbol("en");
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
			<jsp:include page="/shows/_episode_next.jsp"/>
			<br/>
<%
	Object content=info.getValue("content");
	if (content!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="content">Content</a></td></tr>
			<tr><td class="content">
				<%=content%>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	}
	XPBean credits=(XPBean)info.getValue("credits");
	if (credits!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class=header2><a name="credits">Credits</a></td></tr>
			<tr><td class="content"><dl>
<%
		Collection altTitles=info.getValues("credits.altTitle");
		if (altTitles!=null)
		{
%>
				<dt><b>Alternative Titles:</b>
<%
		   Iterator it=altTitles.iterator();
		   while (it.hasNext()) out.println("<dd>"+it.next());
		}
		Collection writers=info.getValues("credits.writer");
		if (writers!=null)
		{
%>
				<dt><b>Written by:</b><dd><%=StringUtils.formatAsEnumeration(writers)%>
<%
		}
		Collection storyWriters=info.getValues("credits.story");
		if (storyWriters!=null)
		{
%>
				<dt><b>Story by:</b><dd><%=StringUtils.formatAsEnumeration(storyWriters)%>
<%
		}
		Collection directors=info.getValues("credits.director");
		if (directors!=null)
		{
%>
				<dt><b>Directed by:</b><dd><%=StringUtils.formatAsEnumeration(directors)%>
<%
		}
   		XPBean guestCast=(XPBean)info.getValue("credits.guestCast");
   		if (guestCast!=null)
   		{
%>
				<dt><b>Guest Cast:</b><dd>
					<table cellspacing=2 cellpadding=0><%
			Iterator it=guestCast.getValues("cast").iterator();
			while (it.hasNext())
			{
				XPBean cast=(XPBean)it.next();
				out.print("<tr><td class=content2>"+cast.getValue("actor")+"</td>");
				Object character=cast.getValue("character");
				if (character!=null) out.print("<td class=content2>&#151;</td><td class=content2>"+character+"</td>");
				out.print("</tr>");
			}
%>
					</table>
<%
   		}
   		Collection cast=info.getValues("credits.recurringCast");
   		if (cast!=null)
   		{
%>
				<dt><b>Recurring Cast:</b><dd><%=StringUtils.formatAsEnumeration(cast)%>
<%
		}
   		Object firstAired=info.getValue("credits.firstAired");
   		if (firstAired!=null)
   		{
%>
				<dt><b>First Aired:</b><dd><%=firstAired%>
<%
		}
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
			<tr><td class="header2"><a name="notes">Notes</a></td></tr>
			<tr><td class="content">
<%
		Collection facts=info.getValues("notes.fact");
		if (facts!=null)
		{
			out.print("<dl>");
			Iterator it=facts.iterator();
			while (it.hasNext())
			{
				XPBean fact=(XPBean)it.next();
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
			<tr><td class="header2"><a name="comments">Comments</a></td></tr>
			<tr><td class="content">
<%
		Iterator it=info.getValues("comments.comment").iterator();
		while (it.hasNext())
		{
			XPBean comment=(XPBean)it.next();
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
			</td></tr>
			</table>
<%
	}
	XPBean summary2=(XPBean)info.getValue("summary");
	if (summary2!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="summary">Summary</a></td></tr>
			<tr><td class="content">
				<%=summary2%>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	}
	if (info.getValue("quotes")!=null)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="quotes">Quotes</a></td></tr>
			<tr><td class="content">
<%
		Iterator it=info.getValues("quotes.quote").iterator();
		while (it.hasNext()) out.println(it.next());
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
