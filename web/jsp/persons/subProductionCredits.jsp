<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="credits" value="credits"/>

<media:panel title="%{production.productionTitle+': '+type.asName}">
	<ol>
		<s:iterator value="credits.productions">
			<li><b><media:format variant="Show"/></b>
				<s:if test="type.actingCredit">
					<s:set var="actingCredits" value="#credits.getCredits(top).{? !characterName.empty}"/>
					<s:if test="!#actingCredits.empty">
						... <media:formatList value="#actingCredits.{characterName}" variant="preformatted" separator="/"/>
					</s:if>
				</s:if>
				<s:else>
					<s:set var="crewCredits" value="#credits.getCredits(top).{? !subType.empty}"/>
					<s:if test="!#crewCredits.empty">(<media:formatList value="#crewCredits.{subType}" variant="preformatted"/>)</s:if>
				</s:else>
			</li>
		</s:iterator>
	</ol>
</media:panel>