<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="List">
	<table width="765">
	<tr><td class="content2">
			<small>[
				<s:iterator var="letter" value="letters" status="it">
					<a class=link href="<s:url action="ListBooks"><s:param name="letter" value="#letter"/></s:url>"><s:property value="#letter.toString()"/></a>
					<s:if test="!#it.last">|</s:if>
				</s:iterator>
				] (<s:property value="books.size"/> Books)
			</small>
		</td>
	</tr></table>
	<br>
	<table width="765">
	<tr valign=top>
		<td class="content2" width="20"><b><s:property value="letter"/></b></td>
		<td class="content2" width=700>
			<ul>
				<s:iterator value="books">
					<li><b><media:format/></b> by <media:formatList value="authors"/></li>
				</s:iterator>
			</ul>
		</td>
	</tr>
	</table>
</media:panel>
