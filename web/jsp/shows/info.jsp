<%@ page language="java" %>
<%@ page import = "java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager" %>

<%
	XPBean xp=(XPBean)request.getAttribute("xp");
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getTitle()%></title>
<script language="JavaScript" src="<%=request.getContextPath()%>/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="<%=request.getContextPath()%>/style.css">
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
	<jsp:include page="../_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<%
	for (Iterator it=xp.getValues("section").iterator(); it.hasNext();)
	{
		XPBean section=(XPBean)it.next();
%>

<table class="contenttable" width="790">
<tr><td class="header1"><%=section.getValue("title")%></td></tr>
<tr><td class="content">
	<%=section%><br clear="all">
	<p align=right><a class=link href="#top">Top</a></p>
</tr>
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
