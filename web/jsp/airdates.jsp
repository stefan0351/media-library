<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import = "java.text.DateFormat,
				   java.text.SimpleDateFormat,
				   java.util.SortedSet,
				   java.util.TreeSet,
				   java.util.Iterator,
				   java.util.Calendar,
				   com.kiwisoft.media.show.Episode,
				   com.kiwisoft.media.*" %>

<%
	SortedSet airdates=new TreeSet(new AirdateComparator(AirdateComparator.INV_TIME));
	String period=request.getParameter("period");
	String title;
	if ("today".equals(period))
	{
		title="Heute";
		airdates.addAll(AirdateManager.getInstance().getAirdatesToday());
	}
	else if ("week".equals(period))
	{
		title="Nächste Woche";
		airdates.addAll(AirdateManager.getInstance().getAirdates(Calendar.DATE, 7));
	}
	else if ("month".equals(period))
	{
		title="Nächsten Monat";
		airdates.addAll(AirdateManager.getInstance().getAirdates(Calendar.MONTH, 1));
	}
	else
	{
		title="Nächsten Tag";
		airdates.addAll(AirdateManager.getInstance().getAirdates(Calendar.DATE, 1));
	}

%>
<html>

<head>
<title>Sendetermine - <%=title%></title>
<script language="JavaScript" src="/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<script language="JavaScript"><!--
function navAirdates(pos)
{
	text="<a onClick=\"nd()\" class=link2_nav href=\"airdates.jsp\">Nächsten Tag</a><br>";
	text+="<a onClick=\"nd()\" class=link2_nav href=\"airdates.jsp?period=week\">Nächste Woche</a><br>";
	text+="<a onClick=\"nd()\" class=link2_nav href=\"airdates.jsp?period=month\">Nächsten Monat</a><br>";
	text+="<a onClick=\"nd()\" class=link2_nav href=\"airdates.jsp?period=today\">Heute</a><br>";
	return overlib(text,STICKY,CAPTION,"Sendetermine",FIXX,pos*150-50,FIXY,140,WIDTH,150,CAPCOLOR,"white",BGCOLOR,"black",FGCOLOR,"white");
}
//--></script>
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
<%--		<img src="<%=season.getValue("show.logo-mini.href")%>">--%>
	</td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
		<span style="font-weight:bold;font-size:24pt;">Sendetermine</span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navAirdates(2)" onMouseOut="nd()">Sendetermine</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Sendetermine - <%=title%></td></tr>
</table>
<br>
<table width="100%" border=1 cellspacing=0 cellpadding=3>
<tr bgcolor="#eeeeee"><th>Datum/Zeit</th><th>Sender</th><th>Ereignis</th></tr>
<%
	DateFormat dateFormat=new SimpleDateFormat("EEE, dd.MM.yyyy HH:mm");
	Iterator itAirdates=airdates.iterator();
	while (itAirdates.hasNext())
	{
		Airdate airdate=(Airdate)itAirdates.next();
		String style="";
		Channel channel=airdate.getChannel();
		if (channel!=null && channel.isReceivable()) style="style=\"background:#eeeeff\"";
%>
<tr <%=style%>><td><%=dateFormat.format(airdate.getDate())%></td><td><%=channel%></td>
<%
		Episode episode=airdate.getEpisode();
		if (episode!=null)
		{
//            EpisodeInfo link=episode.getDefaultInfo();
//			out.print("<td>");
//			if (link!=null)
//				out.print("<a class=\"link\" href=\"/"+link.getPath()+"?episode="+episode.getId()+"\">");
//			out.print(airdate.getTitle());
//			if (link!=null)
//				out.print("</a>");
//			out.println("</td></tr>");
		}
		else if (airdate.getMovie()!=null)
		{
%>
			<td><a class=link href="<%=Navigation.getLink(airdate.getMovie())%>"><%=airdate.getName()%></a></td></tr>
<%
		}
		else
		{
%>
			<td><%=airdate.getName()%></td></tr>
<%
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
