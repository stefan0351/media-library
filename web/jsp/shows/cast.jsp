<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Iterator,
				   java.util.Set,
				   java.util.TreeSet,
				   com.kiwisoft.media.CastMember,
				   com.kiwisoft.media.CastComparator,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.media.person.CastMember"%>
<%@ page import="com.kiwisoft.media.person.CastComparator"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
%>
<html>

<head>
<title><%=show.getName()%> - Darsteller</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title><%=show.getName()%></media:title>

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
<tr><td class="header1" colspan="2">Hauptdarsteller</td></tr>
<tr><td class="content">
<%
		Set imageCast=new TreeSet(new CastComparator());
		Set noImageCast=new TreeSet(new CastComparator());
		for (Iterator it=mainCast.iterator(); it.hasNext();)
		{
			CastMember cast=(CastMember)it.next();
			if (StringUtils.isEmpty(cast.getImageSmall())) noImageCast.add(cast);
			else imageCast.add(cast);
		}
%>
	<table cellspacing=5 cellpadding=5 width="100%">
<%
		int col=0;
		boolean row=true;
		for (Iterator it=imageCast.iterator(); it.hasNext();)
		{
			CastMember cast=(CastMember)it.next();
			String description=cast.getDescription();
			if (description==null) description="";
			if (col==0) out.print("<tr class=content valign=top>");
%>
				<td width="50%" style="border:solid 1px lightgray"><b><%=cast.getActor()%></b> als <b><%=cast.getCharacterName()%></b>
				<p>
<%
				String imageLarge=cast.getImageLarge();
				if (!StringUtils.isEmpty(imageLarge)) out.print("<a href=\"/"+imageLarge+"\">");
%>
				<img src="/<%=cast.getImageSmall()%>" align=<%=((row && col==1) || (!row && col==0)) ? "right" : "left"%> hspace="5" vspace="5" border="0">
<%
				if (!StringUtils.isEmpty(imageLarge)) out.print("</a>");
%>
			<%=description%><br clear=all></p>
<%
				if (!StringUtils.isEmpty(cast.getVoice()))
				{
%>
					 <i>Synchronstimme: <%=cast.getVoice()%></i>
<%
				}
%>
				</td>
<%
				col++;
				if (col==2)
				{
					out.print("</tr>");
					col=0;
					row=!row;
				}
			}
			col=0;
			for (Iterator it=noImageCast.iterator(); it.hasNext();)
			{
				CastMember cast=(CastMember)it.next();
				String description=cast.getDescription();
				if (col==0) out.print("<tr class=content valign=top>");
%>
				<td width="50%" style="border:solid 1px lightgray"><b><%=cast.getActor()%></b> als <b><%=cast.getCharacterName()%></b>
<%
				if (!StringUtils.isEmpty(description))
				{
				%><p><%=description%><br clear=all></p><%
				}
				if (!StringUtils.isEmpty(cast.getVoice()))
				{
				%><br><i>Synchronstimme: <%=cast.getVoice()%></i><%
				}
			%></td><%
				col++;
				if (col==2)
				{
					out.print("</tr>");
					col=0;
				}
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
<tr><td class="header1">Wiederkehrende Darsteller</td></tr>
<tr><td class="content">
	<table class="contenttable" width="765">
	<tr class="header2"><td>Charakter</td><td>Schauspieler(in)</td><td>Synchronstimme</td></tr>
<%
		Iterator it=recurringCast.iterator();
		while (it.hasNext())
		{
			CastMember cast=(CastMember)it.next();
%>
			<tr><td class="content"><%=cast.getCharacterName()!=null ? cast.getCharacterName() : "&nbsp;"%></td>
				<td class="content"><%=cast.getActor()!=null ? cast.getActor().toString() : "&nbsp;"%></td>
				<td class="content"><%=cast.getVoice()!=null ? cast.getVoice() : "&nbsp;"%></td></tr>
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
