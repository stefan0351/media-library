<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<table class="menutable">
<tr>
	<td class="menuheader">Main</td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/books/index.jsp">Books</a></td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/fanfic/fandoms.jsp">Fan Fiction</a></td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/media/index.jsp">Media</a></td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/movies/index.jsp">Movies</a></td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/photos/index.jsp">Photos</a></td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/schedule.jsp">Schedule</a></td>
</tr>
<tr>
	<td class="menuitem"><a class="menulink" href="/shows/index.jsp">Shows</a></td>
</tr>
</table>

<form action="/search" method="post">
	<table class="menutable">
	<tr>
		<td class="menuheader">Search</td>
	</tr>
	<tr>
		<td align="center">
			<select name="type" style="margin-top:10px; margin-bottom:5px; width:180px">
				<option value="all" selected>All</option>
				<option value="shows">Shows</option>
				<option value="episodes">Episodes</option>
				<option value="movies">Movies</option>
				<option value="persons">Persons</option>
			</select>
			<input type="text" name="text" style="margin-bottom:10px; height:20px; width:180px;"/>
		</td>
	</tr>
	</table>
</form>