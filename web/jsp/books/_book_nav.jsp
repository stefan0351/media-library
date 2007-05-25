<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.books.Book,
				 com.kiwisoft.media.pics.Picture" %>
<%@ page import="com.kiwisoft.media.pics.PictureFile"%>

<%
	Book book=(Book)request.getAttribute("book");
	Picture cover=book.getCover();
	PictureFile thumbnail=null;
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
<tr><td class="menuitem" align="center"><%=renderPicture("Cover", cover, thumbnail, null)%></td></tr>
</table>
<%
	}
%>
