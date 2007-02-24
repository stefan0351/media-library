<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.*,
				 com.kiwisoft.utils.gui.table.SortableTableModel,
				 com.kiwisoft.media.ui.video.VideosTableModel,
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
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Videos</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
<td width="200">
<!--Navigation Start-->

<jsp:include page="/_nav.jsp"/>
<jsp:include page="_videos_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1">&Uuml;bersicht</td></tr>
<tr><td class="content">
	<table class="contenttable" width="765">
	<tr>
<%
		for (int i=0;i<model.getColumnCount();i++)
		{
			TableSortDescription sortDescription=model.getSortDescription(i);
			String sortDir;
			if (sortDescription!=null && SortableTableModel.ASCEND.equals(sortDescription.getDirection())) sortDir="desc";
			else sortDir="asc";
			out.print("<td class=\"header2\"><a class=\"hiddenlink\" href=\"index.jsp?sort="+i+"&dir="+sortDir);
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
			out.print("</a></tf>");
		}
%>
	</tr>
<%
		for (int row=0;row<model.getRowCount();row++)
		{
			out.print("<tr>");
			for (int col=0;col<model.getColumnCount();col++)
			{
				out.print("<td class=\"content\"");
				Object value=model.getValueAt(row, col);
				if (value instanceof Number) out.print(" align=right");
				Video video=(Video)model.getRow(row).getUserObject();
				out.print("><a class=link href=\"video.jsp?id="+video.getId()+"\">");
				if (value!=null) out.print(value);
				out.print("</a></td>");
			}
			out.println("</tr>");
		}
%>
	</table>

	<p align=right><a class=link href="#top">Top</a></p>
</td></tr>
</table>

<!--Content End-->
</td>
</tr></table>
</div>

</body>
</html>
