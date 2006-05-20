<%@ page language="java" %>
<%@ page import="com.kiwisoft.xp.XPBean,
                 java.util.Collection,
                 java.util.Iterator,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.Season,
				 java.util.Collections,
				 java.util.TreeSet,
				 com.kiwisoft.media.fanfic.FanDom,
				 java.util.Set,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.EpisodeInfo"%>

<%
	Episode episode=(Episode)request.getAttribute("episode");
%>

<script language="JavaScript"><!--
function navEpisode(pos)
{
	text="";
<%
	Set infos=new TreeSet(new StringComparator());
	infos.addAll(episode.getInfos());
	for (Iterator it=infos.iterator(); it.hasNext();)
	{
		EpisodeInfo info=(EpisodeInfo)it.next();
%>
		text+="<a onClick=\"nd()\" class=link2_nav href=\"/<%=info.getPath()%>?episode=<%=episode.getId()%>\"><%=info.getName()%></a><br>";
<%
	}
%>
	return overlib(text,STICKY,CAPTION,"Serie",FIXX,pos*150-50,FIXY,140,WIDTH,200,CAPCOLOR,"white",BGCOLOR,"black",FGCOLOR,"white");
}
//--></script>
