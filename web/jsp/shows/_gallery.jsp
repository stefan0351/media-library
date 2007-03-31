<%@ page language="java" %>
<%@ page import="java.util.List,
				 java.util.ArrayList,
				 java.util.Collection,
				 java.util.Iterator,
				 com.kiwisoft.xp.XPBean"%>

<%
	int i=1;
	int column;
	List imageGroups=new ArrayList();
	XPBean xp=(XPBean)request.getAttribute("xp");
	Collection values=xp.getValues("imageGroup");
	if (values!=null) imageGroups.addAll(values);
	imageGroups.add(xp);
	Iterator itGroups=imageGroups.iterator();
	while (itGroups.hasNext())
	{
		XPBean imageGroup=(XPBean)itGroups.next();
		int columns=3;
		if (imageGroup.getValue("columns")!=null) columns=Integer.parseInt((String)imageGroup.getValue("columns"));
		if ("imageGroup".equals(imageGroup.getName()))
		{
			Object title=imageGroup.getValue("title");
			if (title==null) title="Image Gallery";
%>
			<table cellspacing=0 width="100%">
			<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;<%=title%></td></tr>
			</table><p>
<%
			Object text=imageGroup.getValue("text");
			if (text!=null) out.print(text);
%>
			</p>
<%
		}
		column=0;
		Collection images=imageGroup.getValues("image");
		if (images!=null)
		{
%>
<table cellspacing=5 cellpadding=5 align=center>
<%
			Iterator it=images.iterator();
			while (it.hasNext())
			{
				XPBean image=(XPBean)it.next();
				if (column==0) out.print("<tr valign=top>");
				Object text=image.getValue("text");
				if (text!=null) text=text.toString();
				int colspan=1;
				String span=(String)image.getValue("span");
				if (span!=null) colspan=Integer.parseInt(span);
				String size=(String)image.getValue("size");
				if (size!=null) colspan=Integer.parseInt(size);
%>
				<td align=center style="background:url(/clipart/trans10.png)" colspan="<%=colspan%>">
				<a name="image<%=i++%>" <%=image.getValue("source")!=null ? "href=\""+image.getValue("source")+"\"" : ""%>><img src="<%=image.getValue("preview")%>" border=0 hspace=5 vspace=5></a>
<%
				if (size==null)
				{
					column++;
					for (int im=1;im<colspan;im++)
					{
						image=(XPBean)it.next();
%>
						<a name="image<%=i++%>" <%=image.getValue("source")!=null ? "href=\""+image.getValue("source")+"\"" : ""%>><img src="<%=image.getValue("preview")%>" border=0 hspace=5 vspace=5></a>
<%
						column++;
					}
				}
				else column+=colspan;
%>
				<br/><%=(text!=null ? text : "")%></td>
<%
				if (column+1>columns)
				{
					out.print("</tr>");
					column=0;
				}
			}
%>
</table>
<%
		}
	}
%>
