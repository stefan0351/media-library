<%@ page language="java" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Index">
	<table width="765"><tr><td class="content2"><small>[
		<s:iterator var="letter" value="letters" status="it">
			<a class=link href="<s:url action="ListShows"><s:param name="letter" value="#letter"/></s:url>"><s:property value="#letter"/></a> |
		</s:iterator>
		<a class="link" href="<s:url action="ListShows"><s:param name="letter" value="'all'"/></s:url>">All</a>
		] (<s:property value="shows.size"/> Shows)
	</small></td></tr></table>
	<br>
	<table width="765">
		<tr valign=top>
		<td class="content2" width="20"><b><s:property value="letter"/></b></td>
		<td class="content2" width=700>
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
		</td>
		</tr>
	</table>
</media:panel>
