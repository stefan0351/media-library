<%@ page language="java"  extends="com.kiwisoft.media.MediaJspBase"%>
<%@ page import="java.net.URLEncoder,
                 com.kiwisoft.utils.AlphabeticalMap,
                 com.kiwisoft.utils.Configurator,
                 java.util.*,
				 com.kiwisoft.utils.gui.table.SortableTableModel,
				 com.kiwisoft.utils.db.DBLoader,
				 com.kiwisoft.media.video.Video,
				 com.kiwisoft.media.ui.video.RecordsTableModel,
				 com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.Language,
				 com.kiwisoft.utils.gui.table.TableSortDescription,
				 com.kiwisoft.utils.gui.table.TableConfiguration,
				 com.kiwisoft.media.ui.MediaManagerFrame,
				 com.kiwisoft.media.video.Recording,
				 com.kiwisoft.media.show.Episode,
				 com.kiwisoft.media.video.VideoManager"%>

<%
	String pId=request.getParameter("id");
	Video video=null;
	if (pId!=null) video=VideoManager.getInstance().getVideo(new Long(pId));
	RecordsTableModel model=new RecordsTableModel(video);
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
<title>Video<%=(video!=null ? " - "+video.getUserKey()+": "+video.getName() : "")%></title>
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
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;Video<%=(video!=null ? " - "+video.getUserKey()+": "+video.getName() : "")%></td></tr>
</table>
<br>
<table bordercolor=black border=1 cellspacing=0 width="690">
<tr bgcolor="#eeeeee">
<%
	if (video!=null)
	{
		for (int i=0;i<model.getColumnCount();i++)
		{
			TableSortDescription sortDescription=model.getSortDescription(i);
			String sortDir;
			if (sortDescription!=null && SortableTableModel.ASCEND.equals(sortDescription.getDirection())) sortDir="desc";
			else sortDir="asc";
			out.print("<th><a class=link_nav href=\"video.jsp?id="+video.getId()+"&sort="+i+"&dir="+sortDir+"\">");
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
				Object value=model.getValueAt(row, col);
				Recording recording=(Recording)model.getObject(row);
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
					if (col==1)
					{
						Episode episode=recording.getEpisode();
						if (episode!=null)
						{
							String link=episode.getLink();
							if (link!=null) out.print("<a class=\"link\" href=\""+link+"\">"+value+"</a>");
							else out.print(value);
						}
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

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
