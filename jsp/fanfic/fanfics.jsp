<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.net.URLEncoder,
                 com.kiwisoft.utils.*,
				 com.kiwisoft.media.MediaManagerApp,
				 com.kiwisoft.media.fanfic.*,
				 com.kiwisoft.media.ContactMedium,
				 com.kiwisoft.media.show.ShowManager,
				 com.kiwisoft.media.show.Show,
				 com.kiwisoft.utils.db.Chain,
				 java.util.*,
				 com.kiwisoft.media.Link,
				 com.kiwisoft.media.Language"%>

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
<title>Fanfics - <%=container.getName()%></title>
<script language="JavaScript" src="../../clipart/overlib.js"></script>
<script language="JavaScript" src="/nav.js"></script>
<script language="JavaScript" src="/fanfic/nav.js"></script>
<%
	if (show!=null)
	{
		%><jsp:include page="/shows/_show_nav.jsp"/><%
	}
%>
<link rel="StyleSheet" type="text/css" href="../../clipart/style.css">
</head>

<body>

<a name="top"></a>

<div class="logo">
	<table width=130 height=70 cellspacing=0 cellpadding=0><tr><td align=center>
	<jsp:include page="/shows/_show_logo.jsp"/>
	</td></tr></table>
</div>
<div class="title"><span style="font-weight:bold;font-size:24pt;">Fanfics</span></div>

<div id="overDiv" class="over_lib"></div>
<!--Navigation-->
<div class="nav_pos1"><a class=link_nav href="javascript:void(0)" onMouseOver="navMain(1,'../')" onMouseOut="nd()">Main</a></div>
<div class="nav_pos2"><a class=link_nav href="javascript:void(0)" onMouseOver="navFanfics(2)" onMouseOut="nd()">FanFics</a></div>
<%
	if (show!=null)
	{
%>
<div class="nav_pos3"><a class=link_nav href="javascript:void(0)" onMouseOver="navShow(3)" onMouseOut="nd()">Serie</a></div>
<%
	}
%>
<!--Navigation Ende-->

<div class="bg">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td class="bg_top">&nbsp;</td></tr>
<tr><td class=bg_middle valign=top>

<div class="bg_page">
<!--Body-->
<table cellspacing=0 width="100%">
<tr><td class=h1>&nbsp;&nbsp;&nbsp;&nbsp;<%=container.getName()%></td></tr>
</table>
<%
	if (container instanceof Author)
	{
		Author author=(Author)container;
		if (!author.getMail().isEmpty() || !author.getWeb().isEmpty())
		{
%>
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Author</td></tr>
</table>
<dl>
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
</dl>
<%
	}
	else if (container instanceof FanDom)
	{
		FanDom fanDom=(FanDom)container;
		if (fanDom.getLinkCount()>0)
		{
			%><br>
			<table cellspacing=0 width="100%">
			<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Links</td></tr>
			</table><%
			SortedSetMap sortedLinks=new SortedSetMap();
			for (Iterator itLinks=fanDom.getLinks().iterator(); itLinks.hasNext();)
			{
				Link link=(Link)itLinks.next();
				sortedLinks.add(link.getLanguage(), link);
			}
			for (Iterator itLanguages=sortedLinks.keySet().iterator(); itLanguages.hasNext();)
			{
				Language language=(Language)itLanguages.next();
				%><p><b><u><%=language.getName()%>e Webseiten</u></b></p>
				<ul><%
				for (Iterator itLinks=sortedLinks.get(language).iterator(); itLinks.hasNext();)
				{
					Link link=(Link)itLinks.next();
					%><li><b><%=link.getName()%></b><br>
					<a class="link" target="_new" href="<%=link.getUrl()%>"><%=link.getUrl()%></a></li><%
				}
				%></ul><%
			}
		}
	}
%>
<br>
<table cellspacing=0 width="100%">
<tr><td class=h2>&nbsp;&nbsp;&nbsp;&nbsp;Fanfics</td></tr>
</table>
<br>
<table>
	<tr><td><small>[
<%
    Iterator itChars=letters.iterator();
    while (itChars.hasNext())
    {
        Character character=(Character) itChars.next();
		%><a class=link href="fanfics.jsp?<%=container.getHttpParameter()%>&letter=<%=character%>"><%=character%></a> | <%
    }
	%><a class=link href="fanfics.jsp?<%=container.getHttpParameter()%>&letter=all">All</a> ] (<%=container.getFanFicCount()%> FanFics)</small></td></tr>
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
		%><tr><td valign=top><b><a name="<%=letter%>"><%=letter%></a></b></td><td valign=top width=600><ul> <%
    	Iterator itFanfics=fanFics.iterator();
    	while (itFanfics.hasNext())
    	{
			FanFic fanfic=(FanFic) itFanfics.next();
			%><li><a class="link" href="authors/<%=fanfic.getSource()%>">"<%=fanfic.getTitle()%>"</a> <%
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
				<a class="link" href="authors/<%=part.getSource()%>"><%=i%></a>
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
       	<small>Size: <%=(size>0 ? String.valueOf(size>>0xa)+"kB" : "Unknown")%></small><br>

<%
	}
%>
	</ul></td><td align=right valign=bottom><a class=link href="#top">Top</a></td></tr>
<%
	}
%>
</table>

<!--Body Ende-->
</div>

</td></tr>
<tr><td class="bg_bottom">&nbsp;</td></tr>
</table>
</div>

</body>
</html>
