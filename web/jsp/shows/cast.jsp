<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Iterator,
				   java.util.Set,
				   com.kiwisoft.media.Navigation,
				   com.kiwisoft.media.person.CastMember,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.web.JspUtils"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%> - Cast</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title><%=StringEscapeUtils.escapeHtml(show.getTitle())%></media:title>

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

<%
	Set mainCast=show.getMainCast();
	if (!mainCast.isEmpty())
	{
%>
<table class="contenttable" width="790">
<tr><td class="header1" colspan="2">Main Cast</td></tr>
<tr><td class="content">
	<table class="table1">
	<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td tclass="tcell2">Role</td><td class="tcell2">German Voice</td></tr>
<%
		boolean row=false;
		for (Iterator it=mainCast.iterator(); it.hasNext();)
		{
			CastMember cast=(CastMember)it.next();
%>
	<tr class="<%=row ? "trow1" : "trow2"%>"><td class="tcell2">
<%
			if (!StringUtils.isEmpty(cast.getImageSmall()))
			{
%>
			<img src="/<%=cast.getImageSmall()%>" border="0" vspace="5" hspace="5">
<%
			}
			row=!row;
%>
		</td>
		<td class="tcell2"><a class="link" href="<%=Navigation.getLink(cast.getActor())%>"><%=JspUtils.prepareString(cast.getActor().getName())%></a></td>
		<td class="tcell2">... <%=JspUtils.prepareString(cast.getCharacterName())%>&nbsp;</td>
		<td class="tcell2"><%=JspUtils.prepareString(cast.getVoice())%></td>
	</tr>
<%
		}
%>
	</table>

	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>
<%
	}

	Set recurringCast=show.getRecurringCast();
	if (!recurringCast.isEmpty())
	{
%>
<table class="contenttable" width="790">
<tr><td class="header1" colspan="2">Recurring Cast</td></tr>
<tr><td class="content">
	<table class="table1">
	<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td tclass="tcell2">Role</td><td class="tcell2">German Voice</td></tr>
<%
		boolean row=false;
		for (Iterator it=recurringCast.iterator(); it.hasNext();)
		{
			CastMember cast=(CastMember)it.next();
%>
	<tr class="<%=row ? "trow1" : "trow2"%>"><td class="tcell2">
<%
			if (!StringUtils.isEmpty(cast.getImageSmall()))
			{
%>
			<img src="/<%=cast.getImageSmall()%>" border="0" vspace="5" hspace="5">
<%
			}
			row=!row;
%>
		</td>
		<td class="tcell2"><a class="link" href="<%=Navigation.getLink(cast.getActor())%>"><%=JspUtils.prepareString(cast.getActor().getName())%></a></td>
		<td class="tcell2">... <%=JspUtils.prepareString(cast.getCharacterName())%>&nbsp;</td>
		<td class="tcell2"><%=JspUtils.prepareString(cast.getVoice())%></td>
	</tr>
<%
		}
%>
	</table>

	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>
<%
	}
%>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
