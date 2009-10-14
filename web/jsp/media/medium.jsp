<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{'Medium '+medium.fullKey+': '+medium.name}">
	<table>
		<tr><td class="content2"><b>Medium:</b></td><td class="content2"><media:format value="medium.type"/></td></tr>
		<tr><td class="content2"><b>Length:</b></td><td class="content2"><s:property value="medium.length"/></td></tr>
		<tr><td class="content2"><b>Storage:</b></td><td class="content2"><s:property value="medium.storage"/></td></tr>
	</table>
	<br>
	<media:table model="tracksTable" alternateRows="true"/>
</media:panel>
