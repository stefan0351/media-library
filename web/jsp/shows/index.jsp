<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.*,
				 org.apache.commons.lang.StringEscapeUtils,
				 com.kiwisoft.media.Language,
				 com.kiwisoft.media.LanguageManager,
				 com.kiwisoft.media.show.Show"%>
<%@ page import="com.kiwisoft.media.show.ShowManager"%>
<%@ page import="com.kiwisoft.utils.Utils"%>
<%@ page import="com.kiwisoft.web.JspUtils"%>
<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	String letterString=request.getParameter("letter");
	SortedSet letters=ShowManager.getInstance().getLetters();
	Set shows=new TreeSet(new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			Show show1=(Show)o1;
			Show show2=(Show)o2;
			int result=Utils.compareNullSafe(show1.getIndexBy(), show2.getIndexBy(), false);
			if (result==0) result=show1.getId().compareTo(show2.getId());
			return result;
		}
	});
	if ("all".equalsIgnoreCase(letterString))
	{
		shows.addAll(ShowManager.getInstance().getShows());
		letterString="All";
	}
	else
	{
		char selectedLetter=letterString!=null && letterString.length()==1 ? letterString.charAt(0) : ((Character)letters.first()).charValue();
		shows.addAll(ShowManager.getInstance().getShowsByLetter(selectedLetter));
		letterString=String.valueOf(selectedLetter);
	}
	Language german=LanguageManager.getInstance().getLanguageBySymbol("de");
%>

<html>

<head>
<title>Shows</title>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/style.css"/>
<script language="JavaScript" src="<%=request.getContextPath()%>/overlib.js"></script>
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<media:title>Shows</media:title>

<media:body>
	<media:sidebar>
		<jsp:include page="_shows_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
		<media:panel title="Index">
			<table width="765"><tr><td class="content2"><small>[
<%
				for (Iterator it=letters.iterator(); it.hasNext();)
				{
					Character letter=(Character)it.next();
%>
					<a class=link href="<%=request.getContextPath()%>/shows/index.jsp?letter=<%=letter%>"><%=letter%></a> |
<%
				}
%>
				<a class="link" href="<%=request.getContextPath()%>/shows/index.jsp?letter=all">All</a>
			] (<%=ShowManager.getInstance().getShowCount() %> Shows)</small></td></tr></table>
			<br>
			<table width="765">
			<tr valign=top>
				<td class="content2" width="20"><b><a name="<%=letterString%>"><%=letterString%></a></b></td>
				<td class="content2" width=700>
					<ul>
<%
					for (Iterator itShows=shows.iterator(); itShows.hasNext();)
					{
						Show show=(Show)itShows.next();
%>
						<li><b><%=JspUtils.render(request, show)%></b>
<%
						String yearString=show.getYearString();
						if (yearString!=null)
						{
							out.print(" (");
							out.print(yearString);
							out.println(")");
						}
						if (show.getLanguage()!=german)
						{
							out.print("<br>a.k.a. <i>");
							out.print(StringEscapeUtils.escapeHtml(show.getGermanTitle()));
							out.println("</i>");
                        }
					}
%>
					</ul>
				</td>
			</tr>
			</table>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
