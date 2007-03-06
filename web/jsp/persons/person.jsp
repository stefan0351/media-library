<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Iterator,
				 java.util.Set,
				 com.kiwisoft.media.person.CastMember,
				 com.kiwisoft.media.person.Person,
				 com.kiwisoft.media.person.PersonManager" %>
<%@ page import="com.kiwisoft.utils.SortedSetMap"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.utils.db.DBLoader"%>
<%@ page import="com.kiwisoft.media.movie.Movie"%>
<%@ page import="com.kiwisoft.utils.JspUtils"%>
<%@ page import="com.kiwisoft.media.show.Show"%>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long actorId=new Long(request.getParameter("id"));
	Person actor=PersonManager.getInstance().getPerson(actorId);
	request.setAttribute("person", actor);

	SortedSetMap sortedCast=new SortedSetMap(StringUtils.getComparator(), StringUtils.getComparator());

	Set cast=DBLoader.getInstance().loadSet(CastMember.class, null, "actor_id=? and movie_id is not null", new Object[]{actorId});
	for (Iterator it=cast.iterator();it.hasNext();)
	{
		CastMember castMember=(CastMember)it.next();
		if (castMember.getMovie()!=null) sortedCast.add(castMember.getMovie(), castMember.getCharacterName());
	}
	cast=DBLoader.getInstance().loadSet(CastMember.class, null, "actor_id=? and show_id is not null", new Object[]{actorId});
	for (Iterator it=cast.iterator();it.hasNext();)
	{
		CastMember castMember=(CastMember)it.next();
		if (castMember.getShow()!=null) sortedCast.add(castMember.getShow(), castMember.getCharacterName());
	}
%>
<html>

<head>
<title><%=actor.getName()%></title>
<script language="JavaScript" src="/overlib.js"></script>
<script language="JavaScript" src="/window.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title><%=actor.getName()%></media:title>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
		<!--Navigation Start-->

		<jsp:include page="/_nav.jsp"/>

		<!--Navigation End-->
	</td>
	<td width="800">
		<!--Content Start-->

		<a name="movies"></a>
		<table class="contenttable" width="790">
		<tr><td class="header1">Filmography</td></tr>
		<tr><td class="content">

<%
	if (!sortedCast.isEmpty())
	{
%>

			<table class="contenttable" width="765">
			<tr><td class="header2">Actor/Actress</td></tr>
			<tr><td class="content">
				<ol>
<%
		for (Iterator it=sortedCast.keySet().iterator();it.hasNext();)
		{
			Object prod=it.next();
			if (prod instanceof Movie)
			{
				Movie movie=(Movie)prod;
%>
				<li><a class="link" href="/movies/movie.jsp?movie=<%=movie.getId()%>"><b><%=movie.getTitle()%></b></a>
<%
				if (movie.getYear()!=null)
				{
%>
					(<%=movie.getYear()%>)
<%
				}
			}
			else
			{
				Show show=(Show)prod;
%>
				<li><a class="link" href="<%=show.getLink()%>"><b><%=show.getName()%></b></a>
<%
			}
			out.print(" ... ");
			boolean first=true;
			for (Iterator itRoles=sortedCast.get(prod).iterator();itRoles.hasNext();)
			{
				String role=(String)itRoles.next();
				if (!StringUtils.isEmpty(role))
				{
					if (!first) out.print(" / ");
					out.print(JspUtils.prepareString(role));
					first=false;
				}
			}
%>
				</li>
<%
		}
%>
				</ol>
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
