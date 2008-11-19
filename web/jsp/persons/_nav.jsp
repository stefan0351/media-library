<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>

<table class="menutable">
<tr>
	<td class="menuheader">Person</td>
</tr>
<%
	Person person=(Person)request.getAttribute("person");
	MediaFile picture=person.getPicture();
	ImageFile thumbnail=null;
	if (picture!=null)
	{
		thumbnail=picture.getThumbnailSidebar();
		if (thumbnail==null && picture.getWidth()<=170) thumbnail=picture;
	}
	if (thumbnail!=null)
	{
%>
<tr>
	<td class="menuitem" align="center"><%=renderMedia(request, "Photo", picture, thumbnail, null)%></td>
</tr>
<tr>
	<td>
		<hr size=1 color=black>
	</td>
</tr>
<%
	}
%>
</table>
