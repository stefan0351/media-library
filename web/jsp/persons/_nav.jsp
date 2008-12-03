<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.media.person.Person" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.kiwisoft.media.files.MediaFileManager" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.kiwisoft.media.files.MediaType" %>

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

	Map links=new TreeMap(String.CASE_INSENSITIVE_ORDER);

	links.put("Details", request.getContextPath()+"/persons/person.jsp?id="+person.getId()+"#details");
	links.put("Filmography", request.getContextPath()+"/persons/person.jsp?id="+person.getId()+"#filmography");
	links.put("Schedule", request.getContextPath()+"/persons/schedule.jsp?person="+person.getId());
	if (MediaFileManager.getInstance().getNumberOfMediaFiles(person, MediaType.VIDEO)>0)
		links.put("Videos", request.getContextPath()+"/persons/mediafiles.jsp?person="+person.getId()+"&type="+MediaType.VIDEO.getId());
	if (MediaFileManager.getInstance().getNumberOfMediaFiles(person, MediaType.IMAGE)>0)
		links.put("Images", request.getContextPath()+"/persons/mediafiles.jsp?person="+person.getId()+"&type="+MediaType.IMAGE.getId());

	for (Iterator itLinks=links.keySet().iterator(); itLinks.hasNext();)
	{
		String name=(String)itLinks.next();
		String ref=(String)links.get(name);
%>
		<tr><td class="menuitem"><a class="menulink" href="<%=ref%>"><%=name%></a></td></tr>
<%
	}
%>
</table>
