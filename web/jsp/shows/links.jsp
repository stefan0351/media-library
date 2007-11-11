<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Iterator,
				   com.kiwisoft.media.Language,
				   com.kiwisoft.media.Link,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.collection.SortedSetMap" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%> - Links</title>
<script language="JavaScript" src="/overlib.js"></script>
<script language="JavaScript" src="/window.js"></script>
<script language="JavaScript" src="/popup.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;"><%=StringEscapeUtils.escapeHtml(show.getTitle())%></div>
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
<tr><td class="header1">Links</td></tr>
<tr><td class="content">
<%
	com.kiwisoft.collection.SortedSetMap sortedLinks=new SortedSetMap();
	for (Iterator itLinks=show.getLinks().iterator(); itLinks.hasNext();)
	{
		Link link=(Link)itLinks.next();
		sortedLinks.add(link.getLanguage(), link);
	}
	for (Iterator itLanguages=sortedLinks.keySet().iterator(); itLanguages.hasNext();)
	{
		Language language=(Language)itLanguages.next();
%>
	<p><b><u><%=language.getName()%> Websites</u></b></p>
	<ul>
<%
		for (Iterator itLinks=sortedLinks.get(language).iterator(); itLinks.hasNext();)
		{
			Link link=(Link)itLinks.next();
%>
	<li><b><%=link.getName()%></b><br>
		<a class="link" target="_new" href="<%=link.getUrl()%>"><%=link.getUrl()%></a></li>
<%
		}
%>
	</ul>
<%
	}
%>
	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>


<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
