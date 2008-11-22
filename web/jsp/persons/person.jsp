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
<%@ page import="com.kiwisoft.media.Name" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long personId=new Long(request.getParameter("id"));
	Person person=PersonManager.getInstance().getPerson(personId);
	request.setAttribute("person", person);

	Credits actingCredits=person.getSortedActingCredits();
	request.setAttribute("actingCredits", actingCredits);
	Map creditMap=person.getSortedCrewCredits();
	request.setAttribute("crewCredits", creditMap);
%>
<html>

<head>
<title><%=person.getName()%></title>
<script language="JavaScript" src="../overlib.js"></script>
<script language="JavaScript" src="../popup.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv"></div>

<media:title><media:render value="<%=person.getName()%>"/></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel id="details" title="Details">
			<dl>
<%
	Set names=person.getAltNames();
	if (!names.isEmpty())
	{
		out.print("<dt><b>Also Known As:</b>");
		for (Iterator it=names.iterator(); it.hasNext();)
		{
			Name name=(Name)it.next();
			out.print("<dd>");
			out.print(JspUtils.render(request, name.getName()));
			out.println("</dd>");
		}
		out.println("</dt>");
	}

	String imdbKey=person.getImdbKey();
	String tvcomKey=person.getTvcomKey();
	if (!StringUtils.isEmpty(imdbKey) || !StringUtils.isEmpty(tvcomKey))
	{
%>
			<dt><b>Links:</b>
<%
		if (!StringUtils.isEmpty(imdbKey))
		{
%>
				<dd><a target="_new" class="link" href="http://www.imdb.com/name/<%=imdbKey%>/">
					<img src="<%=request.getContextPath()%>/file?type=Icon&name=imdb" alt="IMDb" border="0"/>
					http://www.imdb.com/name/<%=imdbKey%>/</a></dd>
<%
		}
		if (!StringUtils.isEmpty(tvcomKey))
		{
%>
				<dd><a target="_new" class="link" href="http://www.tv.com/text/person/<%=tvcomKey%>/summary.html">
					<img src="http://www.tv.com/favicon.ico" alt="IMDb" border="0"/>
					http://www.tv.com/text/person/<%=tvcomKey%>/summary.html</a></dd>
<%
		}
%>

			</dt>
<%
	}
%>
			</dl>
		</media:panel>

		<media:panel id="filmography" title="Filmography">
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
						out.print(JspUtils.render(request, production));
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
									out.print(JspUtils.render(request, castMember.getCharacterName(), "preformatted"));
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
									out.print(JspUtils.render(request, subProduction, "Show"));
									out.print(" ... ");
									Set subCredits=actingCredits.getCredits(subProduction);
									boolean first=true;
									for (Iterator itRoles=subCredits.iterator(); itRoles.hasNext();)
									{
										CastMember castMember=(CastMember)itRoles.next();
										if (!StringUtils.isEmpty(castMember.getCharacterName()))
										{
											if (!first) out.print(" / ");
											out.print(JspUtils.render(request, castMember.getCharacterName(), "preformatted"));
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
						out.print(JspUtils.render(request, production));
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
								Credit crewMember=(Credit)itRoles.next();
								if (!StringUtils.isEmpty(crewMember.getSubType()))
								{
									if (first) out.print(" (");
									else out.print(", ");
									out.print(JspUtils.render(request, crewMember.getSubType(), "preformatted"));
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
									out.print(JspUtils.render(request, subProduction, "Show"));
									Set subCredits=crewCredits.getCredits(subProduction);
									boolean first=true;
									for (Iterator itRoles=subCredits.iterator(); itRoles.hasNext();)
									{
										Credit crewMember=(Credit)itRoles.next();
										if (!StringUtils.isEmpty(crewMember.getSubType()))
										{
											if (first) out.print(" (");
											else out.print(", ");
											out.print(JspUtils.render(request, crewMember.getSubType()));
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
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
