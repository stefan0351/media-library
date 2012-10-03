<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Domains">
	<table class="contenttable" width="765">
	<tr><td class="header2">Shows</td></tr>
	<tr><td class="content">
		<ul>
			<s:iterator value="showDomains">
				<li><a class="link" href="<s:url action="ListFanFics"><s:param name="fanDomId" value="id"/></s:url>"><s:property value="name"/></a>
					(<s:property value="fanFicCount"/>)</li>
			</s:iterator>
		</ul>
	</td></tr>
	</table>

	<table class="contenttable" width="765">
	<tr><td class="header2">Movies</td></tr>
	<tr><td class="content">
		<ul>
			<s:iterator value="movieDomains">
				<li><a class="link" href="<s:url action="ListFanFics"><s:param name="fanDomId" value="id"/></s:url>"><s:property value="name"/></a>
					(<s:property value="fanFicCount"/>)</li>
			</s:iterator>
		</ul>
	</td></tr>
	</table>

	<table class="contenttable" width="765">
	<tr><td class="header2">Others</td></tr>
	<tr><td class="content">
		<ul>
			<s:iterator value="otherDomains">
				<li><a class="link" href="<s:url action="ListFanFics"><s:param name="fanDomId" value="id"/></s:url>"><s:property value="name"/></a>
					(<s:property value="fanFicCount"/>)</li>
			</s:iterator>
		</ul>
	</td></tr>
	</table>
</media:panel>
