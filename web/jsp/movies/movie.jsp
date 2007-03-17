<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.Language,
				 com.kiwisoft.media.LanguageManager,
				 com.kiwisoft.media.movie.Movie,
				 com.kiwisoft.media.movie.MovieManager,
				 com.kiwisoft.utils.JspUtils" %>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.utils.SortedSetMap"%>
<%@ page import="com.kiwisoft.media.person.CrewMember"%>
<%@ page import="java.util.*"%>
<%@ page import="com.kiwisoft.media.person.CastMember"%>
<%@ page import="com.kiwisoft.media.person.Person"%>
<%@ page import="com.kiwisoft.media.Navigation"%>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Movie movie=MovieManager.getInstance().getMovie(new Long(request.getParameter("movie")));
	request.setAttribute("movie", movie);
	Language english=LanguageManager.getInstance().getLanguageBySymbol("en");%>
<html>

<head>
<title>Movies - <%=movie.getTitle()%></title>
<script language="JavaScript" src="/overlib.js"></script>
<script language="JavaScript" src="/window.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=movie.getTitle()%></media:title>

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

		<a name="summary"></a>
		<table class="contenttable" width="790">
		<tr><td class="header1">Summary</td></tr>
		<tr><td class="content">
			<%=JspUtils.prepareString(movie.getSummaryText(english))%>
			<br clear=all>
			<p align=right><a class=link href="#top">Top</a></p>
		</td></tr>
		</table>

		<a name="details"></a>
		<table class="contenttable" width="790">
		<tr><td class="header1">Details</td></tr>
		<tr><td class="content">
			<table>
<%
	if (!StringUtils.isEmpty(movie.getGermanTitle()))
	{
%>
			<tr><td class="content2"><b>Also Known As:</b></td><td class="content2"><%=movie.getGermanTitle()%> (Germany)</td></tr>
<%
	}
	Set genres=movie.getGenres();
	if (!genres.isEmpty())
	{
%>
			<tr><td class="content2"><b>Genre:</b></td><td class="content2"><%=JspUtils.prepareSet(genres)%></td></tr>
<%
	}
	Set languages=movie.getLanguages();
	if (!languages.isEmpty())
	{
%>
			<tr><td class="content2"><b>Language:</b></td><td class="content2"><%=JspUtils.prepareSet(languages)%></td></tr>
<%
	}
	Set countries=movie.getCountries();
	if (!countries.isEmpty())
	{
%>
			<tr><td class="content2"><b>Country:</b></td><td class="content2"><%=JspUtils.prepareSet(countries)%></td></tr>
<%
	}
	if (movie.getYear()!=null)
	{
%>
			<tr><td class="content2"><b>Year:</b></td><td class="content2"><%=movie.getYear()%></td></tr>
<%
	}
	if (movie.getRuntime()!=null)
	{
%>
			<tr><td class="content2"><b>Runtime:</b></td><td class="content2"><%=movie.getRuntime()%> min</td></tr>
<%
	}
%>
			</table>
			<p align=right><a class=link href="#top">Top</a></p>
		</td></tr>
		</table>

<%
	List cast=new ArrayList(movie.getCastMembers());
	if (!cast.isEmpty())
	{
		Collections.sort(cast, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				CastMember castMember1=(CastMember)o1;
				CastMember castMember2=(CastMember)o2;
				Integer creditOrder2=castMember2.getCreditOrder();
				Integer creditOrder1=castMember1.getCreditOrder();
				if (creditOrder1!=null && creditOrder2!=null) return creditOrder1.compareTo(creditOrder2);
				else if (creditOrder1!=null) return -1;
				else if (creditOrder2!=null) return 1;
				return castMember1.getActor().getName().compareToIgnoreCase(castMember2.getActor().getName());
			}
		});
%>
		<a name="cast"></a>
		<table class="contenttable" width="790">
		<tr><td class="header1">Cast</td></tr>
		<tr><td class="content">
			<table>
<%
		for (Iterator it=cast.iterator();it.hasNext();)
		{
			CastMember castMember=(CastMember)it.next();
			Person actor=castMember.getActor();
%>
			<tr valign="top">
				<td class="content2"><a class="link" href="<%=Navigation.getLink(actor)%>"><%=JspUtils.prepare(actor)%></a></td>
				<td class="content2">...</td>
				<td class="content2"><%=JspUtils.prepareString(castMember.getCharacterName())%></td>
			</tr>
<%
		}
%>
			</table>
			<p align=right><a class=link href="#top">Top</a></p>
		</td></tr>
		</table>
<%
	}

	Set crew=movie.getCrewMembers();
	if (!crew.isEmpty())
	{
		SortedSetMap sortedCrew=new SortedSetMap(null, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				CrewMember crewMember1=(CrewMember)o1;
				CrewMember crewMember2=(CrewMember)o2;
				return crewMember1.getPerson().getName().compareToIgnoreCase(crewMember2.getPerson().getName());
			}
		});
		for (Iterator it=crew.iterator();it.hasNext();)
		{
			CrewMember crewMember=(CrewMember)it.next();
			sortedCrew.add(crewMember.getType(), crewMember);
		}
%>
		<a name="crew"></a>
		<table class="contenttable" width="790">
		<tr><td class="header1">Crew</td></tr>
		<tr><td class="content">
			<table>
<%
		for (Iterator it=sortedCrew.keySet().iterator();it.hasNext();)
		{
			String type=(String)it.next();
%>
			<tr valign="top"><td class="content2"><b><%=type%>:</b></td><td class="content2">
<%
			for (Iterator it2=sortedCrew.get(type).iterator();it2.hasNext();)
			{
				CrewMember crewMember=(CrewMember)it2.next();
				out.print(crewMember.getPerson());
				if (!StringUtils.isEmpty(crewMember.getSubType()))
				{
					out.print(" (");
					out.print(crewMember.getSubType());
					out.print(" )");
				}
				out.println("<br>");
			}
%>
			</td></tr>
<%
		}
%>
			</table>
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
