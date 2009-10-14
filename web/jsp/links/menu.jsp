<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<s:if test="show!=null">
	<table class="menutable">
		<tr><td class="menuheader">Show</td></tr>
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
