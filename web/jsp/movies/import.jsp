<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kiwisoft.media.Country" %>
<%@ page import="com.kiwisoft.media.Language" %>
<%@ page import="com.kiwisoft.media.dataImport.CastData" %>
<%@ page import="com.kiwisoft.media.dataImport.CrewData" %>
<%@ page import="com.kiwisoft.media.dataImport.MovieData" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>

<%
	MovieData movie=(MovieData)session.getAttribute("movie");
%>
<html>

<head>
<title>Movies - Import from IMDb.com</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<div class="title">
<div style="margin-left:10px; margin-top:5px;">Movies</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
		<!--Navigation Start-->

		<jsp:include page="/_nav.jsp"/>

		<!--Navigation End-->
	</td>
	<td width="800">
		<!--Content Start-->

		<table class="contenttable" width="790">
		<tr><td class="header1">Update or Insert Movie</td></tr>
		<tr><td class="content">
			<form action="/import_imdb">
				<input type="hidden" name="action" value="add"/>

				<table border=0 cellspacing="5">
				<tr class="content" valign="top"><td><b>Type:</b></td><td>
<%
	if (movie.getMovie()==null) out.print("<b>New Movie</b>");
	else out.print("<b>Update Movie:</b> "+movie.getMovie().getTitle());
%>
					; <input type="checkbox" name="force_new" value="true"/> Force New
				</td></tr>
				<tr class="content" valign="top"><td><b>Title:</b></td><td><%=movie.getTitle()%></td></tr>
				<tr class="content" valign="top"><td><b>German Titel:</b></td><td><%=movie.getGermanTitle()%></td></tr>
				<tr class="content" valign="top"><td><b>Summary:</b></td><td><%=JspUtils.prepareString(movie.getSummary())%></td></tr>
				<tr class="content" valign="top"><td><b>Runtime:</b></td><td><%=movie.getRuntime()%> min</td></tr>
				<tr class="content" valign="top"><td><b>Year:</b></td><td><%=movie.getYear()%></td></tr>
				<tr class="content" valign="top"><td><b>Language:</b></td><td>
					<%
						for (Iterator it=movie.getLanguages().iterator(); it.hasNext();)
						{
							Language language=(Language)it.next();
							out.print(language.getName());
							if (it.hasNext()) out.print(", ");
						}
					%>
				</td></tr>
				<tr class="content" valign="top"><td><b>Country:</b></td><td>
					<%
						for (Iterator it=movie.getCountries().iterator(); it.hasNext();)
						{
							Country country=(Country)it.next();
							out.print(country.getName());
							if (it.hasNext()) out.print(", ");
						}
					%>
				</td></tr>
				<tr class="content" valign="top"><td><b>Cast:</b></td><td>
					<%
						for (Iterator it=movie.getCast().iterator(); it.hasNext();)
						{
							CastData castMember=(CastData)it.next();
							out.print(castMember.getActor());
							out.print(" ... ");
							out.print(castMember.getRole());
							if (it.hasNext()) out.print("<br>");
						}
					%>
				</td></tr>
				<tr class="content" valign="top"><td><b>Crew:</b></td><td>
					<%
						List crew=movie.getCrew();
						for (Iterator it=crew.iterator(); it.hasNext();)
						{
							CrewData crewMember=(CrewData)it.next();
							out.print(crewMember.getName());
							out.print(" ... ");
							out.print(crewMember.getType());
							if (!StringUtils.isEmpty(crewMember.getSubType()))
							{
								out.print(" (");
								out.print(crewMember.getSubType());
								out.print(")");
							}
							if (it.hasNext()) out.print("<br>");
						}
					%>
				</td></tr>
				<tr class="content"><td colspan="2" align="right"><input type="submit" value="OK"></td></tr>
				</table>

			</form>

			<p align=right><a class=link href="#top">Top</a></p>
		</td></tr>
		</table>

		<!--Content End-->
	</td>
</tr></table>
</div>

</body>
</html>
