<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
                   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.media.fanfic.*,
				   com.kiwisoft.utils.StringUtils" %>
<%@ taglib prefix="xp" uri="http://www.kiwisoft.de/media/xp" %>

<%
	XPBean fanfic=(XPBean)request.getAttribute("xp");
	Long id=new Long(fanfic.getValue("id").toString());
	FanFic fanficInfo=FanFicManager.getInstance().getFanFic(id);
%>
<html>

<head>
<title>Fan Fiction - <%=fanficInfo.getTitle()%></title>
<script language="JavaScript" src="<%=request.getContextPath()%>/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="<%=request.getContextPath()%>/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Fan Fiction</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

<jsp:include page="_nav.jsp"/>
<jsp:include page="../_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1"><%=fanficInfo.getTitle()%></td></tr>
<tr><td class="content">
	<table class="contenttable" width="765">
	<tr><td class="header2">Credits</td></tr>
	<tr><td class="content2">
		<table>
			<tr class="content2" valign=top><td><b>Author:</b></td><td>
<%
	Iterator itAuthors=fanficInfo.getAuthors().iterator();
	while (itAuthors.hasNext())
	{
		Author author=(Author)itAuthors.next();
%>
				<a class=link href="<%=request.getContextPath()%>/fanfic/fanfics.jsp?author=<%=author.getId()%>"><%=author.getName()%></a>
 <%
		if (itAuthors.hasNext()) out.print(",");
	}
%>
			</td></tr>
			<tr class="content2" valign=top><td><b>FanDom:</b></td><td>
<%
	Iterator itFanDoms=fanficInfo.getFanDoms().iterator();
	while (itFanDoms.hasNext())
	{
		FanDom fandom=(FanDom)itFanDoms.next();
%>
				<a class=link href="<%=request.getContextPath()%>/fanfic/fanfics.jsp?fandom=<%=fandom.getId()%>"><%=fandom.getName()%></a>
<%
		if (itFanDoms.hasNext()) out.print(", ");
	}
%>
			</td></tr>
			<tr class="content2" valign=top><td><b>Pairings:</b></td><td>
<%
	Iterator itPairs=fanficInfo.getPairings().iterator();
	while (itPairs.hasNext())
	{
		Pairing pairing=(Pairing)itPairs.next();
%>
				<a class=link href="<%=request.getContextPath()%>/fanfic/fanfics.jsp?pairing=<%=pairing.getId()%>"><%=pairing.getName()%></a>
<%
		if (itPairs.hasNext()) out.print(", ");
	}
	String rating=fanficInfo.getRating();
	if (!StringUtils.isEmpty(rating))
	{
%>
			<tr class="content2" valign=top><td><b>Rating:</b></td><td><%=rating%></td></tr>
<%
	}
	String description=fanficInfo.getDescription();
	if (!StringUtils.isEmpty(description))
	{
%>
			<tr class="content2" valign=top><td><b>Description:</b></td><td><%=description%></td></tr>
<%
	}
	String spoiler=fanficInfo.getSpoiler();
	if (!StringUtils.isEmpty(spoiler))
	{
%>
			<tr class="content2" valign=top><td><b>Spoiler:</b></td><td><%=spoiler%></td></tr>
<%
	}
	FanFic prequel=fanficInfo.getPrequel();
	FanFic sequel=fanficInfo.getSequel();
	if (prequel!=null || sequel!=null)
	{
%>
			<tr class="content2" valign=top><td><b>Series:</b></td><td>
<%
		if (prequel!=null)
		{
%>
				Sequel to <a class="link" href="/fanfic/authors/<%=prequel.getSource()%>">"<%=prequel.getTitle()%>"</a>
<%
		}
		if (sequel!=null)
		{
			if (prequel!=null) out.print("; ");
%>
				Continued in <a class="link" href="/fanfic/authors/<%=sequel.getSource()%>">"<%=sequel.getTitle()%>"</a>
<%
		}
%>
			</td></tr>
<%
	}
    Collection notes=fanfic.getValues("note");
	if (notes!=null)
	{
		Iterator itNotes=notes.iterator();
		while (itNotes.hasNext())
		{
			XPBean note=(XPBean)itNotes.next();
%>
			<tr class="content2" valign=top><td><b><%=note.getValue("name")%>:</b></td><td><%=note%></td></tr><%
		}
	}
	notes=fanfic.getValues("notes");
	if (notes!=null)
	{
%>
			<tr class="content2" valign=top><td><b>Notes:</b></td><td>
<%
		Iterator itNotes=notes.iterator();
		while (itNotes.hasNext())
		{
			XPBean note=(XPBean)itNotes.next();
			out.print(note);
			if (itNotes.hasNext()) out.println("<br>");
		}
%>
			</td></tr>
<%
	}
	String url=fanficInfo.getUrl();
	if (!StringUtils.isEmpty(url))
	{
%>
			<tr class="content2" valign="top"><td><b>Web:</b></td><td><a class="link" href="<%=url%>"><%=url%></a></td></tr>
<%
	}
%>
		</table>
	</td></tr>
	</table>

<%
	Collection chapters=fanfic.getValues("chapter");
	Iterator itChapters=chapters.iterator();
	while (itChapters.hasNext())
	{
		XPBean chapter=(XPBean) itChapters.next();
		Object title=chapter.getValue("title");
%>
	<table class="contenttable" width="765">
	<tr><td class=header2>
<%
		if (title!=null) out.print(title);
		else out.print("Story");
%>
	</td></tr>
	<tr><td class="content">
		<%=chapter%>
<%
        if (itChapters.hasNext())
        {
%>
            <p align=right><a class=link href="#top">Top</a></p>
<%
        }
%>
	</td></tr>
	</table>
<%
	}

	Object next=fanfic.getValue("next");
	if (next!=null)
	{
%>
		<p align=center><b>[</b> <a class=link href="<%=next%>">Next Part</a> <b>]</b></p>
<%
	}
	else if (!fanficInfo.isFinished())
	{
%>
		<p align=center><b>[</b> To be continued... <b>]</b></p>
<%
	}
	else
	{
		if (sequel!=null)
		{
%>
			<p align=center><b>[</b> Continued in <a class="link" href="<%=request.getContextPath()%>/fanfic/authors/<%=sequel.getSource()%>">"<%=sequel.getTitle()%>"</a> <b>]</b></p>
<%
		}
		else
		{
%>
			<p align=center><b>[</b> The End <b>]</b></p>
<%
		}
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
