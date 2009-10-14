<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:iterator value="xmlBean.getValues('section')">
	<media:panel title="%{getValue('title')}">
		<s:property escape="false"/><br clear="all">
	</media:panel>
</s:iterator>
