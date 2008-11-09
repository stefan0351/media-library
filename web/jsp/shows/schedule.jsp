<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>

<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.kiwisoft.media.*" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.media.schedule.ScheduleTable" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.media.show.ShowManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long showId=new Long(request.getParameter("show"));
	Show show=ShowManager.getInstance().getShow(showId);
	request.setAttribute("show", show);
	String rangeId=request.getParameter("range");
	DateRange range=null;
	if (!StringUtils.isEmpty(rangeId)) range=DateRange.get(Long.valueOf(rangeId));
	if (range==null) range=DateRange.NEXT_24_HOURS;
	ScheduleTable table=new ScheduleTable(show, range);
	table.sort();
	request.setAttribute("table", table);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(show.getTitle())%> - Schedule</title>
<script language="JavaScript" src="../overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>

<media:title><%=StringEscapeUtils.escapeHtml(show.getTitle())%></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_show_nav.jsp"/>
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Schedule">
			Date Range: <select name="range" size="1" onChange="window.location.href='<%=request.getContextPath()%>/shows/schedule.jsp?show=<%=show.getId()%>&range='+this.value">
<%
	Collection ranges=DateRange.values();
	for (Iterator it=ranges.iterator(); it.hasNext();)
	{
		DateRange r=(DateRange)it.next();
		if (r!=DateRange.CUSTOM)
		{
%>
				<option value="<%=r.getId()%>" <%=r==range ? "selected" : ""%>><media:render value="<%=r%>"/></option>
<%
		}
	}
%>
			</select><br/><br/>
			<media:table model="table" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
