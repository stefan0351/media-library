<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<s:if test="show!=null">
	<table class="menutable">
		<tr><td class="menuheader">FanDom</td></tr>
		<s:if test="show.logo!=null">
			<s:set var="thumbnail" value="show.logo.findSidebarThumbnail()"/>
			<s:if test="#thumbnail!=null">
				<tr><td class="menuitem" align="center"><media:thumbnail title="Logo" image="show.logo" thumbnail="#thumbnail"/></td></tr>
				<tr>
					<td>
						<hr size=1 color=black>
					</td>
				</tr>
			</s:if>
		</s:if>
		<tr><td class="menuitem"><a class="menulink" href="<s:url action="ShowDetails"><s:param name="showId" value="show.id"/></s:url>">Show
			Details</a></td></tr>
	</table>
</s:if>
<s:if test="movie!=null">
	<table class="menutable">
		<tr><td class="menuheader">FanDom</td></tr>
		<s:if test="movie.poster!=null">
			<s:set var="thumbnail" value="movie.poster.findSidebarThumbnail()"/>
			<s:if test="#thumbnail!=null">
				<tr><td class="menuitem" align="center"><media:thumbnail title="Poster" image="movie.poster" thumbnail="#thumbnail"/></td></tr>
				<tr>
					<td>
						<hr size=1 color=black>
					</td>
				</tr>
			</s:if>
		</s:if>
		<tr><td class="menuitem"><a class="menulink" href="<s:url action="MovieDetails"><s:param name="movieId" value="movie.id"/></s:url>">Movie
			Details</a></td></tr>
	</table>
</s:if>

<table class="menutable">
	<tr><td class="menuheader">Fan Fiction</td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListFanDoms"/>">Domains</a></td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListFanFicPairs"/>">Pairings</a></td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListFanFicAuthors"/>">Authors</a></td></tr>
</table>
