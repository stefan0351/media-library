<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Schedule">
	<form action="<s:url/>" method="get">
		<input type="hidden" name="personId" value="<s:property value="person.id"/>">
		<input type="hidden" name="showId" value="<s:property value="show.id"/>">
		Date Range:
		<select name="rangeId" size="1" onChange="this.form.submit();">
			<s:set var="custom" value="@com.kiwisoft.media.DateRange@CUSTOM"/>
			<s:iterator value="@com.kiwisoft.media.DateRange@values()">
				<s:if test="#custom.id!=top.id">
					<option value="<s:property value="id"/>" <s:if test="range==top">selected="selected"</s:if>><media:format value="top"/></option>
				</s:if>
			</s:iterator>
		</select><br/><br/>
		<media:table model="scheduleTable" alternateRows="true"/>
	</form>
</media:panel>
