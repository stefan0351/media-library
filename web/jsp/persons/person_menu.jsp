<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<table class="menutable">
	<tr><td class="menuheader">Person</td></tr>
	<s:if test="thumbnail!=null">
		<tr><td class="menuitem" align="center"><media:thumbnail title="Photo" thumbnail="thumbnail" image="picture"/></td></tr>
		<tr>
			<td>
				<hr size=1 color=black>
			</td>
		</tr>
	</s:if>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="PersonDetails" anchor="details"><s:param name="personId" value="person.id"/></s:url> ">Details</a>
	</td></tr>
	<tr><td class="menuitem"><a class="menulink"
								href="<s:url action="PersonDetails" anchor="filmography"><s:param name="personId" value="person.id"/></s:url> ">Filmography</a>
	</td></tr>
	<tr><td class="menuitem"><a class="menulink"
								href="<s:url action="PersonDetails" anchor="books"><s:param name="personId" value="person.id"/></s:url> ">Books</a>
	</td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="PersonSchedule"><s:param name="personId" value="person.id"/></s:url>">Schedule</a>
	</td></tr>
	<s:if test="hasImages()">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="PersonMediaFiles"><s:param name="personId" value="person.id"/>
								<s:param name="typeId" value="@com.kiwisoft.media.files.MediaType@IMAGE.id"/></s:url>">Images</a>
		</td></tr>
	</s:if>
	<s:if test="hasVideos()">
		<tr><td class="menuitem"><a class="menulink"
									href="<s:url action="PersonMediaFiles"><s:param name="personId" value="person.id"/>
								<s:param name="typeId" value="@com.kiwisoft.media.files.MediaType@VIDEO.id"/></s:url>">Videos</a>
		</td></tr>
	</s:if>
</table>
