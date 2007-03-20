<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import = "java.util.Calendar,
				   com.kiwisoft.media.AirdateManager,
				   com.kiwisoft.media.schedule.ScheduleTable" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	ScheduleTable table=new ScheduleTable();
	request.setAttribute("table", table);
	String period=request.getParameter("period");
	String title;
	if ("today".equals(period))
	{
		title="Today";
		table.addAll(AirdateManager.getInstance().getAirdatesToday());
	}
	else if ("week".equals(period))
	{
		title="Next Week";
		table.addAll(AirdateManager.getInstance().getAirdates(Calendar.DATE, 7));
	}
	else if ("month".equals(period))
	{
		title="Next Month";
		table.addAll(AirdateManager.getInstance().getAirdates(Calendar.MONTH, 1));
	}
	else
	{
		title="Tomorrow";
		table.addAll(AirdateManager.getInstance().getAirdates(Calendar.DATE, 1));
	}

%>
<html>

<head>
<title>Schedule - <%=title%></title>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<media:title>Schedule - <%=title%></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_schedule_nav.jsp"/>
		<jsp:include page="/_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%="Schedule - "+title%>">
			<media:table model="table" alternateRows="true"/>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
