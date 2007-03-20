<%@ page language="java" %>
<%@ page import="java.util.Iterator,
				 java.util.Map,
				 java.util.TreeMap,
				 java.util.TreeSet,
				 com.kiwisoft.media.show.Season,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowInfo"%>
<%@ page import="com.kiwisoft.utils.StringUtils"%>

<%
	Show show=(Show)request.getAttribute("show");
	Map links=new TreeMap(String.CASE_INSENSITIVE_ORDER);
	links.put("Schedule", "/shows/schedule.jsp?show="+show.getId());
	if (show.getRecordingCount()>0)
		links.put("Records", "/shows/videos.jsp?show="+show.getId());
	if (!show.getMainCast().isEmpty() || !show.getRecurringCast().isEmpty())
		links.put("Cast and Crew", "/shows/cast.jsp?show="+show.getId());
	if (show.getLinkCount()>0)
		links.put("Links", "/shows/links.jsp?show="+show.getId());
	if (show.getFanFicCount()>0)
		links.put("Fan Fiction", "/fanfic/fanfics.jsp?show="+show.getId());
	for (Iterator it=show.getInfos().iterator(); it.hasNext();)
	{
		ShowInfo info=(ShowInfo)it.next();
		links.put(info.getName(), "/"+info.getPath()+"?show="+show.getId());
	}
%>

<table class="menutable">
<tr><td class="menuheader">Show</td></tr>
<%
	String logoPath=show.getLogoMini();
	if (!StringUtils.isEmpty(logoPath))
	{
%>
		<tr><td class="menuitem" align="center"><img style="margin-top:5px" src="/<%=logoPath%>"></td></tr>
		<tr><td><hr size=1 color=black></td></tr>
<%
	}
	Iterator it=new TreeSet(show.getSeasons()).iterator();
	if (!it.hasNext())
	{
%>
		<tr><td class="menuitem"><a class="menulink" href="/shows/episodes.jsp?show=<%=show.getId()%>">Episodes</a></td></tr>
<%
	}
	else
	{
		while (it.hasNext())
		{
			Season season=(Season)it.next();
%>
			<tr><td class="menuitem"><a class="menulink" href="/shows/episodes.jsp?show=<%=show.getId()%>#season<%=season.getNumber()%>"><%=season%></a></td></tr>
<%
		}
	}
	if (!show.getMovies().isEmpty())
	{
%>
	<tr><td class="menuitem"><a class="menulink" href="/shows/episodes.jsp?show=<%=show.getId()%>#movies">Movies</a></td></tr>
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
