<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Date,
				 java.util.Iterator,
				 java.util.Set,
				 org.apache.commons.lang.StringEscapeUtils,
				 com.kiwisoft.media.*" %>
<%@ page import="com.kiwisoft.media.person.CastMember" %>
<%@ page import="com.kiwisoft.media.person.CrewMember" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.show.Episode" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.media.show.ShowManager" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="com.kiwisoft.media.person.CreditType" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Episode episode=ShowManager.getInstance().getEpisode(new Long(request.getParameter("episode")));
	request.setAttribute("episode", episode);
	Language english=LanguageManager.getInstance().getLanguageBySymbol("en");
	Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
	Show show=episode.getShow();
	request.setAttribute("show", show);
%>

<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%> - <%=StringEscapeUtils.escapeHtml(episode.getTitle())%>
</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=StringEscapeUtils.escapeHtml(show.getTitle())%>
</media:title>

<div class="main">
<table cellspacing="0" cellpadding="5">
<tr valign="top">
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
<tr>
	<td class="header1">Episode <%=episode.getTitleWithKey(null)%>
	</td>
</tr>
<tr>
<td class="content">
<jsp:include page="/shows/_episode_next.jsp"/>
<br/>
<%
	String englishSummary=episode.getSummaryText(english);
	String germanSummary=episode.getSummaryText(german);
	if (!StringUtils.isEmpty(englishSummary) || !StringUtils.isEmpty(germanSummary))
	{
%>
<table class="contenttable" width="765">
<tr>
	<td class="header2"><a name="content">Summary</a></td>
</tr>
<tr>
	<td class="content">
		<%
			boolean hr=false;
			if (!StringUtils.isEmpty(englishSummary))
			{
				out.print("<p>");
				out.print(JspUtils.prepareString(englishSummary));
				out.println("</p>");
				hr=true;
			}
			if (!StringUtils.isEmpty(germanSummary))
			{
				if (hr) out.println("<hr size=\"1\" color=\"black\">");
				out.print("<p>");
				out.print(JspUtils.render(german));
				out.print(" ");
				out.print(JspUtils.prepareString(germanSummary));
				out.println("</p>");
			}
		%>
		<p align=right><a class=link href="#top">Top</a></p>
	</td>
</tr>
</table>
<%
	}

	Date airdate=episode.getAirdate();
	String productionCode=episode.getProductionCode();
%>
<table class="contenttable" width="765">
<tr>
	<td class="header2"><a name="production">Production Details</a></td>
</tr>
<tr>
	<td class="content">
		<dl>
		<%
			Set names=episode.getAltNames();
			if (!names.isEmpty() || (!StringUtils.isEmpty(episode.getGermanTitle()) && !episode.getGermanTitle().equals(episode.getTitle())))
			{
		%>
		<dt><b>Also Known As:</b>
			<dd>
				<%
					if (!StringUtils.isEmpty(episode.getGermanTitle()) && !episode.getGermanTitle().equals(episode.getTitle()))
					{
						out.print(StringEscapeUtils.escapeHtml(episode.getGermanTitle()));
						out.print(" (");
						out.print(JspUtils.render(CountryManager.getInstance().getCountryBySymbol("DE")));
						out.print(")<br>");
					}
					for (Iterator it=names.iterator(); it.hasNext();)
					{
						Name name=(Name)it.next();
						out.print(StringEscapeUtils.escapeHtml(name.getName()));
						out.println(" ("+JspUtils.render(name.getLanguage())+")<br>");
					}
				%>
			</dd>
		</dt>
		<%
			}
			if (airdate!=null)
			{
		%>
		<dt><b>First Aired:</b><dd><%=JspUtils.prepareDate(airdate)%></dd></dt>
		<%
			}
			if (!StringUtils.isEmpty(productionCode))
			{
		%>
		<dt><b>Production Code:</b>
			<dd><%=productionCode%>
			</dd>
		</dt>
		<%
			}
		%>
		</dl>
		<p align=right><a class=link href="#top">Top</a></p>
	</td>
</tr>
</table>
<%

	Set writers=episode.getCrewMembers(CreditType.WRITER);
	Set directors=episode.getCrewMembers(CreditType.DIRECTOR);
	Set mainCast=episode.getCastMembers(CreditType.MAIN_CAST);
	Set recurringCast=episode.getCastMembers(CreditType.RECURRING_CAST);
	Set guestCast=episode.getCastMembers(CreditType.GUEST_CAST);
	if (!writers.isEmpty() || !directors.isEmpty()
		|| !mainCast.isEmpty() || !recurringCast.isEmpty() || !guestCast.isEmpty())
	{
%>
<table class="contenttable" width="765">
<tr>
	<td class="header2"><a name="castAndCrew">Cast and Crew</a></td>
</tr>
<tr>
<td class="content">
<dl>
<%
	if (!writers.isEmpty())
	{
%>
<dt><b>Writing credits:</b>
	<dd>
		<%
			for (Iterator it=writers.iterator(); it.hasNext();)
			{
				CrewMember crew=(CrewMember)it.next();
				out.print(JspUtils.render(crew.getPerson()));
				if (!StringUtils.isEmpty(crew.getSubType())) out.print(" ("+crew.getSubType()+")");
				if (it.hasNext()) out.println(",");
			}
		%>
	</dd>
</dt>
<%
	}
	if (!directors.isEmpty())
	{
%>
<dt><b>Directed by:</b>
	<dd>
		<%
			for (Iterator it=directors.iterator(); it.hasNext();)
			{
				CrewMember crew=(CrewMember)it.next();
				out.print(JspUtils.render(crew.getPerson()));
				if (it.hasNext()) out.println(",");
			}
		%>
	</dd>
</dt>
<%
	}
	if (!mainCast.isEmpty())
	{
%>
<dt><b>Main Cast:</b>
	<dd>
		<table cellspacing=2 cellpadding=0>
		<%
			for (Iterator it=mainCast.iterator(); it.hasNext();)
			{
				CastMember castMember=(CastMember)it.next();
				Person actor=castMember.getActor();
		%>
		<tr>
			<td class="content2"><%=JspUtils.render(actor)%></td>
			<td class="content2">...</td>
			<td class="content2"><%=castMember.getCharacterName()%>
			</td>
			<%
				if (!StringUtils.isEmpty(castMember.getVoice()))
				{
			%>
			<td class="content2">voice:</td>
			<td class="content2"><%=castMember.getVoice()%>
			</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		</table>
	</dd>
</dt>
<%
	}
	if (!recurringCast.isEmpty())
	{
%>
<dt><b>Recurring Cast:</b>
	<dd>
		<table cellspacing=2 cellpadding=0>
		<%
			for (Iterator it=recurringCast.iterator(); it.hasNext();)
			{
				CastMember castMember=(CastMember)it.next();
				Person actor=castMember.getActor();
		%>
		<tr>
			<td class="content2"><%=JspUtils.render(actor)%></td>
			<td class="content2">...</td>
			<td class="content2"><%=castMember.getCharacterName()%>
			</td>
			<%
				if (!StringUtils.isEmpty(castMember.getVoice()))
				{
			%>
			<td class="content2">voice:</td>
			<td class="content2"><%=castMember.getVoice()%>
			</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		</table>
	</dd>
</dt>
<%
	}
	if (!guestCast.isEmpty())
	{
%>
<dt><b>Guest Cast:</b>
	<dd>
		<table cellspacing=2 cellpadding=0>
		<%
			for (Iterator it=guestCast.iterator(); it.hasNext();)
			{
				CastMember castMember=(CastMember)it.next();
				Person actor=castMember.getActor();
		%>
		<tr>
			<td class="content2"><%=JspUtils.render(actor)%></td>
			<td class="content2">...</td>
			<td class="content2"><%=castMember.getCharacterName()%>
			</td>
			<%
				if (!StringUtils.isEmpty(castMember.getVoice()))
				{
			%>
			<td class="content2">voice:</td>
			<td class="content2"><%=castMember.getVoice()%>
			</td>
			<%
				}
			%>
		</tr>
		<%
			}
		%>
		</table>
	</dd>
</dt>
<%
	}
%>
</dl>
<p align=right><a class=link href="#top">Top</a></p>
</td>
</tr>
</table>
<%
	}
%>
</td>
</tr>
</table>

<!--Content End-->
</td>
</tr>
</table>
</div>

</body>
</html>
