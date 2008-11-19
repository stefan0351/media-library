<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="com.kiwisoft.media.files.MediaFile" %>
<%@ page import="com.kiwisoft.media.files.ImageFile" %>
<%@ page import="com.kiwisoft.media.person.CreditType" %>
<%@ page import="com.kiwisoft.media.show.Season" %>
<%@ page import="com.kiwisoft.media.show.Show" %>
<%@ page import="com.kiwisoft.media.show.ShowInfo" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>

<%
	Show show=(Show)request.getAttribute("show");
	Season season=(Season)request.getAttribute("season");

	Map links=new TreeMap(String.CASE_INSENSITIVE_ORDER);
	links.put("Schedule", request.getContextPath()+"/shows/schedule.jsp?show="+show.getId());
	if (show.getRecordingCount()>0) links.put("Media", request.getContextPath()+"/shows/tracks.jsp?show="+show.getId());
	if (!show.getCastMembers(CreditType.MAIN_CAST).isEmpty() || !show.getCastMembers(CreditType.RECURRING_CAST).isEmpty())
		links.put("Cast and Crew", request.getContextPath()+"/shows/cast.jsp?show="+show.getId());
	if (show.getLinkGroup()!=null && show.getLinkGroup().getLinkCount()>0)
		links.put("Links", request.getContextPath()+"/links.jsp?show="+show.getId()+"&group="+show.getLinkGroup().getId());
	if (show.getFanFicCount()>0)
		links.put("Fan Fiction", request.getContextPath()+"/fanfic/fanfics.jsp?show="+show.getId());
	for (Iterator it=show.getInfos().iterator(); it.hasNext();)
	{
		ShowInfo info=(ShowInfo)it.next();
		links.put(info.getName(), request.getContextPath()+"/resource?file="+info.getPath()+"&show="+show.getId());
	}
%>

<table class="menutable">
<tr><td class="menuheader">Show</td></tr>
<%
	ImageFile thumbnail=null;
	MediaFile logo=null;
	if (season!=null) logo=season.getLogo();
	if (logo==null) logo=show.getLogo();
	if (logo!=null)
	{
		thumbnail=logo.getThumbnailSidebar();
		if (thumbnail==null && logo.getWidth()<=170) thumbnail=logo;
	}
	if (logo!=null && thumbnail!=null)
	{
%>
		<tr><td class="menuitem" align="center">
<%
			out.println(renderMedia(request, "Logo", logo, thumbnail, null));
%>
		<tr><td><hr size=1 color=black></td></tr>
<%
	}
	Iterator it=new TreeSet(show.getSeasons()).iterator();
	if (!it.hasNext())
	{
%>
		<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/shows/episodes.jsp?show=<%=show.getId()%>">Episodes</a></td></tr>
<%
	}
	else
	{
		while (it.hasNext())
		{
%>
			<tr><td class="menuitem"><%=JspUtils.render(request, it.next(), "Menu")%></td></tr>
<%
		}
	}
	if (!show.getMovies().isEmpty())
	{
%>
	<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/shows/episodes.jsp?show=<%=show.getId()%>#movies">Movies</a></td></tr>
<%
	}
%>
	<tr><td><hr size=1 color=black></td></tr>
<%
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
