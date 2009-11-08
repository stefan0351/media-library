<%@ page language="java" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{seriesName}">
	<ul>
		<s:iterator value="books">
			<li><s:if test="seriesNumber!=null"><s:property value="seriesNumber"/>.</s:if> <media:format variant="series"/> by <media:format value="authors"/></li>
		</s:iterator>
	</ul>
</media:panel>
