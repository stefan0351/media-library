<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="menutable">
	<tr><td class="menuheader">Episode</td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<media:url value="episode"/>">Summary</a></td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<media:url value="episode"/>#production">Production</a></td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<media:url value="episode"/>#castAndCrew">Cast and Crew</a></td></tr>
	<s:if test="episode.hasImages()">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="EpisodeMediaFiles"><s:param name="episodeId" value="episode.id"/><s:param name="typeId" value="@com.kiwisoft.media.files.MediaType@IMAGE.id"/></s:url>">Images</a>
		</td></tr>
	</s:if>
	<s:if test="episode.hasVideos()">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="EpisodeMediaFiles"><s:param name="episodeId" value="episode.id"/><s:param name="typeId" value="@com.kiwisoft.media.files.MediaType@VIDEO.id"/></s:url>">Videos</a>
		</td></tr>
	</s:if>
	<s:set var="previous" value="episode.previousEpisode"/>
	<s:set var="next" value="episode.nextEpisode"/>
	<s:if test="#previous!=null || #next!=null">
		<tr>
			<td>
				<hr size=1 color=black>
			</td>
		</tr>
		<s:if test="#previous!=null">
			<tr><td class="menuitem"><a class="menulink" href="<s:url includeParams="get"><s:param name="episodeId" value="#previous.id"/></s:url>">Previous Episode</a></td></tr>
		</s:if>
		<s:if test="#next!=null">
			<tr><td class="menuitem"><a class="menulink" href="<s:url includeParams="get"><s:param name="episodeId" value="#next.id"/></s:url>">Next Episode</a></td></tr>
		</s:if>
	</s:if>
</table>
