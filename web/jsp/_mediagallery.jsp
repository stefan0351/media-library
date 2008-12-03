<%@ page contentType="text/html;charset=UTF-8" language="java" extends="com.kiwisoft.media.MediaJspBase" %>

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.kiwisoft.collection.SortedSetMap" %>
<%@ page import="com.kiwisoft.format.FormatStringComparator" %>
<%@ page import="com.kiwisoft.media.Navigation" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>
<%@ page import="com.kiwisoft.media.files.MediaFileUtils" %>
<%@ page import="com.kiwisoft.media.files.MediaType" %>
<%@ page import="com.kiwisoft.utils.Utils" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Set mediaFiles=(Set)request.getAttribute("mediaFiles");
	SortedSetMap groups=new SortedSetMap(String.CASE_INSENSITIVE_ORDER, new FormatStringComparator());
	for (Iterator it=mediaFiles.iterator(); it.hasNext();)
	{
		MediaFile mediaFile=(MediaFile)it.next();
		if (mediaFile.getContentType()!=null) groups.add(mediaFile.getContentType().getPluralName(), mediaFile);
		else groups.add("Unsorted", mediaFile);
	}
	for (Iterator it=groups.keySet().iterator(); it.hasNext();)
	{
		String groupName=(String)it.next();
		Set groupFiles=groups.get(groupName);
%>

<table class="contenttable" width="765">
<tr><td class="header2"><media:render value="<%=groupName%>"/></td></tr>
<tr><td class="content">


<table cellspacing="0">
<%
	List rows=Utils.splitIntoRows(groupFiles, 4);
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
				if (MediaFileUtils
					.isThumbnailSize(mediaFile.getWidth(), mediaFile.getHeight(), MediaFileUtils.THUMBNAIL_WIDTH, MediaFileUtils.THUMBNAIL_HEIGHT))
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
		MediaFile mediaFile=(MediaFile)itFiles.next();
%>
<td style="width:170px; font-size:8pt; text-align:center; vertical-align:top; background:url(<%=request.getContextPath()%>/clipart/trans20.png);">
	<media:render value="<%=mediaFile.getName()%>"/>
</td>
<%
			if (itFiles.hasNext()) out.print("<td width=\"10\"></td>");
		}
		out.println("</tr>");
		if (itRows.hasNext()) out.println("<tr height=\"10\"><td colSpan=\"7\"></td></tr>");
	}
%>
</table>
	</td></tr>
</table>
<%
	}
%>
