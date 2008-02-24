<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kiwisoft.media.photos.Photo" %>
<%@ page import="com.kiwisoft.media.photos.PhotoGallery" %>
<%@ page import="com.kiwisoft.media.photos.PhotoManager" %>
<%@ page import="com.kiwisoft.media.pics.PictureFile" %>
<%@ page import="com.kiwisoft.utils.Utils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="com.kiwisoft.media.Navigation" %>
<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>
<html>

<%
	PhotoGallery gallery=PhotoManager.getInstance().getGallery(new Long(request.getParameter("gallery")));
%>

<head>
<title>Photos - <%=JspUtils.render(request, gallery.getName())%></title>
<script language="JavaScript" src="../overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title>Photos</media:title>
<media:body>
	<media:sidebar>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="<%=JspUtils.render(request, gallery.getName())%>">
			<table cellspacing="0">
			<%
				List rows=Utils.splitIntoRows(gallery.getPhotos(), 4);
				for (Iterator itRows=rows.iterator(); itRows.hasNext();)
				{
					List row=(List)itRows.next();
					out.print("<tr>");
					for (Iterator itPhotos=row.iterator(); itPhotos.hasNext();)
					{
						Photo photo=(Photo)itPhotos.next();
						PictureFile thumbnail=photo.getThumbnail();
			%>
			<td style="width:170px; height:130px; text-align:center; vertical-align:middle; background:url(<%=request.getContextPath()%>/clipart/trans10.png);">
				<a href="<%=Navigation.getLink(request, photo)%>"><%=renderPicture(request, thumbnail, null)%></a>
			</td>
			<%
						if (itPhotos.hasNext()) out.print("<td width=\"10\"></td>");
					}
					out.println("</tr>");
					out.print("<tr>");
					for (Iterator itPhotos=row.iterator(); itPhotos.hasNext();)
					{
						Photo photo=(Photo)itPhotos.next();
			%>
			<td style="width:170px; font-size:8pt; text-align:center; vertical-align:top; background:url(<%=request.getContextPath()%>/clipart/trans20.png);">
				[<%=JspUtils.render(request, photo.getCreationDate())%>]<br>
				<%=JspUtils.render(request, photo.getDescription())%>
			</td>
			<%
						if (itPhotos.hasNext()) out.print("<td width=\"10\"></td>");
					}
					out.println("</tr>");
					if (itRows.hasNext()) out.println("<tr height=\"10\"><td colSpan=\"7\"></td></tr>");
				}
			%>
			</table>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
