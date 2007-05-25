<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.pics.Picture" %>
<%@ page import="com.kiwisoft.media.pics.PictureFile" %>
<%@ page language="java" %>

<table class="menutable">
<tr>
	<td class="menuheader">Person</td>
</tr>
<%
	Person person=(Person)request.getAttribute("person");
	Picture picture=person.getPicture();
	if (picture!=null)
	{
		String fileName=null;
		PictureFile thumbnail=picture.getThumbnailSidebar();
		if (thumbnail!=null) fileName=thumbnail.getFile();
		else if (picture.getWidth()<=170) fileName=picture.getFile();
		if (fileName!=null)
		{
%>
<tr>
	<td class="menuitem" align="center"><img style="margin-top:5px" src="/<%=fileName.replace('\\', '/')%>"></td>
</tr>
<tr>
	<td>
		<hr size=1 color=black>
	</td>
</tr>
<%
		}
	}
%>
</table>
