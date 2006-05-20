<%@ page language="java" %>
<%@ page import="com.kiwisoft.xp.XPBean,
                 java.io.StringWriter,
                 java.io.PrintWriter"%>

<%
	Throwable error=(Throwable)request.getAttribute("error");
%>
<html>

<head>
<title>Error</title>
</head>

<body>

<p style="color:red"><b><%=error.getMessage()%></b></p>

<pre><% error.printStackTrace(new PrintWriter(out)); %></pre>

</body>
</html>
