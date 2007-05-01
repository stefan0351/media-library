<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Iterator,
				 java.util.Map,
				 java.util.Set" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="com.kiwisoft.media.movie.Movie" %>
<%@ page import="com.kiwisoft.media.person.*" %>
<%@ page import="com.kiwisoft.media.show.Production" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="com.kiwisoft.media.show.Show" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long personId=new Long(request.getParameter("id"));
	Person person=PersonManager.getInstance().getPerson(personId);
	request.setAttribute("person", person);

	Credits actingCredits=person.getActingCredits();
	request.setAttribute("actingCredits", actingCredits);
	Map creditMap=person.getCrewCredits();
	request.setAttribute("crewCredits", creditMap);
%>
<html>

<head>
<title><%=person.getName()%>
</title>
<script language="JavaScript" src="/overlib.js"></script>
<script language="JavaScript" src="/window.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=person.getName()%>
</media:title>

<div class="main">
<table cellspacing="0" cellpadding="5">
<tr valign="top">
<td width="200">
	<!--Navigation Start-->

	<jsp:include page="_nav.jsp"/>
	<jsp:include page="/_nav.jsp"/>

	<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<a name="movies"></a>
<table class="contenttable" width="790">
<tr>
	<td class="header1">Filmography</td>
</tr>
<tr>
<td class="content">

<%
	if (!actingCredits.isEmpty())
	{
%>

<table class="contenttable" width="765">
<tr>
	<td class="header2">Actor/Actress</td>
</tr>
<tr>
	<td class="content">
		<ol>
		<%
			for (Iterator it=actingCredits.getProductions().iterator(); it.hasNext();)
			{
				Production production=(Production)it.next();
				Set mainCredits=actingCredits.getCredits(production);
				out.print("<li><b>");
				out.print(JspUtils.render(production));
				out.print("</b>");
				if (production instanceof Movie)
				{
					Integer year=((Movie)production).getYear();
					if (year!=null) out.println(" ("+year+")");
				}
				else if (production instanceof Show)
				{
					String yearString=((Show)production).getYearString();
					if (yearString!=null)
					{
						out.print(" (");
						out.print(yearString);
						out.println(")");
					}
				}
				if (!mainCredits.isEmpty())
				{
					out.print(" ... ");
					boolean first=true;
					for (Iterator itRoles=mainCredits.iterator(); itRoles.hasNext();)
					{
						CastMember castMember=(CastMember)itRoles.next();
						if (!StringUtils.isEmpty(castMember.getCharacterName()))
						{
							if (!first) out.print(" / ");
							out.print(JspUtils.prepareString(castMember.getCharacterName()));
							first=false;
						}
					}
				}
				else
				{
					Set subProductions=actingCredits.getSubProductions(production);
					int i=0;
					for (Iterator itEpisodes=subProductions.iterator(); itEpisodes.hasNext();)
					{
						Production subProduction=(Production)itEpisodes.next();
						if (i>=5)
						{
							out.print("<br>and "+(subProductions.size()-5)+" more");
							break;
						}
						else
						{
							out.print("<br>- ");
							out.print(JspUtils.render(subProduction, "Show"));
							out.print(" ... ");
							Set subCredits=actingCredits.getCredits(subProduction);
							boolean first=true;
							for (Iterator itRoles=subCredits.iterator(); itRoles.hasNext();)
							{
								CastMember castMember=(CastMember)itRoles.next();
								if (!StringUtils.isEmpty(castMember.getCharacterName()))
								{
									if (!first) out.print(" / ");
									out.print(JspUtils.prepareString(castMember.getCharacterName()));
									first=false;
								}
							}
						}
						i++;
					}
				}
			}
		%>
		</ol>

		<p align=right><a class=link href="#top">Top</a></p>
	</td>
</tr>
</table>

<%
	}

	for (Iterator itTypes=creditMap.keySet().iterator(); itTypes.hasNext();)
	{
		CreditType type=(CreditType)itTypes.next();
		Credits crewCredits=(Credits)creditMap.get(type);
%>

<table class="contenttable" width="765">
<tr>
	<td class="header2"><%=StringEscapeUtils.escapeHtml(type.getAsName())%>
	</td>
</tr>
<tr>
	<td class="content">
		<ol>
		<%
			for (Iterator it=crewCredits.getProductions().iterator(); it.hasNext();)
			{
				Production production=(Production)it.next();
				Set mainCredits=crewCredits.getCredits(production);
				out.print("<li><b>");
				out.print(JspUtils.render(production));
				out.print("</b>");
				if (production instanceof Movie)
				{
					Movie movie=(Movie)production;
					Integer year=movie.getYear();
					if (year!=null) out.println(" ("+year+")");
				}
				if (!mainCredits.isEmpty())
				{
					boolean first=true;
					for (Iterator itRoles=mainCredits.iterator(); itRoles.hasNext();)
					{
						CrewMember crewMember=(CrewMember)itRoles.next();
						if (!StringUtils.isEmpty(crewMember.getSubType()))
						{
							if (first) out.print(" (");
							else out.print(", ");
							out.print(JspUtils.prepareString(crewMember.getSubType()));
							first=false;
						}
					}
					if (!first) out.print(")");
				}
				else
				{
					Set subProductions=crewCredits.getSubProductions(production);
					int i=0;
					for (Iterator itEpisodes=subProductions.iterator(); itEpisodes.hasNext();)
					{
						Production subProduction=(Production)itEpisodes.next();
						if (i>=5)
						{
							out.print("<br>and "+(subProductions.size()-5)+" more");
							break;
						}
						else
						{
							out.print("<br>- ");
							out.print(JspUtils.render(subProduction, "Show"));
							Set subCredits=crewCredits.getCredits(subProduction);
							boolean first=true;
							for (Iterator itRoles=subCredits.iterator(); itRoles.hasNext();)
							{
								CrewMember crewMember=(CrewMember)itRoles.next();
								if (!StringUtils.isEmpty(crewMember.getSubType()))
								{
									if (first) out.print(" (");
									else out.print(", ");
									out.print(JspUtils.render(crewMember.getSubType()));
									first=false;
								}
							}
							if (!first) out.print(")");
						}
						i++;
					}
				}
			}
		%>
		</ol>

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
