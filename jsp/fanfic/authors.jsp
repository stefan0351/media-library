<%--
$Revision: 1.2 $, $Date: 2004/07/26 16:50:41 $
--%>

<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.Set,
                 java.util.Iterator,
                 java.util.Collections,
                 java.net.URLEncoder,
                 com.kiwisoft.utils.AlphabeticalMap,
                 java.util.TreeSet,
                 com.kiwisoft.utils.StringUtils,
				 com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.fanfic.FanFicManager,
				 java.util.SortedSet,
				 com.kiwisoft.media.fanfic.Author"%>

<%
	String param=request.getParameter("letter");
	Character letter=StringUtils.isEmpty(param) ? new Character('A') : new Character(param.charAt(0));
%>
<html>

<head>
<title>Fanfics - Autoren</title>
<script language="JavaScript" src="../../clipart/overlib.js"></script>
<script language="JavaScript" src="../nav.js"></script>
<script language="JavaScript" src="nav.js"></script>
<link rel="StyleSheet" type="text/css" href="../../clipart/style.css">
</head>

<body>

<a name="top"></a>

<%--<div class="logo"><img style="margin-top:13px;" src="../clipart/logo_mini.gif"></div>--%>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;">Fanfics</span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'../')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navFanfics(2)" onMouseOut="nd()">FanFics</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Autoren</td></tr>
</table>
<br>
<table>
	<tr><td><small>[
<%
	SortedSet letters=FanFicManager.getInstance().getAuthorLetters();
	Iterator itChars=letters.iterator();
    while (itChars.hasNext())
    {
        Character character=(Character) itChars.next();
%>
        <a class=link href="authors.jsp?letter=<%=character%>"><%=character%></a>
<%
        if (itChars.hasNext()) out.print("|");
    }
%>
	]</small></td></tr>
</table>
<br>
<table>
        <tr><td valign=top><b><a name="<%=letter%>"><%=letter%></a></b></td><td valign=top width=600><ul>
<%
		SortedSet authors=new TreeSet(FanFicManager.getInstance().getAuthors(letter.charValue()));
        Iterator itAuthors=authors.iterator();
        while (itAuthors.hasNext())
        {
			Author author=(Author)itAuthors.next();
%>
            <li><a class=link href="fanfics.jsp?author=<%=author.getId()%>"><%=author.getName()%></a> (<%=author.getFanFicCount()%>)
<%
        }
%>
		</ul></td></tr>
</table>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
<%--
$Log: authors.jsp,v $
Revision 1.2  2004/07/26 16:50:41  stefan
Added version tag

--%>