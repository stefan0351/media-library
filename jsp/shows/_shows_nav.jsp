<%@ page language="java" %>
<%@ page import="com.kiwisoft.xp.XPBean,
                 java.util.Collection,
                 java.util.Iterator,
				 java.util.SortedSet,
				 java.util.TreeSet,
				 com.kiwisoft.utils.StringComparator,
				 com.kiwisoft.media.show.ShowType"%>

<script language="JavaScript"><!--
function navShows(pos)
{
	text="";
<%
		SortedSet types=new TreeSet(new StringComparator());
		types.addAll(ShowType.getAll());
		Iterator itTypes=types.iterator();
		while (itTypes.hasNext())
		{
			ShowType type=(ShowType)itTypes.next();
%>
			text+="<a onClick=\"nd()\" class=link2_nav href=\"/shows/index.jsp#type<%=type.getId()%>\"><%=type.getName()%></a><br>";
<%
		}
%>
	return overlib(text,STICKY,CAPTION,"Serie",FIXX,pos*150-50,FIXY,140,WIDTH,150,CAPCOLOR,"white",BGCOLOR,"black",FGCOLOR,"white");
}
//--></script>
