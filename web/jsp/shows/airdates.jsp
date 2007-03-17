<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.text.DateFormat,
				   java.text.SimpleDateFormat,
				   java.util.Iterator,
				   java.util.SortedSet,
				   java.util.TreeSet,
				   com.kiwisoft.media.Airdate,
				   com.kiwisoft.media.AirdateComparator,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager" %>
<%@ page import="com.kiwisoft.media.Navigation"%>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getTitle()%> - Schedule</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;"><%=show.getTitle()%></div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

	<jsp:include page="_show_nav.jsp"/>
	<jsp:include page="_shows_nav.jsp"/>
	<jsp:include page="/_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1">Sendetermine</td></tr>
<tr><td class="content">
	<table class="table1" width="765">
	<tr class="thead"><td class="tcell">Datum/Zeit</td><td class="tcell">Sender</td><td class="tcell">Ereignis</td></tr>
<%
		DateFormat dateFormat=new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
		SortedSet airdates=new TreeSet(new AirdateComparator(AirdateComparator.TIME));
		airdates.addAll(show.getAirdates());
		Iterator itAirdates=airdates.iterator();
		boolean row=false;
		while (itAirdates.hasNext())
		{
			Airdate airdate=(Airdate)itAirdates.next();
%>
			<tr class="<%=row ? "trow1" : "trow2"%>"><td class="tcell"><%=dateFormat.format(airdate.getDate())%></td>
				<td class="tcell"><%=airdate.getChannelName()%></td>
				<td class="tcell">
<%
			Episode episode=airdate.getEpisode();
			if (episode!=null)
			{
%>
				<a class="link" href="<%=Navigation.getLink(episode)%>"><%=airdate.getName()%></a>
<%
			}
			else
			{
%>
				<%=airdate.getName()%>
<%
			}
%>
			</td></tr>
<%
			row=!row;
		}
%>
	</table>
	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
