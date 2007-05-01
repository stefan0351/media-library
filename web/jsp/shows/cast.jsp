<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Iterator,
				   java.util.Set,
				   org.apache.commons.lang.StringEscapeUtils,
				   com.kiwisoft.media.Navigation,
				   com.kiwisoft.media.person.CastMember,
				   com.kiwisoft.media.pics.Picture,
				   com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.media.show.ShowManager"%>
<%@ page import="com.kiwisoft.utils.Utils"%>
<%@ page import="com.kiwisoft.web.JspUtils" %>
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
<script language="JavaScript" src="/popup.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv"></div>

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
	Set cast=show.getMainCast();
	if (!cast.isEmpty())
	{
		cast=Utils.toSortedSet(cast, new CastMember.Comparator());
%>
<table class="contenttable" width="790">
<tr><td class="header1" colspan="2">Main Cast</td></tr>
<tr><td class="content">
	<table class="table1">
	<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td class="tcell2">Role</td><td class="tcell2">German Voice</td></tr>
<%
		boolean row=false;
		for (Iterator it=cast.iterator(); it.hasNext();)
		{
			CastMember castMember=(CastMember)it.next();
%>
	<tr class="<%=row ? "trow1" : "trow2"%>"><td class="tcell2">
<%
			Picture picture=castMember.getPicture();
			if (picture==null) picture=castMember.getActor().getPicture();
			if (picture!=null && picture.getThumbnail50x50()!=null)
			{
%>
				<img src="/<%=picture.getThumbnail50x50().getFile().replace('\\', '/')%>" border="0" vspace="5" hspace="5"
					onMouseOver="imagePopup('<%=JspUtils.render(castMember.getActor().getName())%>', '/<%=picture.getFile().replace('\\', '/')%>')"
					onMouseOut="nd()">
<%
			}
			row=!row;
%>
		</td>
		<td class="tcell2"><a class="link" href="<%=Navigation.getLink(castMember.getActor())%>"><%=JspUtils.prepareString(castMember.getActor().getName())%></a></td>
		<td class="tcell2">... <%=JspUtils.prepareString(castMember.getCharacterName())%>&nbsp;</td>
		<td class="tcell2"><%=JspUtils.prepareString(castMember.getVoice())%></td>
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

	cast=show.getRecurringCast();
	if (!cast.isEmpty())
	{
		cast=Utils.toSortedSet(cast, new CastMember.Comparator());
%>
<table class="contenttable" width="790">
<tr><td class="header1" colspan="2">Recurring Cast</td></tr>
<tr><td class="content">
	<table class="table1">
	<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td class="tcell2">Role</td><td class="tcell2">German Voice</td></tr>
<%
		boolean row=false;
		for (Iterator it=cast.iterator(); it.hasNext();)
		{
			CastMember castMember=(CastMember)it.next();
%>
	<tr class="<%=row ? "trow1" : "trow2"%>"><td class="tcell2">
<%
			Picture picture=castMember.getPicture();
			if (picture==null) picture=castMember.getActor().getPicture();
			if (picture!=null && picture.getThumbnail50x50()!=null)
			{
%>
				<img src="/<%=picture.getThumbnail50x50().getFile().replace('\\', '/')%>" border="0" vspace="5" hspace="5"
					onMouseOver="imagePopup('<%=JspUtils.render(castMember.getActor().getName())%>', '/<%=picture.getFile().replace('\\', '/')%>')"
					onMouseOut="nd()">
<%
			}
			row=!row;
%>
		</td>
		<td class="tcell2"><a class="link" href="<%=Navigation.getLink(castMember.getActor())%>"><%=JspUtils.prepareString(castMember.getActor().getName())%></a></td>
		<td class="tcell2">... <%=JspUtils.prepareString(castMember.getCharacterName())%>&nbsp;</td>
		<td class="tcell2"><%=JspUtils.prepareString(castMember.getVoice())%></td>
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
