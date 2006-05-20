<%@ page language="java" %>
<%@ page import="com.kiwisoft.xp.XPBean,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.Season,
				 com.kiwisoft.media.fanfic.FanDom,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.EpisodeInfo,
				 com.kiwisoft.media.show.ShowInfo,
				 java.util.*"%>

<%
	Show show=(Show)request.getAttribute("show");
	Map links=new TreeMap(String.CASE_INSENSITIVE_ORDER);
	links.put("Sendetermine", "/shows/airdates.jsp?show="+show.getId());
	if (show.getRecordingCount()>0)
		links.put("Aufnahmen", "/shows/videos.jsp?show="+show.getId());
	if (!show.getMainCast().isEmpty() || !show.getRecurringCast().isEmpty())
		links.put("Darsteller", "/shows/cast.jsp?show="+show.getId());
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

<script language="JavaScript"><!--
function navShow(pos)
{
	text="";
<%
	Iterator it=new TreeSet(show.getSeasons()).iterator();
	if (!it.hasNext())
	{
%>
		text+="<a onClick=\"nd()\" class=link2_nav href=\"/shows/episodes.jsp?show=<%=show.getId()%>\">Episoden</a><br>";
<%
	}
	else
	{
		while (it.hasNext())
		{
			Season season=(Season)it.next();
%>
			text+="<a onClick=\"nd()\" class=link2_nav href=\"/shows/episodes.jsp?show=<%=show.getId()%>#season<%=season.getNumber()%>\"><%=season%></a><br>";
<%
		}
	}
	if (!show.getMovies().isEmpty())
	{
%>
	text+="<a onClick=\"nd()\" class=link2_nav href=\"/shows/episodes.jsp?show=<%=show.getId()%>#movies\">Filme</a><br>";
<%
	}
%>
	text+="<hr size=1 color=black>";
<%
	for (Iterator itLinks=links.keySet().iterator(); itLinks.hasNext();)
	{
		String name=(String)itLinks.next();
		String ref=(String)links.get(name);
%>
			text+="<a onClick=\"nd()\" class=link2_nav href=\"<%=ref%>\"><%=name%></a><br>";
<%
	}
%>
	return overlib(text,STICKY,CAPTION,"Serie",FIXX,pos*150-50,FIXY,140,WIDTH,200,CAPCOLOR,"white",BGCOLOR,"black",FGCOLOR,"white");
}
//--></script>
