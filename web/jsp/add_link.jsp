<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import = "java.util.Set,
				   java.util.TreeSet,
				   com.kiwisoft.media.show.ShowManager,
				   java.util.Iterator,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.fanfic.FanFicManager,
				   com.kiwisoft.media.fanfic.FanDom" %>
<%@ page import="com.kiwisoft.utils.StringUtils"%>

<%
	String name=request.getParameter("name");
	String url=request.getParameter("url");
%>
<html>

<head>
<title>Links</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Links</div>
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
<tr><td class="header1">Link hinzufügen</td></tr>
<tr><td class="content">
	<form action="/add_link">

	<table border=0>
	<tr class="content"><td><b>Serie:</b></td><td>
	<select name="show">
		<option value="empty">Wähle Serie</option>
<%
		Set shows=new TreeSet(StringUtils.getComparator());
		shows.addAll(ShowManager.getInstance().getShows());
		 for (Iterator it=shows.iterator();it.hasNext();)
		 {
			Show show=(Show)it.next();
		%><option value="<%=show.getId()%>"><%=show.getName()%></option><%
		}
%>
	</select>
	</td></tr>
	<tr class="content"><td><b>FanDom:</b></td><td>
	<select name="fanDom">
		<option value="empty">Wähle FanDom</option>
<%
		Set fanDoms=new TreeSet(StringUtils.getComparator());
		fanDoms.addAll(FanFicManager.getInstance().getDomains());
		 for (Iterator it=fanDoms.iterator();it.hasNext();)
		 {
			FanDom fanDom=(FanDom)it.next();
		%><option value="<%=fanDom.getId()%>"><%=fanDom.getName()%></option><%
		}
%>
	</select>
	</td></tr>
	<tr class="content"><td><b>Name:</b></td><td><input type="text" size="50" name="name" value="<%=name%>"></td></tr>
	<tr class="content"><td><b>URL:</b></td><td><input type="text" size="50" name="url" value="<%=url%>"></td></tr>
	<tr class="content"><td><b>Sprache:</b></td><td>
	<select name="language">
	<option value="de">Deutsch</option>
	<option value="en">Englisch</option>
	</select>
	</td></tr>
	<tr class="content"><td colspan="2" align="right"><input type="submit" value="Add"></td></tr>
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
