<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<media:panel title="Theme">
	<s:iterator value="xmlBean.getValues('theme')">
		<table class="contenttable" width="765">
			<tr><td class="header2"><s:property value="getValue('description')" default="Theme"/></td></tr>
			<tr>
				<td class="content">
					<table>
						<s:set var="title" value="getValue('title')"/>
						<s:if test="#title!=null">
							<tr><td class="content2"><b>Title:</b></td><td class="content2"><s:property value="#title"/></td></tr>
						</s:if>
						<s:set var="composer" value="getValue('composer')"/>
						<s:if test="#composer!=null">
							<tr><td class="content2"><b>Composer:</b></td><td class="content2"><s:property value="#composer"/></td></tr>
						</s:if>
						<s:set var="interpret" value="getValue('interpret')"/>
						<s:if test="#interpret!=null">
							<tr><td class="content2"><b>Interpret:</b></td><td class="content2"><s:property value="#interpret"/></td></tr>
						</s:if>
						<s:set var="source" value="getValue('source')"/>
						<s:if test="#source!=null">
							<tr><td class="content2"><b>File:</b></td><td class="content2"><a class="link" href="<s:property value="source" escape="false"/>"><img
									src="<%=request.getContextPath()%>/icons/sound.gif" alt="Sound" border="0"></a> (<s:property value="getValue('length')"/>)
							</td></tr>
						</s:if>
						<s:set var="lyrics" value="getValue('lyrics')"/>
						<s:if test="#lyrics!=null">
							<tr valign="top"><td class="content2"><b>Lyrics:</b></td><td class="content2"><media:format value="#lyrics.toString()"
																														variant="preformatted"/></td></tr>
						</s:if>
					</table>
				</td>
			</tr>
		</table>
	</s:iterator>
</media:panel>