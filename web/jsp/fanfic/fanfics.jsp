<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{container.fanFicGroupName}">
	<s:if test="author!=null">
		<s:if test="!author.mail.empty || !author.web.empty">
			<table class="contenttable" width="765">
				<tr>
					<td class=header2>Author</td>
				</tr>
				<tr>
					<td class="content">
						<dl>
							<s:if test="!author.mail.empty">
								<dt>EMail:</dt>
								<s:iterator value="author.mail">
									<dd><a class="link"
										   href="mailto:<s:property value="value" escape="false"/>"><s:property
											value="value"/></a></dd>
								</s:iterator>
							</s:if>
							<s:if test="!author.web.empty">
								<dt>Web:</dt>
								<s:iterator value="author.web">
									<dd><a class="link" href="<s:property value="value" escape="false"/>"><s:property
											value="value"/></a></dd>
								</s:iterator>
							</s:if>
						</dl>
					</td>
				</tr>
			</table>
		</s:if>
	</s:if>

	<s:if test="fanDom!=null">
		<s:set var="linkGroup" value="fanDom.linkGroup"/>
		<s:if test="#linkGroup!=null && #linkGroup.linkCount>0">
			<table class="contenttable" width="765">
				<tr>
					<td class=header2>Links</td>
				</tr>
				<tr>
					<td class="content">
						<ul>
							<s:sort source="#linkGroup.links"
									comparator="@com.kiwisoft.utils.NaturalComparator@getInstance()">
								<s:iterator>
									<li><b><s:property value="name"/></b><br>
										<a class="link" target="_new"
										   href="<s:property value="url" escape="false"/>"><s:property value="url"/></a>
									</li>
								</s:iterator>
							</s:sort>
						</ul>
					</td>
				</tr>
			</table>
		</s:if>
	</s:if>

	<table class="contenttable" width="765">
		<tr><td class=header2>FanFics</td></tr>
		<tr>
			<td class="content">
				<table>
					<tr>
						<td class="content2">
							<small>[
								<s:iterator value="letters">
									<a class=link
									   href="<s:url action="ListFanFics" includeParams="get"><s:param name="letter" value="top"/></s:url>"><s:property
											value="top"/></a> |
								</s:iterator>
								<a class="link"
								   href="<s:url action="ListFanFics" includeParams="get"><s:param name="letter" value="'all'"/></s:url>">All</a>
								] (<s:property value="container.fanFicCount"/> FanFics)
							</small>
						</td>
					</tr>
				</table>
				<br>
				<table>
					<s:iterator value="visibleLetters">
						<tr valign="top">
							<td class="content2"><b><a name="<s:property value="top"/>"><s:property
									value="top"/></a></b></td>
							<td class="content2" width=600>
								<ul>
									<s:sort source="container.getFanFics(top.charValue())" comparator="comparator">
										<s:iterator>
											<li><a class="link"
												   href="<s:url action="FanFic"><s:param name="fanFicId" value="id"/></s:url>">
												&quot;<s:property value="title"/>&quot;</a>
												<s:if test="unfinished">(unfinished)</s:if> by
												<s:iterator value="authors" status="it">
													<a class="link"
													   href="<s:url action="ListFanFics"><s:param name="authorId" value="id"/></s:url>"><s:property
															value="name"/></a>
													<s:if test="!#it.last">,</s:if>
												</s:iterator>
												<br>
												<s:set var="parts" value="parts"/>
												<s:if test="#parts.size()>1">
													<small>Parts:
														<s:iterator value="#parts" status="it">
															<a class="link"
															   href="<s:url action="FanFic"><s:param name="partId" value="id"/></s:url>"><s:property value="#it.index"/></a>
															<s:if test="!#it.last">|</s:if>
														</s:iterator>
													</small><br>
												</s:if>
												<s:set var="fanDoms" value="fanDoms"/>
												<s:if test="!#fanDoms.empty">
													<small>FanDoms:
														<s:iterator value="#fanDoms" status="it">
															<a class="link" href="<s:url action="ListFanFics"><s:param name="fanDomId" value="id"/></s:url>"><s:property value="name"/></a>
															<s:if test="!#it.last">,</s:if>
														</s:iterator>
													</small><br>
												</s:if>
												<s:set var="pairs" value="pairings"/>
												<s:if test="!#pairs.empty">
													<small>Pairings:
														<s:iterator value="#pairs" status="it">
															<a class="link" href="<s:url action="ListFanFics"><s:param name="pairingId" value="id"/></s:url>"><s:property value="name"/></a>
															<s:if test="!#it.last">,</s:if>
														</s:iterator>
													</small><br>
												</s:if>
												<s:if test="description!=null">
													<small>Summary: <s:property value="description"/></small><br>
												</s:if>
												<s:if test="prequel!=null">
													<small>Sequel to:
														<a class="link" href="<s:url action="FanFic"><s:param name="fanFicId" value="prequel.id"/></s:url>"><s:property value="prequel.title"/></a>
													</small>
												</s:if>
												<s:set var="size" value="size"/>
												<small>Size: <s:if test="#size>0"><s:property value="#size>>10"/>kB</s:if><s:else>Unknown</s:else></small>
											</li>
											<br>
										</s:iterator>
									</s:sort>
								</ul>
							</td>
						</tr>
					</s:iterator>
				</table>
			</td>
		</tr>
	</table>
</media:panel>
