<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<table class="menutable">
	<tr><td class="menuheader">Show</td></tr>
	<s:set var="logo" value="findLogo()"/>
	<s:if test="#logo!=null">
		<s:set var="thumbnail" value="#logo.findSidebarThumbnail()"/>
		<s:if test="#thumbnail!=null">
			<tr><td class="menuitem" align="center"><media:thumbnail title="Logo" thumbnail="#thumbnail" image="#logo"/></td></tr>
			<tr>
				<td>
					<hr size=1 color=black>
				</td>
			</tr>
		</s:if>
	</s:if>

	<s:if test="seasons.empty">
		<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListEpisodes"><s:param name="showId" value="show.id"/></s:url>">Episodes</a></td></tr>
	</s:if>
	<s:else>
		<s:iterator value="seasons">
			<tr><td class="menuitem"><media:format value="top" variant="Menu"/></td></tr>
		</s:iterator>
	</s:else>
	<s:if test="!show.movies.empty">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="ListEpisodes" anchor="movies"><s:param name="showId" value="show.id"/></s:url>">Movies</a></td></tr>
	</s:if>

	<tr>
		<td>
			<hr size=1 color=black>
		</td>
	</tr>

	<s:if test="!show.getCastMembers(@com.kiwisoft.media.person.CreditType@MAIN_CAST).empty || !show.getCastMembers(@com.kiwisoft.media.person.CreditType@RECURRING_CAST).empty">
		<tr><td class="menuitem"><a class="menulink" href="<s:url action="ShowCredits"><s:param name="showId" value="show.id"/></s:url>">Cast and Crew</a></td></tr>
	</s:if>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="ShowTracks"><s:param name="showId" value="show.id"/></s:url>">Media</a></td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="ShowSchedule"><s:param name="showId" value="show.id"/></s:url>">Schedule</a></td></tr>
	<s:if test="show.hasImages()">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="ShowMediaFiles"><s:param name="showId" value="show.id"/>
									<s:param name="typeId" value="@com.kiwisoft.media.files.MediaType@IMAGE.id"/></s:url>">Images</a>
		</td></tr>
	</s:if>
	<s:if test="show.hasVideos()">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="ShowMediaFiles"><s:param name="showId" value="show.id"/>
									<s:param name="typeId" value="@com.kiwisoft.media.files.MediaType@VIDEO.id"/></s:url>">Videos</a>
		</td></tr>
	</s:if>
	<s:if test="show.linkGroup!=null && show.linkGroup.linkCount>0">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="Links"><s:param name="showId" value="show.id"/><s:param name="groupId" value="show.linkGroup.id"/></s:url>">Links</a>
		</td></tr>
	</s:if>
	<s:if test="show.fanFicCount>0">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="ListFanFics"><s:param name="showId" value="show.id"/></s:url>">Fan Fiction</a>
		</td></tr>
	</s:if>

	<s:if test="!show.infos.empty">
		<tr>
			<td>
				<hr size=1 color=black>
			</td>
		</tr>
		<s:iterator value="show.infos">
			<tr><td class="menuitem"><a class="menulink"
										href="<s:url action="ShowInfo"><s:param name="infoId" value="id"/></s:url>"><s:property value="name"/></a>
			</td></tr>
		</s:iterator>
	</s:if>
</table>
