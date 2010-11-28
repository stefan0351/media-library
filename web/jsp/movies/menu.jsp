<%@ page language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="menutable">
<tr>
	<td class="menuheader">Movies</td>
</tr>

<tr>
	<td class="menuitem"><a class="menulink" href="<s:url action="ListMovies"/>">Index</a></td>
</tr>

<tr>
	<td>
		<hr size=1 color=black>
	</td>
</tr>

<tr>
	<td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/create.pdf?template=/media/mediaByMovie.fo.vsl">
		<img src="<%=request.getContextPath()%>/icons/printer.png" border="0"> DVD's List</a></td>
</tr>
</table>
