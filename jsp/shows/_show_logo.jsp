<%@ page language="java" %>
<%@ page import="com.kiwisoft.utils.StringUtils,
				 com.kiwisoft.media.show.Show"%>

<%
	Show show=(Show)request.getAttribute("show");
	if (show!=null)
	{
		String logoPath=show.getLogoMini();
		if (!StringUtils.isEmpty(logoPath))
		{
%>
			<img src="/<%=logoPath%>">
<%
		}
	}
%>
