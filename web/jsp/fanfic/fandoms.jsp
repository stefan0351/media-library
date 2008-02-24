<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.*,
				 com.kiwisoft.media.fanfic.FanFicManager,
				 com.kiwisoft.media.fanfic.FanDom"%>

<html>

<head>
<title>Fan Fiction - Domains</title>
<script language="JavaScript" src="../overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Fan Fiction</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
		<jsp:include page="_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</td>
	<td width="800">
		<a name="top"></a>
		<table class="contenttable" width="790">
		<tr><td class="header1">Domains</td></tr>
		<tr><td class="content">
			<table class="contenttable" width="765">
			<tr><td class="header2">Shows</td></tr>
			<tr><td class="content">
			<ul>
<%
				Iterator it=new TreeSet(FanFicManager.getInstance().getShowDomains()).iterator();
				while (it.hasNext())
				{
					FanDom fanDom=(FanDom) it.next();
%>
					<li><a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a> (<%=fanDom.getFanFicCount()%>)
<%
				}
%>
			</ul>
			</td></tr>
			</table>

			<table class="contenttable" width="765">
			<tr><td class="header2">Movies</td></tr>
			<tr><td class="content">
			<ul>
<%
				it=new TreeSet(FanFicManager.getInstance().getMovieDomains()).iterator();
				while (it.hasNext())
				{
					FanDom fanDom=(FanDom) it.next();
%>
			        <li><a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a> (<%=fanDom.getFanFicCount()%>)
<%
				}
%>
			</ul>
			</td></tr>
			</table>

			<table class="contenttable" width="765">
			<tr><td class="header2">Others</td></tr>
			<tr><td class="content">
			<ul>
<%
				it=new TreeSet(FanFicManager.getInstance().getOtherDomains()).iterator();
				while (it.hasNext())
				{
					FanDom fanDom=(FanDom) it.next();
%>
			        <li><a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a> (<%=fanDom.getFanFicCount()%>)
<%
				}
%>
			</ul>
			</td></tr>
			</table>
		</td></tr>
		</table>
	</td>
</tr></table>
</div>

</body>
</html>
