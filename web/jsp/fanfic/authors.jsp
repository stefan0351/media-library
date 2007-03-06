<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.Iterator,
                 java.util.TreeSet,
                 com.kiwisoft.utils.StringUtils,
				 com.kiwisoft.media.fanfic.FanFicManager,
				 java.util.SortedSet,
				 com.kiwisoft.media.fanfic.Author"%>

<%
	String param=request.getParameter("letter");
	Character letter=StringUtils.isEmpty(param) ? new Character('A') : new Character(param.charAt(0));
%>
<html>

<head>
<title>Fan Fiction - Authors</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
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
		<jsp:include page="_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</td>
	<td width="800">
		<table class="contenttable" width="790">
		<tr><td class="header1">Authors</td></tr>
		<tr><td class="content">
			<table><tr><td class="content2"><small>[
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
			]</small></td></tr></table>
			<br>
			<table>
					<tr valign="top"><td class="content2"><b><a name="<%=letter%>"><%=letter%></a></b></td><td class="content2" width=600><ul>
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
		</td></tr>
		</table>
	</td>
</tr></table>
</div>

</body>
</html>
