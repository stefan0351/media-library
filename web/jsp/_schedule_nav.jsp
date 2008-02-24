<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<table class="menutable">
<tr><td class="menuheader">Schedule</td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/schedule.jsp?period=today">Today</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/schedule.jsp">Tomorrow</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/schedule.jsp?period=week">Next Week</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/schedule.jsp?period=month">Next Month</a></td></tr>
</table>
