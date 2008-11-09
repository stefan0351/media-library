<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.kiwisoft.media.DateRange" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<table class="menutable">
<tr><td class="menuheader">Schedule</td></tr>
<%
	Collection ranges=DateRange.values();
	for (Iterator it=ranges.iterator(); it.hasNext();)
	{
		DateRange range=(DateRange)it.next();
		if (range!=DateRange.CUSTOM)
		{
%>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/schedule.jsp?range=<%=range.getId()%>"><media:render value="<%=range%>"/></a></td></tr>
<%
		}
	}
%>
</table>
