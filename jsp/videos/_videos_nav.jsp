<%@ page language="java" %>
<%@ page import="com.kiwisoft.xp.XPBean,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.ShowType,
				 com.kiwisoft.media.video.MediumType,
				 java.util.*"%>

<script language="JavaScript"><!--
function navVideos(pos)
{
	text="<a class=link2_nav href=\"/videos/index.jsp\">Alle</a><br>";
<%
	Map types=new TreeMap(String.CASE_INSENSITIVE_ORDER);
	for (Iterator it=MediumType.getAll().iterator(); it.hasNext();)
	{
		MediumType type=(MediumType)it.next();
		types.put(type.getPluralName(), type);
	}
	for (Iterator it=types.keySet().iterator(); it.hasNext();)
	{
		MediumType type=(MediumType)types.get(it.next());
%>
		text+="<a class=link2_nav href=\"/videos/index.jsp?type=<%=type.getId()%>\"><%=type.getPluralName()%></a><br>";
<%
	}
%>
	text+="<hr size=1 color=black>";
	text+="<a target=_new class=link2_nav href=\"/videos/print.pdf?xsl=/videos/print.xsl\">Druckansicht</a><br>";
	return overlib(text,STICKY,CAPTION,"Videos",FIXX,pos*150-50,FIXY,140,WIDTH,150,CAPCOLOR,"white",BGCOLOR,"black",FGCOLOR,"white");
}
//--></script>
