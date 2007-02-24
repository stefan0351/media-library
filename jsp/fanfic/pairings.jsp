<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Iterator,
                 java.util.TreeSet,
				 com.kiwisoft.media.fanfic.FanFicManager,
				 com.kiwisoft.media.fanfic.Pairing"%>

<html>

<head>
<title>Fanfics - Paare</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">FanFics</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
		<jsp:include page="/_nav.jsp"/>
		<jsp:include page="_nav.jsp"/>
	</td>
	<td width="800">
		<table class="contenttable" width="790">
		<tr><td class="header1">Paare</td></tr>
		<tr><td class="content">
			<ul>
<%
				Iterator it=new TreeSet(FanFicManager.getInstance().getPairings()).iterator();
				while (it.hasNext())
				{
					Pairing pairing=(Pairing) it.next();
					if (pairing.getFanFicCount()>0)
					{
%>
					<li><a class=link href="fanfics.jsp?pairing=<%=pairing.getId()%>"><%=pairing.getName()%></a> (<%=pairing.getFanFicCount()%>)
<%
					}
				}
%>
			</ul>
			<p align=right><a class="link" href="#top">Top</a></p>
		</td></tr>
		</table>
	</td>
</tr></table>
</div>

</body>
</html>
