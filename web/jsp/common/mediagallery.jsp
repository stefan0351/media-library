<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{galleryName}">
	<s:iterator value="mediaFileGroups">
		<table class="contenttable" width="765">
			<tr><td class="header2"><s:property/></td></tr>
			<tr>
				<td class="content">
					<table cellspacing="0">
						<s:set var="rows" value="@com.kiwisoft.util.Utils@splitIntoRows(getMediaFiles(top), 4)"/>
						<s:iterator value="#rows" status="itRows">
							<tr>
								<s:iterator status="it">
									<td style="width:170px; height:130px; text-align:center; vertical-align:middle; background:url(<%=request.getContextPath()%>/style/images/trans10.png);">
										<a target="_blank" href="<media:url value="top"/>"><media:image image="findThumbnail()"/></a>
									</td>
									<s:if test="!#it.last"><td width="10"></td></s:if>
								</s:iterator>
							</tr>
							<tr>
								<s:iterator status="it">
									<td style="width:170px; font-size:8pt; text-align:center; vertical-align:top; background:url(<%=request.getContextPath()%>/style/images/trans20.png);">
										<s:property value="name"/>
									</td>
									<s:if test="!#it.last"><td width="10"></td></s:if>
								</s:iterator>
							</tr>
							<s:if test="!#itRows.last">
								<tr height="10"><td colSpan="7"></td></tr>
							</s:if>
						</s:iterator>
					</table>
				</td>
			</tr>
		</table>
	</s:iterator>
</media:panel>
