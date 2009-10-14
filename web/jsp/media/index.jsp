<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="List">
	<table>
		<tr valign="top">
			<td class="content2" width="100" align="center">
				<div style="border: 1px solid black; background: url(<%=request.getContextPath()%>/style/images/trans10.png)">
					<small>
						<s:iterator value="groupCount.{#this}">
							<a class="link"
							   href="<s:url action="ListMedia"><s:param name="group" value="top"/></s:url>"><s:property value="getGroupName(top)"/></a><br/>
						</s:iterator>
					</small>
				</div>
			</td>
			<td><media:table model="mediaTable" alternateRows="true"/></td>
		</tr>
	</table>
</media:panel>

</body>
</html>
