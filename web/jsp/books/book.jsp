<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Collection,
				 com.kiwisoft.media.books.Book" %>
<%@ page import="com.kiwisoft.media.books.BookManager" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>
<%@ page import="com.kiwisoft.media.Language" %>
<%@ page import="com.kiwisoft.utils.StringUtils" %>
<%@ page import="com.kiwisoft.media.LanguageManager" %>

<%@ taglib prefix="media" uri="http://www.kiwisoft.de/media" %>

<%
	Book book=BookManager.getInstance().getBook(new Long(request.getParameter("book")));
	request.setAttribute("book", book);
%>
<html>

<head>
<title>Book - <%=JspUtils.render(request, book.getTitle())%>
</title>
<script language="JavaScript" src="../overlib.js"></script>
<script language="JavaScript" src="../popup.js"></script>
<link rel="StyleSheet" type="text/css" href="../style.css">
</head>

<body>
<div id="overDiv" class="over_lib"></div>
<a name="top"></a>

<media:title><%=JspUtils.render(request, book.getTitle())%>
</media:title>
<media:body>
	<media:sidebar>
		<jsp:include page="_book_nav.jsp"/>
		<jsp:include page="../_nav.jsp"/>
	</media:sidebar>
	<media:content>
<%
    String germanSummary=book.getSummaryText(LanguageManager.GERMAN);
    String englishSummary=book.getSummaryText(LanguageManager.ENGLISH);
    if (!StringUtils.isEmpty(germanSummary) || !StringUtils.isEmpty(englishSummary))
    {
%>
        <media:panel title="Summary">
<%
        if (!StringUtils.isEmpty(englishSummary))
        {
%>
            <p><media:render value="<%=LanguageManager.GERMAN%>" variant="icon only"/>
                <media:render value="<%=germanSummary%>" variant="preformatted"/></p>
<%
        }
        if (!StringUtils.isEmpty(germanSummary))
        {
            if (!StringUtils.isEmpty(englishSummary)) out.println("<hr size=\"1\" color=\"black\">");
%>
            <p><media:render value="<%=LanguageManager.GERMAN%>" variant="icon only"/>
                <media:render value="<%=germanSummary%>" variant="preformatted"/></p>
<%
        }
%>
        </media:panel>
<%
    }
%>
        <media:panel title="Details">
			<dl>
<%
				Collection authors=book.getAuthors();
				if (!authors.isEmpty())
				{
%>
					<dt><b>Author:</b> <dd><%=JspUtils.renderSet(request, authors)%></dd>
<%
				}
				Collection translators=book.getTranslators();
				if (!translators.isEmpty())
				{
%>
					<dt><b>Translated by:</b> <dd><%=JspUtils.renderSet(request, translators)%></dd>
<%
				}
				Language language=book.getLanguage();
				if (language!=null)
				{
%>
					<dt><b>Language:</b> <dd><%=JspUtils.render(request, language)%></dd>
<%
				}
				String publisher=book.getPublisher();
				if (!StringUtils.isEmpty(publisher))
				{
%>
					<dt><b>Published by:</b> <dd><%=JspUtils.render(request, publisher)%></dd>
<%
				}
				Integer publishedYear=book.getPublishedYear();
				String edition=book.getEdition();
				if (publishedYear!=null || !StringUtils.isEmpty(edition))
				{
%>
					<dt><b>Edition:</b>
						<dd><%=JspUtils.render(request, edition)%> <%=JspUtils.render(request, publishedYear)%></dd>
<%
				}
				String binding=book.getBinding();
				if (!StringUtils.isEmpty(binding))
				{
%>
					<dt><b>Binding:</b> <dd><%=JspUtils.render(request, binding)%></dd>
<%
				}
				Integer pageCount=book.getPageCount();
				if (pageCount!=null)
				{
%>
					<dt><b>Pages:</b> <dd><%=JspUtils.render(request, pageCount)%></dd>
<%
				}
				String isbn=book.getIsbn13();
				if (StringUtils.isEmpty(isbn)) isbn=book.getIsbn10();
				if (!StringUtils.isEmpty(isbn))
				{
%>
					<dt><b>ISBN:</b> <dd><%=JspUtils.render(request, isbn)%></dd>
<%
				}
%>
			</dl>
		</media:panel>
	</media:content>
</media:body>

</body>
</html>
