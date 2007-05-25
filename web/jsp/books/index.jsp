<%@ page language="java" extends="com.kiwisoft.media.MediaJspBase" %>
<%@ page import="java.util.Iterator,
				 java.util.Set,
				 java.util.SortedSet,
				 java.util.TreeSet" %>
<%@ page import="com.kiwisoft.media.Navigation" %>
<%@ page import="com.kiwisoft.media.books.Book" %>
<%@ page import="com.kiwisoft.media.books.BookComparator" %>
<%@ page import="com.kiwisoft.media.books.BookManager" %>
<%@ page import="com.kiwisoft.web.JspUtils" %>

<%
	String letterString=request.getParameter("letter");
	SortedSet letters=BookManager.getInstance().getLetters();
	char selectedLetter=letterString!=null && letterString.length()==1 ? letterString.charAt(0) : ((Character)letters.first()).charValue();
	Set books=new TreeSet(new BookComparator());
	books.addAll(BookManager.getInstance().getBooksByLetter(selectedLetter));
%>
<html>

<head>
<title>Books</title>
<script language="JavaScript" src="/overlib.js"></script>
<link rel="StyleSheet" type="text/css" href="/style.css">
</head>

<body>
<a name="top"></a>
<div id="overDiv" class="over_lib"></div>

<div class="title">
<div style="margin-left:10px; margin-top:5px;">Books</div>
</div>

<div class="main">
<table cellspacing="0" cellpadding="5">
<tr valign="top">
	<td width="200">
		<!--Navigation Start-->

		<%--<jsp:include page="_nav.jsp"/>--%>
		<jsp:include page="/_nav.jsp"/>

		<!--Navigation End-->
	</td>
	<td width="800">
		<!--Content Start-->

		<table class="contenttable" width="790">
		<tr>
			<td class="header1">List</td>
		</tr>
		<tr>
			<td class="content">
				<table width="765">
				<tr>
					<td class="content2">
						<small>[
							<%
								for (Iterator it=letters.iterator(); it.hasNext();)
								{
									Character letter=(Character)it.next();
							%>
							<a class=link href="/books/index.jsp?letter=<%=letter%>"><%=letter%>
							</a>
							<%
									if (it.hasNext()) out.print("|");
								}
							%>
							] (<%=BookManager.getInstance().getBookCount() %> Books)
						</small>
					</td>
				</tr>
				</table>
				<br>
				<table width="765">
				<tr valign=top>
					<td class="content2" width="20"><b><a name="<%=selectedLetter%>"><%=selectedLetter%></a></b></td>
					<td class="content2" width=700>
						<ul>
<%
							for (Iterator itBooks=books.iterator(); itBooks.hasNext();)
							{
								Book book=(Book)itBooks.next();
%>
								<li><b><a class="link" href="<%=Navigation.getLink(book)%>"><%=JspUtils.render(book.getTitle())%></a></b>
									by <%=JspUtils.renderSet(book.getAuthors())%>
<%
							}
%>
						</ul>
					</td>
					<td class="content2" align=right valign=bottom><a class=link href="#top">Top</a></td>
				</tr>
				</table>
			</td>
		</tr>
		</table>

		<!--Content End-->
	</td>
</tr>
</table>
</div>

</body>
</html>
