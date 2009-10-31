<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Home">
	<table width="100%">
		<tr style="vertical-align:top">
			<td>
				<table class="contenttable" width="100%">
					<tr><td class="header2">Recently Visited</td></tr>
					<tr>
						<td class="content">
							<ul>
								<s:iterator value="recentItems">
									<li><media:format variant="Full"/></li>
								</s:iterator>
							</ul>
						</td>
					</tr>
				</table>
			</td>
			<td>
				<table class="contenttable" width="100%">
					<tr><td class="header2">Next on TV</td></tr>
					<tr>
						<td class="content">
							<table>
								<s:iterator value="airdates" status="it">
									<tr valign="top" <s:if test="#it.even">class="trow1"</s:if><s:else>class="trow2"</s:else>>
										<td align="right"><s:date name="date" format="HH:mm"/></td>
										<td><media:format value="channel"/></td>
										<td><media:format/></td>
										<td>
											<s:if test="!detailsLink.empty">
												<img src="<%=request.getContextPath()%>/file/?type=Icon&name=details" alt="Details"
														onClick="newWindow('Details', '<s:property value="detailsLink"/>', 500, 500);" border="0"/>
											</s:if>
										</td>
									</tr>
								</s:iterator>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</media:panel>