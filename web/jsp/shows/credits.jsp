<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<s:if test="!mainCast.empty">
	<media:panel id="mainCast" title="Main Cast">
		<table class="table1">
			<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td class="tcell2">Role</td><td class="tcell2">German Voice</td></tr>
			<s:iterator value="mainCast" status="it">
				<tr class="<s:if test="#it.even">trow1</s:if><s:else>trow2</s:else>">
					<td class="tcell2">
						<s:set var="picture" value="findPicture()"/>
						<s:if test="#picture!=null && #picture.thumbnail50x50!=null">
							<media:thumbnail title="%{actor!=null ? actor.name : characterName}" image="#picture" thumbnail="#picture.thumbnail50x50"
											 attributes="vspace='5' hspace='5'"/>
						</s:if>
					</td>
					<td class="tcell2"><media:format value="actor"/></td>
					<td class="tcell2">... <media:format value="characterName" variant="preformatted"/>&nbsp;</td>
					<td class="tcell2"><media:format value="voice" variant="preformatted"/></td>
				</tr>
			</s:iterator>
		</table>
	</media:panel>
</s:if>

<s:if test="!recurringCast.empty">
	<media:panel id="recurringCast" title="Recurring Cast">
		<table class="table1">
			<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td class="tcell2">Role</td><td class="tcell2">German Voice</td></tr>
			<s:iterator value="recurringCast" status="it">
				<tr class="<s:if test="#it.even">trow1</s:if><s:else>trow2</s:else>">
					<td class="tcell2">
						<s:set var="picture" value="findPicture()"/>
						<s:if test="#picture!=null && #picture.thumbnail50x50!=null">
							<media:thumbnail title="%{actor!=null ? actor.name : characterName}" image="#picture" thumbnail="#picture.thumbnail50x50"
											 attributes="vspace='5' hspace='5'"/>
						</s:if>
					</td>
					<td class="tcell2"><media:format value="actor"/></td>
					<td class="tcell2">... <media:format value="characterName" variant="preformatted"/>&nbsp;</td>
					<td class="tcell2"><media:format value="voice" variant="preformatted"/></td>
				</tr>
			</s:iterator>
		</table>
	</media:panel>
</s:if>
