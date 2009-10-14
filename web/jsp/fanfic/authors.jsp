<%@ page language="java" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Authors">
	<table>
		<tr>
			<td class="content2">
				<small>[
					<s:iterator value="letters" status="it">
						<a class="link"
						   href="<s:url action="ListFanFicAuthors"><s:param name="letter" value="top"/></s:url>"><s:property
								value="top"/></a>
						<s:if test="!#it.last">|</s:if>
					</s:iterator>
					] (<s:property value="authors.size"/> Authors)
				</small>
			</td>
		</tr>
	</table>
	<br>
	<table>
		<tr valign="top">
			<td class="content2"><b><s:property value="letter"/></b></td>
			<td class="content2" width=600>
				<ul>
					<s:iterator value="authors">
						<li><a class="link" href="<s:url action="ListFanFics"><s:param name="authorId" value="id"/></s:url>"><s:property value="name"/></a>
							(<s:property value="fanFicCount"/>)</li>
					</s:iterator>
				</ul>
			</td>
		</tr>
	</table>
</media:panel>
