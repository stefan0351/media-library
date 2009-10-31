<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<media:panel title="%{fanFic.title}">
	<table class="contenttable" width="765">
		<tr><td class="header2">Credits</td></tr>
		<tr>
			<td class="content2">
				<table>
					<tr class="content2" valign=top><td><b>Author:</b></td><td>
						<s:iterator value="fanFic.authors" status="it">
							<a class="link"
							   href="<s:url action="ListFanFics"><s:param name="authorId" value="id"/></s:url>"><s:property
									value="name"/></a><s:if test="!#it.last">,</s:if>
						</s:iterator>
					</td></tr>
					<tr class="content2" valign=top><td><b>FanDom:</b></td><td>
						<s:iterator value="fanFic.fanDoms">
							<a class="link"
							   href="<s:url action="ListFanFics"><s:param name="fanDomId" value="id"/></s:url>"><s:property
									value="name"/></a><s:if test="!#it.last">,</s:if>
						</s:iterator>
					</td></tr>
					<tr class="content2" valign=top><td><b>Pairings:</b></td><td>
						<s:iterator value="fanFic.pairings">
							<a class="link"
							   href="<s:url action="ListFanFics"><s:param name="pairingId" value="id"/></s:url>"><s:property
									value="name"/></a><s:if test="!#it.last">,</s:if>
						</s:iterator>
					</td></tr>
					<s:if test="!fanFic.rating.empty">
						<tr class="content2" valign=top><td><b>Rating:</b></td><td><s:property value="fanFic.rating"/></td></tr>
					</s:if>
					<s:if test="!fanFic.description.empty">
						<tr class="content2" valign=top><td><b>Description:</b></td><td><s:property value="fanFic.description"/></td></tr>
					</s:if>
					<s:if test="!fanFic.spoiler.empty">
						<tr class="content2" valign=top><td><b>Spoiler:</b></td><td><s:property value="fanFic.spoiler"/></td></tr>
					</s:if>
					<s:set var="prequel" value="fanFic.prequel"/>
					<s:set var="sequel" value="fanFic.sequel"/>
					<s:if test="#prequel!=null || #sequel!=null">
						<tr class="content2" valign=top><td><b>Series:</b></td><td>
							<s:if test="#prequel!=null">
								Sequel to <a class="link" href="<s:url action="FanFic"><s:param name="fanFicId" value="#prequel.id"/></s:url>">"<s:property value="#prequel.title"/>"</a>
							</s:if>
							<s:if test="#sequel!=null">
								<s:if test="#prequel!=null">;</s:if>
								Continued in <a class="link" href="<s:url action="FanFic"><s:param name="fanFicId" value="#sequel.id"/></s:url>">"<s:property value="#sequel.title"/>"</a>
							</s:if>
						</td></tr>
					</s:if>
					<s:set var="notes" value="xmlBean.getValues('notes')"/>
					<s:if test="!#notes.empty">
						<tr class="content2" valign=top><td><b>Notes:</b></td><td>
							<s:iterator value="#notes" status="it">
								<s:property value="top"/>
								<s:if test="!#it.last"><br></s:if>
							</s:iterator>
						</td></tr>
					</s:if>
					<s:if test="!fanFic.url.empty">
						<tr class="content2" valign="top"><td><b>Web:</b></td><td><a class="link" href="<s:property value="fanFic.url" escape="false"/>"><s:property value="fanFic.url"/></a></td></tr>
					</s:if>
				</table>
			</td>
		</tr>
	</table>

	<s:if test="html!=null">
		<table class="contenttable" width="765">
		<tr><td class=header2><s:if test="!part.name.empty"><s:property value="part.name"/></s:if><s:else>Story</s:else></td></tr>
		<tr><td class="content"><s:property value="html" escape="false"/></td></tr>
		</table>
	</s:if>
	<s:else>
		<s:iterator value="xmlBean.getValues('chapter')" status="it">
			<table class="contenttable" width="765">
			<tr><td class=header2><s:if test="getValue('title')!=null"><s:property value="getValue('title')"/></s:if><s:else>Story</s:else></td></tr>
				<tr><td class="content">
					<s:property value="top" escape="false"/>
					<s:if test="!#it.last"><p align="right"><a class="link" href="#top">Top</a></p></s:if>
				</td></tr>
			</table>
		</s:iterator>
	</s:else>

	<s:if test="nextPart!=null">
		<p align=center><b>[</b> <a class="link" href="<s:url action="FanFic"><s:param name="partId" value="nextPart.id"/></s:url>">Next Part</a> <b>]</b></p>
	</s:if>
	<s:elseif test="!fanFic.finished">
		<p align=center><b>[</b> To be continued... <b>]</b></p>
	</s:elseif>
	<s:elseif test="#sequel!=null">
		<p align=center><b>[</b> Continued in <a class="link" href="<s:url action="FanFic"><s:param name="fanFicId" value="#sequel.id"/></s:url>">"<s:property value="#sequel.title"/>"</a> <b>]</b></p>
	</s:elseif>
	<s:else>
		<p align=center><b>[</b> The End <b>]</b></p>
	</s:else>
</media:panel>
