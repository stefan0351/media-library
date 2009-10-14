<%@ page language="java" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<media:panel title="Error">
	<table class="contenttable" width="765">
		<tr><td class="header2">Error Message</td></tr>
		<tr>
			<td class="content">
				<s:actionerror cssClass="errorList"/>
				<s:if test="exception!=null">
					<p><s:property value="%{exception.message}"/></p>
				</s:if>
			</td>
		</tr>
	</table>
	<s:if test="exception!=null">
		<table class="contenttable" width="765">
			<tr><td class="header2">Technical Details</td></tr>
			<tr>
				<td class="content">
					<p><s:property value="%{exceptionStack}"/></p>
				</td>
			</tr>
		</table>
	</s:if>
</media:panel>
