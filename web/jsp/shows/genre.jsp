<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{genre.name}">
	<ul>
		<s:iterator value="shows">
			<li><b><media:format value="top"/></b>
				<s:if test="yearString!=null">(<s:property value="yearString"/>)</s:if>
				<s:if test="language.symbol!='de'">
					<s:set var="germanTitle" value="germanTitle"/>
					<s:if test="#germanTitle!=null && #germanTitle.length()>0 && #germanTitle!=title"><br>a.k.a. <i>&quot;<s:property value="#germanTitle"/>&quot;</i></s:if>
				</s:if>
		</s:iterator>
	</ul>
</media:panel>
