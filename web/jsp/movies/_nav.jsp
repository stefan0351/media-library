<%@ page language="java" %>

<table class="menutable">
<tr>
	<td class="menuheader">Movies</td>
</tr>

<tr>
	<td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/movies/index.jsp">Index</a></td>
</tr>

<tr>
	<td>
		<hr size=1 color=black>
	</td>
</tr>

<tr>
	<td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/create.pdf?xml.source=com.kiwisoft.media.medium.MediaByMovieXML&xsl=/media/mediaByMovie.xsl">
		<img src="<%=request.getContextPath()%>/icons/printer.png" border="0"> DVD's List</a></td>
</tr>
</table>
