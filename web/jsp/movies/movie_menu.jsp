<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<table class="menutable">
	<tr><td class="menuheader">Movie</td></tr>
	<s:if test="thumbnail!=null">
		<tr><td class="menuitem" align="center"><media:thumbnail title="Poster" image="poster"
																 thumbnail="thumbnail"/></td>
		</tr>
		<tr>
			<td>
				<hr size=1 color=black>
			</td>
		</tr>
	</s:if>
	<s:if test="!summaries.empty">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="MovieDetails" anchor="summary"><s:param name="movieId" value="movie.id"/></s:url>">Summary</a>
		</td></tr>

	</s:if>
	<tr><td class="menuitem"><a class="menulink"
								href="<s:url action="MovieDetails" anchor="details"><s:param name="movieId" value="movie.id"/></s:url>">Details</a>
	</td></tr>
	<s:if test="!cast.empty">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="MovieDetails" anchor="cast"><s:param name="movieId" value="movie.id"/></s:url>">Cast</a>
		</td></tr>
	</s:if>
	<s:if test="!crew.empty">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="MovieDetails" anchor="crew"><s:param name="movieId" value="movie.id"/></s:url>">Crew</a>
		</td></tr>
	</s:if>
	<s:if test="movie.show!=null">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="ShowDetails"><s:param name="showId" value="movie.show.id"/></s:url>">TV
			Show</a></td></tr>
	</s:if>
</table>
