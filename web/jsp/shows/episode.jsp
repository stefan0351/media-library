<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="%{'Episode '+episode.getTitleWithKey(null)}">

	<table class="contenttable" width="765">
		<tr><td class="header2"><a name="content">Summary</a></td></tr>
		<tr>
			<td class="content">
				<s:if test="!summaries.empty">
					<s:iterator value="summaries" status="it">
						<p><media:format value="language" variant="icon only"/>
							<media:format value="summary" variant="preformatted"/></p>
						<s:if test="!#it.last">
							<hr size="1" color="black">
						</s:if>
					</s:iterator>
				</s:if>
			</td>
		</tr>
	</table>

	<table class="contenttable" width="765">
		<tr><td class="header2"><a name="production">Production Details</a></td></tr>
		<tr>
			<td class="content">
				<dl>
					<s:if test="(!episode.germanTitle.empty && episode.germanTitle!=episode.title) || !episode.altNames.empty">
						<dt>Also Known As</dt>
						<s:if test="!episode.germanTitle.empty && episode.germanTitle!=episode.title">
							<dd><s:property value="episode.germanTitle"/> (<media:format value="germany"/>)</dd>
						</s:if>
						<s:iterator value="episode.altNames">
							<dd><s:property value="name"/> (<media:format value="language"/>)</dd>
						</s:iterator>
					</s:if>
					<s:if test="episode.airdate!=null">
						<dt>First Aired:</dt>
						<dd><media:format value="episode.airdate" variant="Date only"/></dd>
					</s:if>
					<s:if test="!episode.productionCode.empty">
						<dt>Production Code:</dt>
						<dd><s:property value="episode.productionCode"/></dd>
					</s:if>
					<s:set var="media" value="episode.media"/>
					<s:if test="!#media.empty">
						<dt>Media:</dt>
						<s:iterator value="#media">
							<dd <s:if test="obsolete">style="text-decoration: line-through;"</s:if>>
								<media:format value="top" variant="Full"/>
							</dd>
						</s:iterator>
					</s:if>
				</dl>
			</td>
		</tr>
	</table>

	<s:if test="!writers.empty || !directors.empty || !mainCast.empty || !recurringCast.empty || !guestCast.empty">
		<table class="contenttable" width="765">
			<tr><td class="header2"><a name="castAndCrew">Cast and Crew</a></td></tr>
			<tr>
				<td class="content">
					<dl>
						<s:if test="!writers.empty">
							<dt>Writing Credits:</dt>
							<dd><s:iterator value="writers" status="it">
								<s:if test="!#it.first">, </s:if><media:format value="person"/>
								<s:if test="!subType.empty"> (<s:property value="subType"/>)</s:if>
							</s:iterator></dd>
						</s:if>
						<s:if test="!directors.empty">
							<dt>Directed by:</dt>
							<dd><s:iterator value="directors" status="it">
								<s:if test="!#it.first">, </s:if><media:format value="person"/>
							</s:iterator></dd>
						</s:if>
						<s:if test="!mainCast.empty">
							<dt>Main Cast:</dt>
							<dd>
								<table cellspacing="2" cellpadding="2">
									<s:iterator value="mainCast">
										<tr><td class="content2"><media:format value="actor"/></td>
											<td>...</td>
											<td><s:property value="characterName"/></td>
											<td><s:if test="!voice.empty">voice: <s:property value="voice"/></s:if></td></tr>
									</s:iterator>
								</table>
							</dd>
						</s:if>
						<s:if test="!recurringCast.empty">
							<dt>Recurring Cast:</dt>
							<dd>
								<table cellspacing="2" cellpadding="2">
									<s:iterator value="recurringCast">
										<tr><td class="content2"><media:format value="actor"/></td>
											<td>...</td>
											<td><s:property value="characterName"/></td>
											<td><s:if test="!voice.empty">voice: <s:property value="voice"/></s:if></td></tr>
									</s:iterator>
								</table>
							</dd>
						</s:if>
						<s:if test="!guestCast.empty">
							<dt>Guest Cast:</dt>
							<dd>
								<table cellspacing="2" cellpadding="2">
									<s:iterator value="guestCast">
										<tr><td class="content2"><media:format value="actor"/></td>
											<td>...</td>
											<td><s:property value="characterName"/></td>
											<td><s:if test="!voice.empty">voice: <s:property value="voice"/></s:if></td></tr>
									</s:iterator>
								</table>
							</dd>
						</s:if>
					</dl>
				</td>
			</tr>
		</table>
	</s:if>
</media:panel>

