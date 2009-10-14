<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="menutable">
<tr><td class="menuheader">Main</td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="Home"/>">Home</a></td></tr>
<tr><td><hr size=1 color=black></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListBooks"/>">Books</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListFanDoms"/>">Fan Fiction</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="Links"/>">Links</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListMedia"/>">Media</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListMovies"/>">Movies</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListPhotoGalleries"/>">Photos</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="Schedule"/>">Schedule</a></td></tr>
<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListShows"/>">Shows</a></td></tr>
</table>

<form action="<s:url action="Search"/>" method="post">
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
