<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.*,
				 com.kiwisoft.media.Navigation,
				 com.kiwisoft.media.movie.Movie,
				 com.kiwisoft.media.movie.MovieManager" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.media.movie.MovieComparator" %>

<%
	String letterString=request.getParameter("letter");
	SortedSet letters=MovieManager.getInstance().getLetters();
	char selectedLetter=letterString!=null && letterString.length()==1 ? letterString.charAt(0) : ((Character)letters.first()).charValue();
	Set movies=new TreeSet(new MovieComparator());
	movies.addAll(MovieManager.getInstance().getMoviesByLetter(selectedLetter));
%>
<html>

<head>
<title>Movies</title>
<script language="JavaScript" src="../overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
<div style="margin-left:10px; margin-top:5px;">Movies</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5">
<tr valign="top">
	<td width="200">
		<!--Navigation Start-->

		<jsp:include page="_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>

		<!--Navigation End-->
	</td>
	<td width="800">
		<!--Content Start-->

		<table class="contenttable" width="790">
		<tr>
			<td class="header1">List</td>
		</tr>
		<tr>
			<td class="content">
				<table width="765">
				<tr>
					<td class="content2">
						<small>[
							<%
								for (Iterator it=letters.iterator(); it.hasNext();)
								{
									Character letter=(Character)it.next();
							%>
							<a class=link href="<%=request.getContextPath()%>/movies/index.jsp?letter=<%=letter%>"><%=letter%>
							</a>
							<%
									if (it.hasNext()) out.print("|");
								}
							%>
							] (<%=MovieManager.getInstance().getMovieCount() %> Movies)
						</small>
					</td>
				</tr>
				</table>
				<br>
				<table width="765">
				<tr valign=top>
					<td class="content2" width="20"><b><a name="<%=selectedLetter%>"><%=selectedLetter%></a></b></td>
					<td class="content2" width=700>
						<ul>
						<%
							for (Iterator itMovies=movies.iterator(); itMovies.hasNext();)
							{
								Movie movie=(Movie)itMovies.next();
						%>
						<li><b><a class="link" href="<%=Navigation.getLink(request, movie)%>"><%=movie.getTitle()%>
						</a></b>
							<%

				   Integer year=movie.getYear();
				   if (year!=null) out.print(" ("+year+")");
				   String germanTitle=movie.getGermanTitle();
				   if (!StringUtils.isEmpty(germanTitle) && !germanTitle.equals(movie.getTitle())) out.print("<br>a.k.a. <i>&quot;"+germanTitle+"&quot;</i>");
			   }

							%>
						</ul>
					</td>
					<td class="content2" align=right valign=bottom><a class=link href="#top">Top</a></td>
				</tr>
				</table>
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
