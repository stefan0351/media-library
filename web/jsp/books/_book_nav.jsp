<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.books.Book" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>

<%
	Book book=(Book)request.getAttribute("book");
	MediaFile cover=book.getCover();
	ImageFile thumbnail=null;
	if (cover!=null)
	{
		thumbnail=cover.getThumbnailSidebar();
		if (thumbnail==null && cover.getWidth()<=170) thumbnail=cover;
	}
%>

<%
	if (thumbnail!=null)
	{
%>
<table class="menutable">
<tr><td class="menuheader">Book</td></tr>
<tr><td class="menuitem" align="center"><%=renderMedia(request, "Cover", cover, thumbnail, null)%></td></tr>
</table>
<%
	}
%>
