<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="com.kiwisoft.media.medium.MediumManager,
				 com.kiwisoft.media.medium.MediaTable"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String groupParameter=request.getParameter("group");
	int group=0;
	if ("all".equals(groupParameter)) group=-1;
	else if (groupParameter!=null) group=Integer.parseInt(groupParameter);
	int groupCount=MediumManager.getInstance().getGroupCount();
	request.setAttribute("mediaTable", new MediaTable(group));
%>
<html>

<head>
<title>Media</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title>Media</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_media_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Media">
			<table>
			<tr valign="top"><td class="content2" width="100" align="center"><div style="border: 1px solid black; background: url(/clipart/trans10.png)"><small>
<%
				for (int i=0;i<groupCount;i++)
				{
%>
					<a class=link href="/media/index.jsp?group=<%=i%>"><%=MediumManager.getGroupName(i)%></a><br/>
<%
				}
%>
				</small></div></td>
				<td><media:table model="mediaTable" alternateRows="true"/></td>
			</tr></table>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
