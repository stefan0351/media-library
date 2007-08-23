package com.kiwisoft.media.tags;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Enumeration;
import java.net.URLEncoder;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

import com.kiwisoft.web.*;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.table.TableConstants;

/**
 * @author Stefan Stiller
 */
public class TableTag extends TagSupport
{
	private String model;
	private boolean alternateRows;
	private String width;

	@Override
	public int doStartTag() throws JspException
	{
		if (model!=null)
		{
			try
			{
				HttpServletRequest request=(HttpServletRequest)pageContext.getRequest();

				SortableWebTable table=(SortableWebTable)pageContext.getAttribute(model);
				if (table==null) table=(SortableWebTable)request.getAttribute(model);
				if (table==null) table=(SortableWebTable)pageContext.getSession().getAttribute(model);
				if (table!=null)
				{
					try
					{
						String pSort=request.getParameter("sort");
						if (pSort!=null)
						{
							int sort=Integer.parseInt(pSort);
							Integer sortDir="desc".equals(request.getParameter("dir")) ? TableConstants.DESCEND : TableConstants.ASCEND;
							table.setSortColumn(new TableSortDescription(sort, sortDir));
							table.sort();
						}
					}
					catch (NumberFormatException e)
					{
					}


					JspWriter out=pageContext.getOut();
					out.print("<table class=\"table1\"");
					if (width!=null) out.print(" width=\""+width+"\"");
					out.println(">");
					renderHeader(table);
					renderRows(table);
					out.println("</table>");
				}
			}
			catch (IOException e)
			{
				throw new JspException(e);
			}
		}
		return SKIP_BODY;
	}

	private void renderRows(SortableWebTable table) throws IOException
	{
		JspWriter out=pageContext.getOut();
		Map<String, Object> context=new HashMap<String, Object>();
		for (int rowIndex=0;rowIndex<table.getRowCount();rowIndex++)
		{
			out.print("<tr class=\"");
			if (alternateRows && rowIndex%2==1) out.print("trow1");
			else out.print("trow2");
			out.print("\">");
			for (int columnIndex=0;columnIndex<table.getColumnCount();columnIndex++)
			{
				context.clear();
				Object value=table.getValueAt(rowIndex, columnIndex);
				Class cellClass=table.getCellClass(rowIndex, columnIndex);
				if (cellClass==null)
				{
					if (value!=null) cellClass=value.getClass();
					else cellClass=Object.class;
				}
				String rendererVariant=table.getRendererVariant(rowIndex, columnIndex);
				String cellStyle=table.getCellStyle(rowIndex, columnIndex);

				HTMLRenderer renderer=HTMLRendererManager.getInstance().getRenderer(cellClass, rendererVariant);
				out.print("<td class=\"tcell\"");
				if (cellStyle!=null) out.print(" style=\""+cellStyle+"\"");				
				String attributes=renderer.getAttributes(value, rowIndex, columnIndex);
				if (attributes!=null) out.print(attributes);
				out.print(">");
				String content=renderer.getContent(value, context, rowIndex, columnIndex);
				if (StringUtils.isEmpty(content)) content="&nbsp;";
				out.print(content);
				out.print("</td>");
			}
			out.println("</tr>");
		}
	}

	private void renderHeader(SortableWebTable table) throws IOException
	{
		JspWriter out=pageContext.getOut();
		HttpServletRequest request=(HttpServletRequest)pageContext.getRequest();
		out.println("<tr class=\"thead\">");
		StringBuilder queryString=null;
		if (table.isResortable())
		{
			queryString=new StringBuilder();
			Enumeration parameters=request.getParameterNames();
			while (parameters.hasMoreElements())
			{
				String parameter=(String)parameters.nextElement();
				if (!"sort".equals(parameter) && !"dir".equals(parameter))
				{
					queryString.append(parameter);
					queryString.append("=");
					queryString.append(URLEncoder.encode(request.getParameter(parameter), "UTF-8"));
					queryString.append("&");
				}
			}
		}
		for (int column=0;column<table.getColumnCount();column++)
		{
			TableSortDescription sortDescription=table.getSortDescription(column);
			String sortDir;
			if (sortDescription!=null && TableConstants.ASCEND.equals(sortDescription.getDirection())) sortDir="desc";
			else sortDir="asc";
			out.print("<td class=\"tcell\">");
			if (table.isResortable())
			{
				out.print("<a class=hiddenlink href=\""+request.getServletPath()+"?");
				if (queryString!=null && queryString.length()>0) out.print(queryString);
				out.print("sort="+column+"&dir="+sortDir+"\">");
			}
			out.print(JspUtils.prepareString(table.getColumnName(column)));
			if (sortDescription!=null)
			{
				if (TableConstants.ASCEND.equals(sortDescription.getDirection()))
					out.print("<img src=\"/clipart/ascend.gif\" border=0 hspace=3>");
				else
					out.print("<img src=\"/clipart/descend.gif\" border=0 hspace=3>");
			}
			if (table.isResortable()) out.print("</a>");
			out.print("</td>");
		}
		out.println("</tr>");
	}

	public void setModel(String model)
	{
		this.model=model;
	}

	public void setAlternateRows(boolean alternateRows)
	{
		this.alternateRows=alternateRows;
	}

	public void setWidth(String width)
	{
		this.width=width;
	}

	@Override
	public void release()
	{
		width=null;
		alternateRows=false;
		model=null;
		super.release();
	}
}
