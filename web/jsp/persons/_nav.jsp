<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.pics.Picture" %>
<%@ page import="com.kiwisoft.media.pics.PictureFile" %>

<table class="menutable">
<tr>
	<td class="menuheader">Person</td>
</tr>
<%
	Person person=(Person)request.getAttribute("person");
	Picture picture=person.getPicture();
	PictureFile thumbnail=null;
	if (picture!=null)
	{
		thumbnail=picture.getThumbnailSidebar();
		if (thumbnail==null && picture.getWidth()<=170) thumbnail=picture;
	}
	if (thumbnail!=null)
	{
%>
<tr>
	<td class="menuitem" align="center"><%=renderPicture(request, "Photo", picture, thumbnail, null)%></td>
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
