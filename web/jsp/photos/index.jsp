<%@ page import="com.kiwisoft.media.photos.PhotoGallery" %>
<%@ page import="com.kiwisoft.media.photos.PhotoManager" %>
<%@ page import="com.kiwisoft.media.pics.PictureFile" %>
<%@ page import="com.kiwisoft.utils.Utils" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="com.kiwisoft.utils.format.FormatStringComparator" %>
<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>
<html>

<head>
<title>Photos</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>

<div id="overDiv" class="over_lib"></div>

<media:title>Photos</media:title>
<media:body>
	<media:sidebar>
		<jsp:include page="/_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Galleries">
			<table cellspacing="0">
			<%
				List galleries=new ArrayList(PhotoManager.getInstance().getGalleries());
				Collections.sort(galleries, new FormatStringComparator());
				List rows=Utils.splitIntoRows(galleries, 4);
				for (Iterator itRows=rows.iterator(); itRows.hasNext();)
				{
					List row=(List)itRows.next();
					out.print("<tr>");
					for (Iterator itGalleries=row.iterator(); itGalleries.hasNext();)
					{
						PhotoGallery gallery=(PhotoGallery)itGalleries.next();
						PictureFile thumbnail=gallery.getThumbnail();
			%>
			<td style="width:170px; height:130px; text-align:center; vertical-align:middle; background:url(/clipart/trans10.png);">
				<a href="/photos/gallery.jsp?gallery=<%=gallery.getId()%>"><%=renderPicture(thumbnail, null)%></a>
			</td>
			<%
						if (itGalleries.hasNext()) out.print("<td width=\"10\"></td>");
					}
					out.println("</tr>");
					out.print("<tr>");
					for (Iterator itGalleries=row.iterator(); itGalleries.hasNext();)
					{
						PhotoGallery gallery=(PhotoGallery)itGalleries.next();
			%>
			<td style="width:170px; font-size:8pt; text-align:center; vertical-align:top; background:url(/clipart/trans20.png);">
				[<%=JspUtils.render(gallery.getCreationDate(), "Date only")%>]<br>
				<%=JspUtils.render(gallery.getName())%>
			</td>
			<%
						if (itGalleries.hasNext()) out.print("<td width=\"10\"></td>");
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
