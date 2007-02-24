<%--
$Revision: 1.2 $, $Date: 2004/07/31 13:26:54 $
--%>

<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.*,
				 com.kiwisoft.utils.gui.table.SortableTableModel,
				 com.kiwisoft.utils.gui.table.TableSortDescription,
				 com.kiwisoft.media.ui.MediaManagerFrame,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.media.show.ShowManager,
				 com.kiwisoft.media.ui.show.ShowRecordsTableModel,
				 com.kiwisoft.media.Language,
				 com.kiwisoft.media.video.Recording,
				 com.kiwisoft.media.show.Episode"%>

<%
	Show show=ShowManager.getInstance().getShow(new Long(request.getParameter("show")));
	request.setAttribute("show", show);
	ShowRecordsTableModel model=new ShowRecordsTableModel(show);
	ResourceBundle tableResources=ResourceBundle.getBundle(MediaManagerFrame.class.getName());
	try
	{
		String pSort=request.getParameter("sort");
		if (pSort!=null)
		{
			int sort=Integer.parseInt(pSort);
			Integer sortDir="desc".equals(request.getParameter("dir")) ? SortableTableModel.DESCEND : SortableTableModel.ASCEND;
			model.addSortColumn(new TableSortDescription(sort, sortDir));
			model.sort();
		}
	}
	catch (NumberFormatException e)
	{
	}
%>
<html>

<head>
<title>Videos</title>
<script language="JavaScript" src="/clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<jsp:include page="_shows_nav.jsp"/>
<jsp:include page="_show_nav.jsp" />
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<jsp:include page="/shows/_show_logo.jsp"/>
	</td></tr></table>
</div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
		<span style="font-weight:bold;font-size:24pt;"><%=show.getName()%></span>
	</td></tr></table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navShows(2)" onMouseOut="nd()">Serien</a></div>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Aufnahmen</td></tr>
</table>
<br>
<table bordercolor=black border=1 cellspacing=0 width="690">
<tr bgcolor="#eeeeee">
<%
	for (int i=0;i<model.getColumnCount();i++)
	{
		TableSortDescription sortDescription=model.getSortDescription(i);
		String sortDir;
		if (sortDescription!=null && SortableTableModel.ASCEND.equals(sortDescription.getDirection())) sortDir="desc";
		else sortDir="asc";
		out.print("<th><a class=link_nav href=\"videos.jsp?show="+show.getId()+"&sort="+i+"&dir="+sortDir+"\">");
		String columnName=model.getColumnName(i);
		try
		{
			columnName=tableResources.getString("table.show.recordings."+columnName);
		}
		catch (MissingResourceException e)
		{
		}
		out.print(columnName);
		if (sortDescription!=null)
		{
			if (SortableTableModel.ASCEND.equals(sortDescription.getDirection()))
				out.print("<img src=\"/clipart/ascend.gif\" border=0 hspace=3>");
			else
				out.print("<img src=\"/clipart/descend.gif\" border=0 hspace=3>");
		}
		out.print("</a></th>");
	}
%>
</tr>
<%
	for (int row=0;row<model.getRowCount();row++)
	{
		out.print("<tr>");
		for (int col=0;col<model.getColumnCount();col++)
		{
			Recording recording=(Recording)model.getObject(row);
			Object value=model.getValueAt(row, col);
			out.print("<td");
			if (value instanceof Number) out.print(" align=right");
			else if (value instanceof Language) out.print(" align=center");
			out.print(">");
			if (value instanceof Language)
			{
				Language language=(Language)value;
				out.print("<img src=\"/clipart/flag_"+language.getSymbol()+".gif\"> "+language.getName());
			}
			else if (value!=null)
			{
				if (col==0 || col==1)
				{
					Episode episode=recording.getEpisode();
					if (episode!=null)
					{
						String link=episode.getLink();
						if (link!=null) out.print("<a class=\"link\" href=\""+link+"\">"+value+"</a>");
						else out.print(value);
					}
					else
					{
						out.print(value);
					}
				}
				else if (col==2) out.print("<a class=\"link\" href=\"/videos/video.jsp?id="+recording.getVideo().getId()+"\">"+value+"</a>");
				else out.print(value);
			}
			out.print("</td>");
		}
		out.println("</tr>");
	}
%>
</table>

<p align=right><a class=link href="#top">Top</a></p>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
