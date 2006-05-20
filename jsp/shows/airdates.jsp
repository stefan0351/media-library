<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "com.kiwisoft.media.MediaManagerApp,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   java.text.DateFormat,
				   java.text.SimpleDateFormat,
				   java.util.SortedSet,
				   java.util.TreeSet,
				   com.kiwisoft.media.AirdateComparator,
				   java.util.Iterator,
				   com.kiwisoft.media.Airdate,
				   com.kiwisoft.utils.StringUtils,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.show.EpisodeInfo,
				   com.kiwisoft.media.Channel,
				   java.io.PrintWriter" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getName()%> - Sendetermine</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<jsp:include page="_shows_nav.jsp"/>
<jsp:include page="_show_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<jsp:include page="/shows/_show_logo.jsp"/>
	</td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
<span style="font-weight:bold;font-size:24pt;"><%=show.getName()%></span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2,'/shows/')" onMouseOut="nd()">Serien</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Sendetermine</td></tr>
</table>
<br>
<table width=100% border=1 cellspacing=0 cellpadding=3>
<tr bgcolor="#eeeeee"><th>Datum/Zeit</th><th>Sender</th><th>Ereignis</th></tr>
<%
	DateFormat dateFormat=new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
	SortedSet airdates=new TreeSet(new AirdateComparator(AirdateComparator.TIME));
	airdates.addAll(show.getAirdates());
	Iterator itAirdates=airdates.iterator();
	while (itAirdates.hasNext())
	{
		Airdate airdate=(Airdate)itAirdates.next();
		String style="";
		Channel channel=airdate.getChannel();
		if (channel!=null && channel.isReceivable()) style="style=\"background:#eeeeff\"";
%>
		<tr <%=style%>><td><%=dateFormat.format(airdate.getDate())%></td><td><%=airdate.getChannelName()%></td>
<%
		Episode episode=airdate.getEpisode();
		if (episode!=null)
		{
            EpisodeInfo link=episode.getDefaultInfo();
			out.print("<td>");
			if (link!=null)
				out.print("<a class=\"link\" href=\"/"+link.getPath()+"?episode="+episode.getId()+"\">");
			out.print(airdate.getName());
			if (link!=null)
				out.print("</a>");
			out.println("</td></tr>");
		}
		else if (airdate.getMovie()!=null)
		{
			%><td><%=airdate.getName()%></td></tr><%
		}
		else
		{
			%><td><%=airdate.getName()%></td></tr><%
		}
	}
%>
</table>

<p align=right><a class=link href="#top">Top</a></p>


<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
