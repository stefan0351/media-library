<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="com.kiwisoft.utils.*,
				 com.kiwisoft.media.fanfic.*,
				 com.kiwisoft.media.ContactMedium,
				 com.kiwisoft.media.show.ShowManager,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.collection.Chain,
				 java.util.*,
				 com.kiwisoft.media.Link,
				 com.kiwisoft.media.Language"%>
<%@ page import="com.kiwisoft.collection.SortedSetMap" %>
<%@ page import="com.kiwisoft.media.LinkGroup" %>

<%
    FanFicGroup container=null;
	Show show=null;
    if (request.getParameterMap().containsKey("fandom"))
    {
        FanDom fanDom=FanFicManager.getInstance().getDomain(new Long(request.getParameter("fandom")));
		container=fanDom;
		show=fanDom.getShow();
    }
    else if (request.getParameterMap().containsKey("pairing"))
    {
		String parameter=request.getParameter("pairing");
		Long id=null;
		try
		{
			id=new Long(parameter);
		}
		catch (NumberFormatException e)
		{
			if ("Buffy+Willow".equals(parameter)) id=new Long(48745);
			else e.printStackTrace();
		}
		container=FanFicManager.getInstance().getPairing(id);
    }
    else if (request.getParameterMap().containsKey("author"))
    {
		try
		{
			Long id=new Long(request.getParameter("author"));
			container=FanFicManager.getInstance().getAuthor(id);
		}
		catch (NumberFormatException e)
		{
			container=FanFicManager.getInstance().getAuthor(request.getParameter("author"));
		}
    }
	else if (request.getParameterMap().containsKey("show"))
	{
		show=ShowManager.getInstance().getShow(new Long(request.getParameter("show")));
		container=show;
	}
	request.setAttribute("show", show);

	Set letters;
	if (container!=null) letters=container.getFanFicLetters();
	else letters=Collections.EMPTY_SET;

    String param=request.getParameter("letter");
	Set lettersVisible;
	if ("all".equals(param)) lettersVisible=letters;
	else if (!StringUtils.isEmpty(param)) lettersVisible=Collections.singleton(new Character(param.charAt(0)));
	else if (!letters.isEmpty()) lettersVisible=Collections.singleton(letters.iterator().next());
	else lettersVisible=Collections.singleton(new Character('A'));
%>
<html>

<head>
<title>Fan Fiction - <%=container.getFanFicGroupName()%></title>
<script language="JavaScript" src="../overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
	<div style="margin-left:10px; margin-top:5px;">Fan Fiction</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5"><tr valign="top">
	<td width="200">
		<%
	if (show!=null)
	{
%>
		<jsp:include page="/shows/_show_nav.jsp"/>
<%
	}
%>
		<jsp:include page="_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</td>
	<td width="800">
		<table class="contenttable" width="790">
		<tr><td class="header1"><%=container.getFanFicGroupName()%></td></tr>
		<tr><td class="content">
<%
	if (container instanceof Author)
	{
		Author author=(Author)container;
		if (!author.getMail().isEmpty() || !author.getWeb().isEmpty())
		{
%>
			<table class="contenttable" width="765">
			<tr><td class=header2>Author</td></tr>
			<tr><td class="content"><dl>
<%
			if (!author.getMail().isEmpty())
			{
%>
				<dt><b>EMail:</b>
<%
				Iterator itMail=author.getMail().iterator();
				while (itMail.hasNext())
				{
					ContactMedium link=(ContactMedium)itMail.next();
%>
					<dd><a class="link" href="mailto:<%=link.getValue()%>"><%=link.getValue()%></a>
<%
				}
			}
			if (!author.getWeb().isEmpty())
			{
%>
				<dt><b>Web:</b>
<%
				Iterator itWeb=author.getWeb().iterator();
				while (itWeb.hasNext())
				{
					ContactMedium link=(ContactMedium)itWeb.next();
%>
					<dd><a class="link" href="<%=link.getValue()%>"><%=link.getValue()%></a>
<%
				}
			}
		}
%>
			</dl></td></tr>
			</table>
<%
}
else if (container instanceof FanDom)
{
	FanDom fanDom=(FanDom)container;
	LinkGroup linkGroup=fanDom.getLinkGroup();
	if (linkGroup!=null && linkGroup.getLinkCount()>0)
	{
%>
			<table class="contenttable" width="765">
			<tr><td class=header2>Links</td></tr>
			<tr><td class="content">
<%
	com.kiwisoft.collection.SortedSetMap sortedLinks=new SortedSetMap();
	for (Iterator itLinks=linkGroup.getLinks().iterator(); itLinks.hasNext();)
	{
		Link link=(Link)itLinks.next();
		sortedLinks.add(link.getLanguage(), link);
	}
	for (Iterator itLanguages=sortedLinks.keySet().iterator(); itLanguages.hasNext();)
	{
		Language language=(Language)itLanguages.next();
%>
				<p><b><u><%=language.getName()%>e Webseiten</u></b></p>
				<ul>
<%
				for (Iterator itLinks=sortedLinks.get(language).iterator(); itLinks.hasNext();)
				{
					Link link=(Link)itLinks.next();
					%><li><b><%=link.getName()%></b><br>
					<a class="link" target="_new" href="<%=link.getUrl()%>"><%=link.getUrl()%></a></li><%
				}
%>
				</ul>
<%
			}
%>
			</td></tr>
			</table>
<%
		}
	}
%>

			<table class="contenttable" width="765">
			<tr><td class=header2>FanFics</td></tr>
			<tr><td class="content">
				<table>
				<tr><td class="content2"><small>[
<%
    Iterator itChars=letters.iterator();
    while (itChars.hasNext())
    {
        Character character=(Character) itChars.next();
		%><a class=link href="fanfics.jsp?<%=container.getHttpParameter()%>&letter=<%=character%>"><%=character%></a> | <%
    }
	%><a class=link href="fanfics.jsp?<%=container.getHttpParameter()%>&letter=all">All</a> ] (<%=container.getFanFicCount()%> FanFics)
				</small></td></tr>
				</table>
				<br>
				<table>
<%
	for (Iterator itLetters=lettersVisible.iterator(); itLetters.hasNext();)
	{
		Character letter=(Character)itLetters.next();
		List fanFics=new LinkedList();
		if (container!=null) fanFics.addAll(container.getFanFics(letter.charValue()));
		Collections.sort(fanFics, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				String title1=StringUtils.buildSortTitle(((FanFic)o1).getTitle());
				String title2=StringUtils.buildSortTitle(((FanFic)o2).getTitle());
				return title1.compareToIgnoreCase(title2);
			}
		});
%>
				<tr valign="top"><td class="content2"><b><a name="<%=letter%>"><%=letter%></a></b></td><td class="content2" width=600><ul>
<%
    	Iterator itFanfics=fanFics.iterator();
    	while (itFanfics.hasNext())
    	{
			FanFic fanfic=(FanFic) itFanfics.next();
%>
					<li><a class="link" href="<%=request.getContextPath()%>/resource?file=fanfic/authors/<%=fanfic.getSource()%>">"<%=fanfic.getTitle()%>"</a>
<%
			if (!fanfic.isFinished()) out.print(" (unfinished)");
						%> by <%
			Iterator itAuthors=fanfic.getAuthors().iterator();
			while (itAuthors.hasNext())
			{
				Author author=(Author)itAuthors.next();
%>
						<a class=link href="fanfics.jsp?author=<%=author.getId()%>"><%=author.getName()%></a>
 <%
				if (itAuthors.hasNext()) out.print(",");
			}
			out.print("<br>");
			Chain parts=fanfic.getParts();
			if (parts.size()>1)
			{
%>
						<small>Parts:
<%
				int i=1;
				for (Iterator itParts=parts.iterator(); itParts.hasNext();)
				{
					FanFicPart part=(FanFicPart)itParts.next();
%>
							<a class="link" href="<%=request.getContextPath()%>/resource?file=fanfic/authors/<%=part.getSource()%>"><%=i%></a>
<%
					i++;
					if (itParts.hasNext()) out.print(" | ");
				}
%>
						</small><br>
<%
			}
        	if (!fanfic.getFanDoms().isEmpty())
        	{
        		out.print("<small>FanDoms: ");
            	Iterator itFanDoms=fanfic.getFanDoms().iterator();
            while (itFanDoms.hasNext())
            {
				FanDom fanDom=(FanDom)itFanDoms.next();
%>
                <a class=link href="fanfics.jsp?fandom=<%=fanDom.getId()%>"><%=fanDom.getName()%></a>
<%
                if (itFanDoms.hasNext()) out.print(", ");
          	}
            out.println("</small><br>");
		}
        if (!fanfic.getPairings().isEmpty())
        {
			out.print("<small>Pairings: ");
			Iterator itPairs=fanfic.getPairings().iterator();
			while (itPairs.hasNext())
			{
				Pairing pairing=(Pairing)itPairs.next();
%>
                <a class=link href="fanfics.jsp?pairing=<%=pairing.getId()%>"><%=pairing.getName()%></a>
<%
                if (itPairs.hasNext()) out.print(", ");
			}
			out.println("</small><br>");
		}
        if (!StringUtils.isEmpty(fanfic.getDescription()))
        {
%>
        	<small>Summary: <%=fanfic.getDescription()%></small><br>
<%
       	}
        FanFic prequel=fanfic.getPrequel();
        if (prequel!=null)
        {
%>
        	<small>Sequel to: <a class="link" href="authors/<%=prequel.getSource()%>">"<%=prequel.getTitle()%>"</a></small><br>
<%
      	}
		long size=0;
		for (Iterator itParts=parts.iterator(); itParts.hasNext() && size>=0;)
		{
			FanFicPart part=(FanFicPart)itParts.next();
			long partSize=part.getSize();
			if (partSize>=0) size+=partSize;
			else size=-1;
		}
%>
       	<small>Size: <%=(size>0 ? String.valueOf(size>>0xa)+"kB" : "Unknown")%></small></li><br>

<%
	}
%>
				</ul></td><td class="content2" valign="bottom" align="right"><a class=link href="#top">Top</a></td></tr>
<%
	}
%>
				</table>
		</td></tr>
		</table>
	</td>
</tr></table>

</div>

</body>
</html>
