<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Iterator,
				 java.util.Set,
				 java.util.TreeSet" %>
<%@ page import="com.kiwisoft.media.movie.Movie" %>
<%@ page import="com.kiwisoft.media.person.CastMember" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.person.PersonManager" %>
<%@ page import="com.kiwisoft.media.show.Episode" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.utils.JspUtils"%>
<%@ page import="com.kiwisoft.utils.SetMap"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>
<%@ page import="com.kiwisoft.utils.db.DBLoader"%>
<%@ page import="com.kiwisoft.utils.db.Chain"%>
<%@ page import="com.kiwisoft.media.Navigation"%>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long actorId=new Long(request.getParameter("id"));
	Person actor=PersonManager.getInstance().getPerson(actorId);
	request.setAttribute("person", actor);

	Set productions=new TreeSet(StringUtils.getComparator());
	SetMap castMap=new SetMap();
	SetMap episodeMap=new SetMap();
	//noinspection RedundantArrayCreation
	Set cast=DBLoader.getInstance().loadSet(CastMember.class, null, "actor_id=?", new Object[]{actorId});
	for (Iterator it=cast.iterator(); it.hasNext();)
	{
		CastMember castMember=(CastMember)it.next();
		Movie movie=castMember.getMovie();
		if (movie!=null)
		{
			productions.add(movie);
			castMap.add(movie, castMember);
		}
	}
	//noinspection RedundantArrayCreation
	cast=DBLoader.getInstance().loadSet(CastMember.class, null, "actor_id=? and show_id is not null", new Object[]{actorId});
	for (Iterator it=cast.iterator(); it.hasNext();)
	{
		CastMember castMember=(CastMember)it.next();
		Show show=castMember.getShow();
		if (show!=null)
		{
			productions.add(show);
			castMap.add(show, castMember);
		}
	}
	//noinspection RedundantArrayCreation
	cast=DBLoader.getInstance().loadSet(CastMember.class, "episodes",
										"episodes.id=cast.episode_id and actor_id=? and episode_id is not null"+
										" and episodes.show_id not in (select show_id from cast where actor_id=? and show_id is not null)"+
										"limit 5",
										new Object[]{actorId, actorId});
	for (Iterator it=cast.iterator();it.hasNext();)
	{
		CastMember castMember=(CastMember)it.next();
		Episode episode=castMember.getEpisode();
		if (episode!=null)
		{
			Show show=episode.getShow();
			productions.add(show);
			if (!castMap.containsKey(show))
			{
				castMap.add(episode, castMember);
				episodeMap.add(show, episode);
			}
		}
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
	if (!productions.isEmpty())
	{
%>

			<table class="contenttable" width="765">
			<tr><td class="header2">Actor/Actress</td></tr>
			<tr><td class="content">
				<ol>
<%
		for (Iterator it=productions.iterator(); it.hasNext();)
		{
			Object production=it.next();
			if (production instanceof Movie)
			{
				Movie movie=(Movie)production;
%>
				<li><a class="link" href="/movies/movie.jsp?movie=<%=movie.getId()%>"><b><%=movie.getTitle()%></b></a>
<%
				Integer year=movie.getYear();
				if (year!=null) out.println("("+year+")");
				out.print(" ... ");
				boolean first=true;
				Set movieCast=castMap.get(movie);
				for (Iterator itRoles=movieCast.iterator(); itRoles.hasNext();)
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
				Show show=(Show)production;
%>
				<li><a class="link" href="<%=Navigation.getLink(show)%>"><b><%=show.getTitle()%></b></a>
<%
				Set showCast=castMap.get(show);
				if (!showCast.isEmpty())
				{
					out.print(" ... ");
					boolean first=true;
					for (Iterator itRoles=showCast.iterator(); itRoles.hasNext();)
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
					Chain episodes=new Chain(episodeMap.get(show));
					for (Iterator itEpisodes=episodes.iterator(); itEpisodes.hasNext();)
					{
						Episode episode=(Episode)itEpisodes.next();
%>
					<br>- <a class="link" href="<%=Navigation.getLink(episode)%>"><%=JspUtils.prepare(episode)%></a> ...
<%
						Set episodeCast=castMap.get(episode);
						boolean first=true;
						for (Iterator itRoles=episodeCast.iterator(); itRoles.hasNext();)
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
				}
%>
				</li>
				<%
			}
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
