<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Genres">
	<ul>
		<s:iterator value="genres">
			<li><media:format value="top"/></li>
		</s:iterator>
	</ul>
</media:panel>
