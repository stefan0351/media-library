<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{groupName}">
	<s:if test="group!=null"><p><media:format value="group" variant="hierarchy"/></p></s:if>
	<s:if test="!links.empty">
		<ul>
			<s:iterator value="links">
				<li><b><media:format value="name"/></b><br>
					<a class="link" target="_blank" href="<s:property value="url" escape="false"/>"><s:property value="url"/></a>
				</li>
			</s:iterator>
		</ul>
	</s:if>
	<s:if test="!childGroups.empty">
		<span style="text-decoration: underline;">Sub Groups</span>
		<ul>
			<s:iterator value="childGroups">
				<li><media:format value="top"/></li>
			</s:iterator>
		</ul>
	</s:if>
	<s:if test="!relatedGroups.empty">
		<span style="text-decoration: underline;">Related Groups</span>
		<ul>
			<s:iterator value="relatedGroups">
				<li><media:format value="top" variant="hierarchy"/></li>
			</s:iterator>
		</ul>
	</s:if>
</media:panel>
