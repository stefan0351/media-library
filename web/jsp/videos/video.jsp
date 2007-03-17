<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.util.MissingResourceException,
				 java.util.ResourceBundle,
				 com.kiwisoft.media.Language,
				 com.kiwisoft.media.MediaTableConfiguration,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.media.video.Recording,
				 com.kiwisoft.media.video.RecordsWebTable,
				 com.kiwisoft.media.video.Video,
				 com.kiwisoft.media.video.VideoManager,
				 com.kiwisoft.utils.gui.table.TableConstants"%>
<%@ page import="com.kiwisoft.utils.gui.table.TableSortDescription"%>
<%@ page import="com.kiwisoft.media.Navigation"%>

<%
	String pId=request.getParameter("id");
	Video video=null;
	if (pId!=null) video=VideoManager.getInstance().getVideo(new Long(pId));
	RecordsWebTable model=new RecordsWebTable(video);
	ResourceBundle tableResources=ResourceBundle.getBundle(MediaTableConfiguration.class.getName());
	try
	{
		String pSort=request.getParameter("sort");
		if (pSort!=null)
		{
			int sort=Integer.parseInt(pSort);
			Integer sortDir="desc".equals(request.getParameter("dir")) ? TableConstants.DESCEND : TableConstants.ASCEND;
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
<title>Video<%=(video!=null ? " - "+video.getUserKey()+": "+video.getName() : "")%></title>
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

<jsp:include page="_videos_nav.jsp"/>
	<jsp:include page="/_nav.jsp"/>

<!--Navigation End-->
</td>
<td width="800">
<!--Content Start-->

<table class="contenttable" width="790">
<tr><td class="header1">Video<%=(video!=null ? " - "+video.getUserKey()+": "+video.getName() : "")%></td></tr>
<tr><td class="content">

<table class="table1" width="765">
<tr class="thead">
<%
	if (video!=null)
	{
		for (int i=0;i<model.getColumnCount();i++)
		{
			TableSortDescription sortDescription=model.getSortDescription(i);
			String sortDir;
			if (sortDescription!=null && TableConstants.ASCEND.equals(sortDescription.getDirection())) sortDir="desc";
			else sortDir="asc";
			out.print("<td class=\"tcell\"><a class=hiddenlink href=\"video.jsp?id="+video.getId()+"&sort="+i+"&dir="+sortDir+"\">");
			String columnName=model.getColumnName(i);
			try
			{
				columnName=tableResources.getString("table.recordings."+columnName);
			}
			catch (MissingResourceException e)
			{
			}
			out.print(columnName);
			if (sortDescription!=null)
			{
				if (TableConstants.ASCEND.equals(sortDescription.getDirection()))
					out.print("<img src=\"/clipart/ascend.gif\" border=0 hspace=3>");
				else
					out.print("<img src=\"/clipart/descend.gif\" border=0 hspace=3>");
			}
			out.print("</a></td>");
		}
%>
</tr>
<%
		for (int row=0;row<model.getRowCount();row++)
		{
			out.print("<tr class=\"");
			if (row%2==1) out.print("trow1");
			else out.print("trow2");
			out.print("\">");
			for (int col=0;col<model.getColumnCount();col++)
			{
				Object value=model.getValueAt(row, col);
				Recording recording=(Recording)model.getObject(row);
				out.print("<td class=\"tcell\"");
				if (value instanceof Number) out.print(" align=right");
				out.print(">");
				if (value instanceof Language)
				{
					Language language=(Language)value;
					out.print("<img src=\"/clipart/flag_"+language.getSymbol()+".gif\"> "+language.getName());
				}
				else if (value!=null)
				{
					if (col==1)
					{
						Episode episode=recording.getEpisode();
						if (episode!=null) out.print("<a class=\"link\" href=\""+Navigation.getLink(episode)+"\">"+value+"</a>");
						else out.print(value);
					}
					else out.print(value);
				}
				out.print("</td>");
			}
			out.println("</tr>");
		}
	}
%>
</table>

<p align=right><a class=link href="#top">Top</a></p>

</td></tr>
</table>

<!--Content End-->
</td></tr>

</table>
</div>

</body>
</html>
