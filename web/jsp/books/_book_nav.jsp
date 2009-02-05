<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.books.Book" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>
<%@ page import="com.kiwisoft.media.files.MediaFileUtils" %>

<%
    Book book=(Book) request.getAttribute("book");
    MediaFile cover=book.getCover();
    ImageFile thumbnail=null;
    if (cover!=null)
    {
        thumbnail=cover.getThumbnailSidebar();
        if (thumbnail==null && cover.getWidth()<=MediaFileUtils.THUMBNAIL_SIDEBAR_WIDTH) thumbnail=cover;
    }
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
