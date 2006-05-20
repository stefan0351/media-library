<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.net.URLEncoder,
                 com.kiwisoft.utils.AlphabeticalMap,
                 com.kiwisoft.utils.Configurator,
                 java.util.*,
				 com.kiwisoft.utils.gui.table.SortableTableModel,
				 com.kiwisoft.utils.gui.table.SortableTableRow,
				 com.kiwisoft.media.ui.video.VideosTableModel,
				 com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.video.Video,
				 com.kiwisoft.utils.gui.table.TableSortDescription,
				 com.kiwisoft.media.video.MediumType,
				 com.kiwisoft.media.ui.MediaManagerFrame"%>

<%
	String typeId=request.getParameter("type");
	MediumType type=null;
	if (typeId!=null) type=MediumType.get(new Long(typeId));
	VideosTableModel model=new VideosTableModel(type);
	ResourceBundle tableResources=ResourceBundle.getBundle(MediaManagerFrame.class.getName());
	try
	{
		String pSort=request.getParameter("sort");
		if (pSort!=null)
		{
			int sort=Integer.parseInt(pSort);
			Integer sortDir="desc".equals(request.getParameter("dir")) ? SortableTableModel.DESCEND : SortableTableModel.ASCEND;
			model.addSortColumn(new TableSortDescription(sort, sortDir));
		}
		else model.addSortColumn(new TableSortDescription(0, SortableTableModel.ASCEND));
		model.sort();
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
<jsp:include page="_videos_nav.jsp"/>
<link rel="StyleSheet" type="text/css" href="/clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo"><%--<img style="margin-top:13px;" src="/clipart/logo_mini.gif">--%></div>
<div class="title">
	<table width=590 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<span style="font-weight:bold;font-size:24pt;">Videos</span>
	</table>
</div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'/')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navVideos(2)" onMouseOut="nd()">Videos</a></div>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;&Uuml;bersicht</td></tr>
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
		out.print("<th><a class=link_nav href=\"index.jsp?sort="+i+"&dir="+sortDir);
		if (type!=null) out.print("&type="+type.getId());
		out.print("\">");
		String columnName=model.getColumnName(i);
		try
		{
			columnName=tableResources.getString("table.videos."+columnName);
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
			out.print("<td");
			Object value=model.getValueAt(row, col);
			if (value instanceof Number) out.print(" align=right");
			Video video=(Video)model.getRow(row).getUserObject();
			out.print("><a class=link_nav href=\"video.jsp?id="+video.getId()+"\">");
			if (value!=null) out.print(value);
			out.print("</a></td>");
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
