<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import = "java.util.Collection,
				   java.util.Iterator,
                   java.util.Set,
                   java.net.URLEncoder,
				   com.kiwisoft.xp.XPBean,
				   com.kiwisoft.media.MediaManagerApp,
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
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<script language="JavaScript" src="/fanfic/nav.js"></script>
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;">Fan Fiction</span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navFanfics(2)" onMouseOut="nd()">FanFics</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<%=fanficInfo.getTitle()%></td></tr>
</table>
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Credits</td></tr>
</table>
<br>
<table>
    <tr valign=top><td class=item>Author:</td><td>
<%
    Iterator itAuthors=fanficInfo.getAuthors().iterator();
    while (itAuthors.hasNext())
    {
        Author author=(Author)itAuthors.next();
%>
        <a class=link href="/fanfic/fanfics.jsp?author=<%=author.getId()%>"><%=author.getName()%></a>
 <%
        if (itAuthors.hasNext()) out.print(",");
    }
%>
    </td></tr>
    <tr valign=top><td class=item>FanDom:</td><td>
<%
    Iterator itFanDoms=fanficInfo.getFanDoms().iterator();
    while (itFanDoms.hasNext())
    {
        FanDom fandom=(FanDom)itFanDoms.next();
%>
        <a class=link href="/fanfic/fanfics.jsp?fandom=<%=fandom.getId()%>"><%=fandom.getName()%></a>
<%
        if (itFanDoms.hasNext()) out.print(", ");
    }
%>
    </td></tr>
    <tr valign=top><td class=item>Pairings:</td><td>
<%
    Iterator itPairs=fanficInfo.getPairings().iterator();
    while (itPairs.hasNext())
    {
        Pairing pairing=(Pairing)itPairs.next();
%>
        <a class=link href="/fanfic/fanfics.jsp?pairing=<%=pairing.getId()%>"><%=pairing.getName()%></a>
<%
        if (itPairs.hasNext()) out.print(", ");
    }
    String rating=fanficInfo.getRating();
    if (!StringUtils.isEmpty(rating))
    {
%>
        <tr valign=top><td class=item>Rating:</td><td><%=rating%></td></tr>
<%
    }
    String description=fanficInfo.getDescription();
    if (!StringUtils.isEmpty(description))
    {
%>
        <tr valign=top><td class=item>Description:</td><td><%=description%></td></tr>
<%
    }
    String spoiler=fanficInfo.getSpoiler();
    if (!StringUtils.isEmpty(spoiler))
    {
%>
        <tr valign=top><td class=item>Spoiler:</td><td><%=spoiler%></td></tr>
<%
    }
    FanFic prequel=fanficInfo.getPrequel();
    FanFic sequel=fanficInfo.getSequel();
    if (prequel!=null || sequel!=null)
    {
%>
		<tr valign=top><td class=item>Series:</td><td>
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
    			%><tr valign=top><td class=item><%=note.getValue("name")%>:</td><td><%=note%></td></tr><%
    		}
	   	}

	notes=fanfic.getValues("notes");
	if (notes!=null)
	{
%>
		<tr valign=top><td class=item>Notes:</td><td>
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
		<tr valign="top"><td class="item">Web:</td><td><a class="link" href="<%=url%>"><%=url%></a></td></tr>
<%
	}
%>
</table>
<br>
<%
    Collection chapters=fanfic.getValues("chapter");
    Iterator itChapters=chapters.iterator();
    while (itChapters.hasNext())
    {
        XPBean chapter=(XPBean) itChapters.next();
        Object title=chapter.getValue("title");
%>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<%
        if (title!=null) out.print(title);
        else out.print("Story");
%></td></tr>
</table>

        <%=chapter%>
<%
        if (itChapters.hasNext())
        {
%>
            <p align=right><a class=link href="#top">Top</a></p>
<%
        }
    }
%>

<%
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
	        <p align=center><b>[</b> Continued in <a class="link" href="/fanfic/authors/<%=sequel.getSource()%>">"<%=sequel.getTitle()%>"</a> <b>]</b></p>
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

<!--Body-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
