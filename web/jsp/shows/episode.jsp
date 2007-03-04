<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Date,
				 java.util.Locale,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowManager" %>
<%@ page import="com.kiwisoft.utils.JspUtils"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.kiwisoft.media.*"%>
<%@ page import="com.kiwisoft.media.person.CrewMember"%>
<%@ page import="com.kiwisoft.media.person.CastMember"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	String languageSymbol=request.getParameter("language");
	if (StringUtils.isEmpty(languageSymbol)) languageSymbol="de";
	Language language=LanguageManager.getInstance().getLanguageBySymbol(languageSymbol);
	request.setAttribute("language", language);
	Locale locale=new Locale(languageSymbol);
	Show show=episode.getShow();
	request.setAttribute("show", show);
%>

<html>

<head>
<title><%=show.getName(language)%> - <%=episode.getName(language)%></title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=show.getName(language)%></media:title>

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
	String summary=episode.getSummaryText(language);
	if (!StringUtils.isEmpty(summary))
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="content"><%=Resources.getResource("episode.shortSummary", locale)%></a></td></tr>
			<tr><td class="content">
				<%=JspUtils.prepareString(summary)%>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	}

	Date airdate=episode.getAirdate();
	String productionCode=episode.getProductionCode();
	if (airdate!=null || !StringUtils.isEmpty(productionCode) || show.getLanguage()!=language)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="production"><%=Resources.getResource("episode.production", locale)%></a></td></tr>
			<tr><td class="content">
				<dl>
<%
		if (show.getLanguage()!=language)
		{
%>
				<dt><b><%=Resources.getResource("episode.originalTitle", locale)%>:</b>
					<dd><%=JspUtils.prepareString(episode.getOriginalName())%></dd> </dt>
<%
		}
		if (airdate!=null)
		{
%>
				<dt><b><%=Resources.getResource("episode.firstAired", locale)%>:</b>
					<dd><%=JspUtils.prepareDate(airdate, locale)%></dd> </dt>
<%
		}
		if (!StringUtils.isEmpty(productionCode))
		{
%>
				<dt><b><%=Resources.getResource("episode.productionCode", locale)%>:</b>
					<dd><%=productionCode%></dd> </dt>
<%
		}
%>
				</dl>
				<p align=right><a class=link href="#top">Top</a></p>
			</td></tr>
			</table>
<%
	}

	Set writers=episode.getCrewMembers(CrewMember.WRITER);
	Set directors=episode.getCrewMembers(CrewMember.DIRECTOR);
	Set story=episode.getCrewMembers(CrewMember.STORY);
	Set mainCast=episode.getCastMembers(CastMember.MAIN_CAST);
	Set recurringCast=episode.getCastMembers(CastMember.RECURRING_CAST);
	Set guestCast=episode.getCastMembers(CastMember.GUEST_CAST);
	if (!writers.isEmpty() || !directors.isEmpty() || !story.isEmpty()
		|| !mainCast.isEmpty() || !recurringCast.isEmpty() || !guestCast.isEmpty())
	{
%>
			<table class="contenttable" width="765">
			<tr><td class="header2"><a name="castAndCrew"><%=Resources.getResource("episode.castAndCrew", locale)%></a></td></tr>
			<tr><td class="content">
				<dl>
<%
		if (!writers.isEmpty())
		{
%>
						<dt><b><%=Resources.getResource("episode.writtenBy", locale)%>:</b><dd>
<%
			for (Iterator it=writers.iterator();it.hasNext();)
			{
				CrewMember crew=(CrewMember)it.next();
				out.print(JspUtils.prepareString(crew.getPerson().getName()));
				if (it.hasNext()) out.println(",");
			}
%>
						</dd></dt>
<%
		}
		if (!directors.isEmpty())
		{
%>
						<dt><b><%=Resources.getResource("episode.directedBy", locale)%>:</b><dd>
<%
			for (Iterator it=directors.iterator();it.hasNext();)
			{
				CrewMember crew=(CrewMember)it.next();
				out.print(JspUtils.prepareString(crew.getPerson().getName()));
				if (it.hasNext()) out.println(",");
			}
%>
						</dd></dt>
<%
		}
		if (!story.isEmpty())
		{
%>
					<dt><b><%=Resources.getResource("episode.storyBy", locale)%>:</b><dd>
<%
			for (Iterator it=story.iterator();it.hasNext();)
			{
				CrewMember crew=(CrewMember)it.next();
				out.print(JspUtils.prepareString(crew.getPerson().getName()));
				if (it.hasNext()) out.println(",");
			}
%>
					</dd></dt>
<%
		}
		if (!mainCast.isEmpty())
		{
%>
					<dt><b><%=Resources.getResource("episode.mainCast", locale)%>:</b><dd>
						<table cellspacing=2 cellpadding=0>
<%
			for (Iterator it=mainCast.iterator();it.hasNext();)
			{
				CastMember castMember=(CastMember)it.next();
%>
						<tr><td class="content2"><%=JspUtils.prepareString(castMember.getActor().getName())%></td>
							<td class="content2">&mdash;</td>
							<td class="content2"><%=castMember.getCharacterName()%></td>
<%
				if (!StringUtils.isEmpty(castMember.getVoice()))
				{
%>
							<td class="content2">&mdash;</td>
							<td class="content2"><%=castMember.getVoice()%></td>
<%
				}
%>
						</tr>
<%
			}
%>
						</table>
					</dd></dt>
<%
		}
		if (!recurringCast.isEmpty())
		{
%>
					<dt><b><%=Resources.getResource("episode.recurringCast", locale)%>:</b><dd>
						<table cellspacing=2 cellpadding=0>
<%
			for (Iterator it=recurringCast.iterator();it.hasNext();)
			{
				CastMember castMember=(CastMember)it.next();
%>
						<tr><td class="content2"><%=JspUtils.prepareString(castMember.getActor().getName())%></td>
							<td class="content2">&mdash;</td>
							<td class="content2"><%=castMember.getCharacterName()%></td>
<%
				if (!StringUtils.isEmpty(castMember.getVoice()))
				{
%>
							<td class="content2">&mdash;</td>
							<td class="content2"><%=castMember.getVoice()%></td>
<%
				}
%>
						</tr>
<%
			}
%>
						</table>
					</dd></dt>
<%
		}
		if (!guestCast.isEmpty())
		{
%>
					<dt><b><%=Resources.getResource("episode.guestCast", locale)%>:</b><dd>
						<table cellspacing=2 cellpadding=0>
<%
			for (Iterator it=guestCast.iterator();it.hasNext();)
			{
				CastMember castMember=(CastMember)it.next();
%>
						<tr><td class="content2"><%=JspUtils.prepareString(castMember.getActor().getName())%></td>
							<td class="content2">&mdash;</td>
							<td class="content2"><%=castMember.getCharacterName()%></td>
<%
				if (!StringUtils.isEmpty(castMember.getVoice()))
				{
%>
							<td class="content2">&mdash;</td>
							<td class="content2"><%=castMember.getVoice()%></td>
<%
				}
%>
						</tr>
<%
			}
%>
						</table>
					</dd></dt>
<%
		}
%>
				</dl>
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
