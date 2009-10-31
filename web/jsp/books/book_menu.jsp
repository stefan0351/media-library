<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="cover!=null">
	<table class="menutable">
	<tr><td class="menuheader">Book</td></tr>
	<tr><td class="menuitem" align="center"><media:thumbnail title="Cover" image="cover" thumbnail="thumbnail"/></td></tr>
	</table>
</s:if>
