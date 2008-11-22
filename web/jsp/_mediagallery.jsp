<%@ page contentType="text/html;charset=UTF-8" language="java" extends="com.kiwisoft.media.MediaJspBase"%>

<%@ page import="java.util.Iterator" %>
<%@ page import="com.kiwisoft.utils.Utils" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>
<%@ page import="com.kiwisoft.media.Navigation" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.kiwisoft.media.files.MediaFileUtils" %>
<%@ page import="com.kiwisoft.media.files.MediaType" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Set mediaFiles=(Set)request.getAttribute("mediaFiles");
%>


<table cellspacing="0">
<%
	List rows=Utils.splitIntoRows(mediaFiles, 4);
	for (Iterator itRows=rows.iterator(); itRows.hasNext();)
	{
		List row=(List)itRows.next();
		out.print("<tr>");
		for (Iterator itFiles=row.iterator(); itFiles.hasNext();)
		{
			MediaFile mediaFile=(MediaFile)itFiles.next();
			ImageFile thumbnail=mediaFile.getThumbnail();
			if (thumbnail==null && mediaFile.getMediaType()==MediaType.IMAGE)
			{
				if (MediaFileUtils.isThumbnailSize(mediaFile.getWidth(), mediaFile.getHeight(), MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT))
				{
					thumbnail=mediaFile;
				}
			}
%>
<td style="width:170px; height:130px; text-align:center; vertical-align:middle; background:url(<%=request.getContextPath()%>/clipart/trans10.png);">
	<a href="<%=Navigation.getLink(request, mediaFile)%>"><%=renderImage(request, thumbnail, null)%></a>
</td>
<%
			if (itFiles.hasNext()) out.print("<td width=\"10\"></td>");
		}
		out.println("</tr>");
		out.print("<tr>");
		for (Iterator itFiles=row.iterator(); itFiles.hasNext();)
		{
			MediaFile video=(MediaFile)itFiles.next();
%>
<td style="width:170px; font-size:8pt; text-align:center; vertical-align:top; background:url(<%=request.getContextPath()%>/clipart/trans20.png);">
	<media:render value="<%=video.getName()%>"/>
</td>
<%
			if (itFiles.hasNext()) out.print("<td width=\"10\"></td>");
		}
		out.println("</tr>");
		if (itRows.hasNext()) out.println("<tr height=\"10\"><td colSpan=\"7\"></td></tr>");
	}
%>
</table>
