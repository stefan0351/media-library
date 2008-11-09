<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>

<%@ page import="java.util.Calendar" %>
<%@ page import="com.kiwisoft.media.AirdateManager" %>
<%@ page import="com.kiwisoft.media.schedule.ScheduleTable" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.media.DateRange" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String rangeId=request.getParameter("range");
	DateRange range=null;
	if (!StringUtils.isEmpty(rangeId)) range=DateRange.get(Long.valueOf(rangeId));
	if (range==null) range=DateRange.NEXT_24_HOURS;
	ScheduleTable table=new ScheduleTable(null, range);
	table.sort();
	request.setAttribute("table", table);
%>
<html>

<head>
<title>Schedule - <media:render value="<%=range%>"/></title>
<link rel="StyleSheet" type="text/css" href="style.css">
</head>

<body>
<a name="top"></a>

<media:title>Schedule - <media:render value="<%=range%>"/></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_schedule_nav.jsp"/>
		<jsp:include page="_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%="Schedule - "+range%>">
			<media:table model="table" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
