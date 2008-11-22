<%@ page language="java" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.media.show.Show,
				   com.kiwisoft.media.show.ShowManager,
				   com.kiwisoft.media.Language,
				   com.kiwisoft.media.LanguageManager" %>

<%
	XPBean xp=(XPBean)request.getAttribute("xp");
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
	Language language=LanguageManager.getInstance().getLanguageBySymbol("en");
%>
<html>

<head>
<title><%=show.getTitle()%> - Multimedia</title>
<script language="JavaScript" src="../overlib.js"></script>
<script language="JavaScript" src="../popup.js"></script>
<jsp:include page="_shows_nav.jsp"/>
<jsp:include page="_show_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	</td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;"><%=show.getTitle(language)%></span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<%--Navigation--%>
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2,'/shows/')" onMouseOut="nd()">Serien</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<%--Navigation Ende--%>

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<%--Body--%>

<%
	Collection videos=xp.getValues("video");
	if (videos!=null)
	{
%>
		<table cellspacing=0 width="100%">
		<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="">Videos</a></td></tr>
		</table>
		<br>
		<table cellspacing=5 cellpadding=5>
<%
		int column=0;
		Iterator it=videos.iterator();
		while (it.hasNext())
		{
			XPBean video=(XPBean)it.next();
			if (column==0) out.print("<tr>");
%>
			<td align=center style="border:solid 1px lightgray"><a href="<%=video.getValue("source")%>"><img src="<%=video.getValue("preview")%>" border=0></a></td>
<%
			if (column==2)
			{
				out.print("</tr>");
				column=0;
			}
			else column++;
		}
%>
		</table>
<%
	}

	Collection images=xp.getValues("image");
	if (images!=null)
	{
%>
		<table cellspacing=0 width="100%">
		<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<a name="media">Images</a></td></tr>
		</table>
		<br>
		<table cellspacing=5 cellpadding=5>
<%
		int column=0;
		Iterator it=images.iterator();
		while (it.hasNext())
		{
			XPBean image=(XPBean)it.next();
			if (column==0) out.print("<tr>");
%>
			<td align=center style="border:solid 1px lightgray"><a href="<%=image.getValue("source")%>"><img src="<%=image.getValue("preview")%>" border=0 hspace=5 vspace=5></a></td>
<%
			if (column==2)
			{
				out.print("</tr>");
				column=0;
			}
			else column++;
		}
%>
		</table>
<%
	}
%>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
