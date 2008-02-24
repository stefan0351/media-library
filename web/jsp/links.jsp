<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.ArrayList,
				 java.util.Collections,
				 java.util.Iterator,
				 java.util.List,
				 com.kiwisoft.format.FormatStringComparator,
				 com.kiwisoft.media.Link" %>
<%@ page import="com.kiwisoft.media.LinkGroup" %>
<%@ page import="com.kiwisoft.media.LinkManager" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.persistence.DBLoader" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String groupId=request.getParameter("group");
	LinkGroup group=null;
	if (!StringUtils.isEmpty(groupId))
	{
		group=(LinkGroup)DBLoader.getInstance().load(LinkGroup.class, new Long(groupId));
	}
	String showId=request.getParameter("show");
	Show show=null;
	if (!StringUtils.isEmpty(showId))
	{
		show=(Show)DBLoader.getInstance().load(Show.class, new Long(showId));
	}
	request.setAttribute("show", show);
%>
<html>

<head>
<title>Links</title>
<script language="JavaScript" src="overlib.js"></script>
<script language="JavaScript" src="window.js"></script>
<script language="JavaScript" src="popup.js"></script>
<link rel="StyleSheet" type="text/css" href="style.css">
</head>

<body>
<div id="overDiv" class="over_lib"></div>
<a name="top"></a>

<media:title>Links</media:title>
<media:body>
	<media:sidebar>
		<%
			if (show!=null)
			{
		%>
		<jsp:include page="shows/_show_nav.jsp"/>
		<jsp:include page="shows/_shows_nav.jsp"/>
		<%
			}
		%>
		<jsp:include page="_nav.jsp"/>

	</media:sidebar>
	<media:content>
		<media:panel title="<%=group!=null ? group.getName() : "Links"%>">
			<%
				if (group!=null) out.println("<p>"+JspUtils.render(request, group, "Hierarchy")+"</p>");

				List links=new ArrayList();
				List relatedGroups=new ArrayList();
				List childGroups=new ArrayList();
				if (group!=null)
				{
					links.addAll(group.getLinks());
					childGroups.addAll(group.getSubGroups());
					relatedGroups.addAll(group.getRelatedGroups());
				}
				else
				{
					childGroups.addAll(LinkManager.getInstance().getRootGroups());
				}
				Collections.sort(links, new FormatStringComparator());
				Collections.sort(childGroups, new FormatStringComparator());
				Collections.sort(relatedGroups, new FormatStringComparator("hierarchy"));

				if (!links.isEmpty())
				{
			%>
			<ul>
			<%
				for (Iterator itLinks=links.iterator(); itLinks.hasNext();)
				{
					Link link=(Link)itLinks.next();
			%>
			<li><b><%=link.getName()%>
			</b><br>
				<a class="link" target="_new" href="<%=link.getUrl()%>"><%=link.getUrl()%>
				</a></li>
			<%
				}
			%>
			</ul>
			<%
				}
				if (!childGroups.isEmpty())
				{
			%>
			<u>Sub Groups</u>
			<ul>
			<%
				for (Iterator it=childGroups.iterator(); it.hasNext();)
				{
					LinkGroup childGroup=(LinkGroup)it.next();
					out.println("<li>"+JspUtils.render(request, childGroup)+"</li>");
				}
			%>
			</ul>
			<%
				}
				if (!relatedGroups.isEmpty())
				{
			%>
			<u>Related Groups</u>
			<ul>
			<%
				for (Iterator it=relatedGroups.iterator(); it.hasNext();)
				{
					LinkGroup relatedGroup=(LinkGroup)it.next();
					out.println("<li>"+JspUtils.render(request, relatedGroup, "hierarchy")+"</li>");
				}
			%>
			</ul>
			<%
				}
			%>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
