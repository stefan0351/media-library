<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>

<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page import="com.kiwisoft.media.*" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.media.schedule.ScheduleTable" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.person.PersonManager" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Long id=new Long(request.getParameter("person"));
	Person person=PersonManager.getInstance().getPerson(id);
	request.setAttribute("person", person);
	String rangeId=request.getParameter("range");
	DateRange range=null;
	if (!StringUtils.isEmpty(rangeId)) range=DateRange.get(Long.valueOf(rangeId));
	if (range==null) range=DateRange.NEXT_24_HOURS;
	ScheduleTable table=new ScheduleTable(person, range);
	table.sort();
	request.setAttribute("table", table);
%>
<html>

<head>
<title><%=StringEscapeUtils.escapeHtml(person.getName())%> - Schedule</title>
<script language="JavaScript" src="../overlib.js"></script>
<script language="JavaScript" src="../popup.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv"></div>

<media:title><%=StringEscapeUtils.escapeHtml(person.getName())%></media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Schedule">
			Date Range: <select name="range" size="1" onChange="window.location.href='<%=request.getContextPath()%>/persons/schedule.jsp?person=<%=person.getId()%>&range='+this.value">
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
