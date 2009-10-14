<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Music">
	<s:iterator value="xmlBean.getValues('song')">
		<table class="contenttable" width="765">
			<tr><td class="header2"><media:format value="getValue('title')"/></td></tr>
			<tr>
				<td class="content">
					<table>
						<s:set var="episodeKey" value="getValue('episode')"/>
						<s:if test="#episodeKey!=null">
							<tr><td class="content2"><b>Episode:</b></td><td class="content2"><media:format value="getEpisode(#episodeKey)"/></td></tr>
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
