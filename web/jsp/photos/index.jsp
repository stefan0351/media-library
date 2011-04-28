<%@ page language="java" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Galleries">
	<table cellspacing="0">
		<s:set var="rows" value="@com.kiwisoft.util.Utils@splitIntoRows(galleries, 4)"/>
		<s:iterator value="#rows" status="itRows">
			<tr>
				<s:iterator value="top" status="it">
					<td style="width:170px; height:130px; text-align:center; vertical-align:middle; background:url(<%=request.getContextPath()%>/style/images/trans10.png);">
						<a href="<s:url action="PhotoGalleryDetails"><s:param name="galleryId" value="id"/></s:url>"><media:image image="thumbnail"/></a>
					</td>
					<s:if test="!#it.last"><td width="10"></td></s:if>
				</s:iterator>
			</tr>
			<tr>
				<s:iterator value="top" status="it">
					<td style="width:170px; font-size:8pt; text-align:center; vertical-align:top; background:url(<%=request.getContextPath()%>/style/images/trans20.png);">
						[<media:format value="creationDate" variant="Date only"/>]<br>
						<media:format value="name"/>
					</td>
					<s:if test="!#it.last"><td width="10"></td></s:if>
				</s:iterator>
			</tr>
			<s:if test="!#itRows.last"><tr height="10"><td colSpan="7"></td></tr></s:if>
		</s:iterator>
	</table>
</media:panel>
