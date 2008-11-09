<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.*,
				 org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.kiwisoft.media.*" %>
<%@ page import="com.kiwisoft.media.movie.Movie" %>
<%@ page import="com.kiwisoft.media.movie.MovieManager" %>
<%@ page import="com.kiwisoft.media.person.CastMember" %>
<%@ page import="com.kiwisoft.media.person.Credit" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.collection.SortedSetMap" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="com.kiwisoft.media.person.CreditType" %>
<%@ page import="com.kiwisoft.media.medium.Medium" %>
<%@ page import="com.kiwisoft.media.medium.MediumManager" %>
<%@ page import="com.kiwisoft.media.pics.Picture" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Movie movie=MovieManager.getInstance().getMovie(new Long(request.getParameter("movie")));
	request.setAttribute("movie", movie);
	Language english=LanguageManager.getInstance().getLanguageBySymbol("en");
	Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
%>
<html>

<head>
<title>Movies - <%=movie.getTitle()%>
</title>
<script language="JavaScript" src="../overlib.js"></script>
<script language="JavaScript" src="../window.js"></script>
<script language="JavaScript" src="../popup.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=movie.getTitle()%>
</media:title>

<div class="main">
<table cellspacing="0" cellpadding="5">
<tr valign="top">
<td width="200">
	<!--Navigation Start-->

	<jsp:include page="_movie_nav.jsp"/>
	<jsp:include page="_nav.jsp"/>
	<jsp:include page="../_nav.jsp"/>

	<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<a name="summary"></a>
<table class="contenttable" width="790">
<%
	String englishSummary=movie.getSummaryText(english);
	String germanSummary=movie.getSummaryText(german);
%>
	<tr><td class="header1">Summary</td></tr>
	<tr><td class="content">
<%
	if (!StringUtils.isEmpty(englishSummary))
	{
		out.print("<p>");
		out.print(JspUtils.render(request, englishSummary, "preformatted"));
		out.println("</p>");
	}
	if (!StringUtils.isEmpty(germanSummary))
	{
		if (!StringUtils.isEmpty(englishSummary)) out.println("<hr size=\"1\" color=\"black\">");
		out.print("<p>");
		out.print(JspUtils.render(request, german, "icon only"));
		out.print(" ");
		out.print(JspUtils.render(request, germanSummary, "preformatted"));
		out.println("</p>");
	}
%>
	<p align=right><a class=link href="#top">Top</a></p>
	</td></tr>
</table>

<a name="details"></a>
<table class="contenttable" width="790">
<tr>
	<td class="header1">Details</td>
</tr>
<tr>
	<td class="content">
		<table>
		<%
			Set names=movie.getAltNames();
			if (!StringUtils.isEmpty(movie.getGermanTitle()) || !names.isEmpty())
			{
		%>
		<tr valign="top">
			<td class="content2"><b>Also Known As:</b></td>
			<td class="content2">
				<%
					if (!StringUtils.isEmpty(movie.getGermanTitle()))
					{
						out.print(StringEscapeUtils.escapeHtml(movie.getGermanTitle()));
						out.println(" ("+JspUtils.render(request, CountryManager.getInstance().getCountryBySymbol("DE"))+")<br>");
					}
					for (Iterator it=names.iterator(); it.hasNext();)
					{
						Name name=(Name)it.next();
						out.print(StringEscapeUtils.escapeHtml(name.getName()));
						out.println(" ("+JspUtils.render(request, name.getLanguage())+")<br>");
					}
				%>
			</td>
		</tr>
		<%
			}
			Set genres=movie.getGenres();
			if (!genres.isEmpty())
			{
		%>
		<tr valign="top">
			<td class="content2"><b>Genre:</b></td>
			<td class="content2"><%=JspUtils.sortAndRenderSet(request, genres)%>
			</td>
		</tr>
		<%
			}
			Set languages=movie.getLanguages();
			if (!languages.isEmpty())
			{
		%>
		<tr valign="top">
			<td class="content2"><b>Language:</b></td>
			<td class="content2"><%=JspUtils.sortAndRenderSet(request, languages)%>
			</td>
		</tr>
		<%
			}
			Set countries=movie.getCountries();
			if (!countries.isEmpty())
			{
		%>
		<tr valign="top">
			<td class="content2"><b>Country:</b></td>
			<td class="content2"><%=JspUtils.sortAndRenderSet(request, countries)%>
			</td>
		</tr>
		<%
			}
			if (movie.getYear()!=null)
			{
		%>
		<tr valign="top">
			<td class="content2"><b>Year:</b></td>
			<td class="content2"><%=movie.getYear()%>
			</td>
		</tr>
		<%
			}
			if (movie.getRuntime()!=null)
			{
		%>
		<tr valign="top">
			<td class="content2"><b>Runtime:</b></td>
			<td class="content2"><%=movie.getRuntime()%> min</td>
		</tr>
		<%
			}
			Set videos=MediumManager.getInstance().getMedia(movie);
			if (!videos.isEmpty())
			{
		%>
				<tr valign="top"><td class="content2"><b>Media:</b></td><td class="content2">
<%
				for (Iterator it=videos.iterator(); it.hasNext();)
				{
					Medium medium=(Medium)it.next();
					if (medium.isObsolete()) out.print("<strike>");
					out.print(JspUtils.render(request, medium, "Full"));
					if (medium.isObsolete()) out.print("</strike>");
					out.print("<br>");
				}
%>
				</td></tr>
<%
			}

			String imdbKey=movie.getImdbKey();
			if (!StringUtils.isEmpty(imdbKey))
			{
%>
				<tr valign="top"><td class="content2"><b>Links:</b></td><td class="content2">
					<a target="_new" class="link" href="http://www.imdb.com/title/<%=imdbKey%>/">
						<img src="<%=request.getContextPath()%>/picture?type=Icon&name=imdb" alt="IMDb" align="middle" border="0"/>
						http://www.imdb.com/title/<%=imdbKey%>/</a>
				</td></tr>
<%
			}
%>
		</table>
		<p align=right><a class=link href="#top">Top</a></p>
	</td>
</tr>
</table>

<%
	List cast=new ArrayList(movie.getCastMembers());
	if (!cast.isEmpty())
	{
		Collections.sort(cast, new CastMember.CreditComparator());
%>
<a name="cast"></a>
<media:panel title="Cast">
	<table class="table1">
	<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td class="tcell2">Role</td></tr>
<%
		boolean row=false;
		for (Iterator it=cast.iterator(); it.hasNext();)
		{
			CastMember castMember=(CastMember)it.next();
%>
			<tr class="<%=row ? "trow1" : "trow2"%>"><td class="tcell2">
<%
			Picture picture=castMember.getPicture();
			Person actor=castMember.getActor();
			if (picture==null && actor!=null) picture=actor.getPicture();
			if (picture!=null && picture.getThumbnail50x50()!=null)
			{
				out.print(renderPicture(request, actor!=null ? actor.getName() : castMember.getCharacterName(), picture, picture.getThumbnail50x50(), " vspace=\"5\" hspace=\"5\""));
			}
			row=!row;
%>
			</td>
			<td class="tcell2"><media:render value="<%=actor%>"/></td>
			<td class="tcell2">... <media:render value="<%=castMember.getCharacterName()%>" variant="preformatted"/></td>
			</tr>
<%
		}
%>
	</table>
</media:panel>
<%
	}

	Set crew=movie.getCredits();
	if (!crew.isEmpty())
	{
		SortedSetMap sortedCrew=new SortedSetMap(null, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				Credit crewMember1=(Credit)o1;
				Credit crewMember2=(Credit)o2;
				return crewMember1.getPerson().getName().compareToIgnoreCase(crewMember2.getPerson().getName());
			}
		});
		for (Iterator it=crew.iterator(); it.hasNext();)
		{
			Credit crewMember=(Credit)it.next();
			sortedCrew.add(crewMember.getCreditType(), crewMember);
		}
%>
<a name="crew"></a>
<table class="contenttable" width="790">
<tr>
	<td class="header1">Crew</td>
</tr>
<tr>
	<td class="content">
		<table>
		<%
			for (Iterator it=sortedCrew.keySet().iterator(); it.hasNext();)
			{
				CreditType type=(CreditType)it.next();
		%>
		<tr valign="top">
			<td class="content2"><b><%=StringEscapeUtils.escapeHtml(type.getByName())%>:</b></td>
			<td class="content2">
				<%
					for (Iterator it2=sortedCrew.get(type).iterator(); it2.hasNext();)
					{
						Credit crewMember=(Credit)it2.next();
						out.print(JspUtils.render(request, crewMember.getPerson()));
						if (!StringUtils.isEmpty(crewMember.getSubType()))
						{
							out.print(" (");
							out.print(JspUtils.render(request, crewMember.getSubType()));
							out.print(" )");
						}
						out.println("<br>");
					}
				%>
			</td>
		</tr>
		<%
			}
		%>
		</table>
		<p align=right><a class=link href="#top">Top</a></p>
	</td>
</tr>
</table>
<%
	}
%>
<!--Content End-->
</td>
</tr>
</table>
</div>

</body>
</html>
