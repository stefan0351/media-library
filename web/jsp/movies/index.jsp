<%@ page language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<media:panel title="List">
	<table width="765">
	<tr>
		<td class="content2">
			<small>[
				<s:iterator var="letter" value="letters" status="it">
					<a class=link href="<s:url action="ListMovies"><s:param name="letter" value="#letter"/></s:url>"><s:property value="#letter.toString()"/></a>
					<s:if test="!#it.last">|</s:if>
				</s:iterator>
				] (<s:property value="movies.size"/> Movies)
			</small>
		</td>
	</tr>
	</table>
	<br>
	<table width="765">
	<tr valign=top>
		<td class="content2" width="20"><b><a name="<s:property value="letter"/>"><s:property value="letter"/></a></b></td>
		<td class="content2" width=700>
			<ul>
				<s:iterator value="movies">
					<li><b><a class="link" href="<s:url action="MovieDetails"><s:param name="movieId" value="id"/></s:url>"><s:property value="title"/></a></b>
						<s:if test="year!=null">(<s:property value="year"/>)</s:if>
						<s:set var="germanTitle" value="germanTitle"/>
						<s:if test="germanTitle!=null && germanTitle.length()>0 && germanTitle!=title"><br>a.k.a. <i>&quot;<s:property value="#germanTitle"/>&quot;</i></s:if>
				</s:iterator>
			</ul>
		</td>
	</tr>
	</table>
</media:panel>
