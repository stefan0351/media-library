<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel id="details" title="Details">
	<dl>
		<s:if test="!person.altNames.empty">
			<dt>Also Known As:</dt>
			<s:iterator value="person.altNames">
				<dd><media:format value="name"/></dd>
			</s:iterator>
		</s:if>
		<s:if test="!person.imdbKey.empty || !person.tvcomKey.empty">
			<dt>Links</dt>
			<s:if test="!person.imdbKey.empty">
				<dd><a target="_blank" class="link" href="http://www.imdb.com/name/<s:property value="person.imdbKey" escape="false"/>/">
					<img src="<%=request.getContextPath()%>/file/?type=Icon&name=imdb" alt="IMDb" border="0"/>
					http://www.imdb.com/name/<s:property value="person.imdbKey"/>/</a></dd>
			</s:if>
			<s:if test="!person.tvcomKey.empty">
				<dd><a target="_new" class="link" href="http://www.tv.com/text/person/<s:property value="person.tvcomKey" escape="false"/>/summary.html">
					<img src="http://www.tv.com/favicon.ico" alt="IMDb" border="0"/>
					http://www.tv.com/text/person/<s:property value="person.tvcomKey"/>/summary.html</a></dd>
			</s:if>
		</s:if>
	</dl>
</media:panel>

<media:panel id="filmography" title="Filmography">
	<s:if test="!actingCredits.empty">
		<table class="contenttable" width="765">
			<tr><td class="header2">Actor/Actress</td></tr>
			<tr>
				<td class="content">
					<ol>
						<s:iterator value="actingCredits.productions">
							<li><b><media:format value="top"/></b>
								<s:if test="top instanceof com.kiwisoft.media.movie.Movie">
									<s:if test="top.year!=null">(<s:property value="top.year"/>)</s:if>
								</s:if>
								<s:if test="top instanceof com.kiwisoft.media.show.Show">
									<s:if test="top.yearString!=null">(<s:property value="top.yearString"/>)</s:if>
								</s:if>
								<s:set var="mainCredits" value="actingCredits.getCredits(top)"/>
								<s:if test="!#mainCredits.empty">
									...
									<s:iterator value="#mainCredits" status="it">
										<s:if test="!characterName.empty">
											<s:if test="!#it.first">/</s:if>
											<media:format value="characterName" variant="preformatted"/>
										</s:if>
									</s:iterator>
								</s:if>
								<s:else>
									<s:set var="subProductions" value="actingCredits.getSubProductions(top)"/>
									<s:subset source="#subProductions" count="5">
										<s:iterator>
											<br>- <media:format value="top" variant="Show"/> ...
											<s:set var="subCredits" value="actingCredits.getCredits(top)"/>
											<s:iterator value="subCredits">
												<s:if test="!characterName.empty">
													<s:if test="!#it.first">/</s:if>
													<media:format value="characterName" variant="preformatted"/>
												</s:if>
											</s:iterator>
										</s:iterator>
									</s:subset>
									<s:if test="#subProductions.size()>5">
										<br>and <s:property value="#subProductions.size()-5"/> more
									</s:if>
								</s:else></li>
						</s:iterator>
					</ol>
				</td>
			</tr>
		</table>
	</s:if>
	<s:iterator value="creditMap.keySet()">
		<s:set var="crewCredits" value="creditMap.get(top)"/>
		<table class="contenttable" width="765">
			<tr><td class="header2"><s:property value="asName"/></td></tr>
			<tr>
				<td class="content">
					<ol>
						<s:iterator value="#crewCredits.productions">
							<li><b><media:format value="top"/></b>
								<s:if test="top instanceof com.kiwisoft.media.movie.Movie">
									<s:if test="top.year!=null">(<s:property value="top.year"/>)</s:if>
								</s:if>
								<s:if test="top instanceof com.kiwisoft.media.show.Show">
									<s:if test="top.yearString!=null">(<s:property value="top.yearString"/>)</s:if>
								</s:if>
								<s:set var="mainCredits" value="#crewCredits.getCredits(top).{? !#this.subType.empty}"/>
								<s:if test="!#mainCredits.empty">
									(<media:formatList value="#mainCredits.{subType}" variant="preformatted"/>)</s:if>
								<s:else>
									<s:set var="subProductions" value="#crewCredits.getSubProductions(top)"/>
									<s:subset source="#subProductions" count="5">
										<s:iterator>
											<br>- <media:format variant="Show"/>
											<s:set var="subCredits" value="#crewCredits.getCredits(top).{? #this.subType.empty}"/>
											<s:if test="!#subCredits.empty">
												(<media:formatList value="#subCredits.{subType}" variant="preformatted"/>)
											</s:if>
										</s:iterator>
									</s:subset>
									<s:if test="#subProductions.size()>5">
										<br>and <s:property value="#subProductions.size()-5"/> more
									</s:if>
								</s:else></li>
						</s:iterator>
					</ol>
				</td>
			</tr>
		</table>
	</s:iterator>
</media:panel>

<s:set var="writtenBooks" value="person.writtenBooks"/>
<s:set var="translatedBooks" value="person.translatedBooks"/>
<s:if test="!#writtenBooks.empty || !#translatedBooks.empty">
	<media:panel id="books" title="Books">
		<s:if test="!#writtenBooks.empty">
			<table class="contenttable" width="765">
				<tr><td class="header2">Author</td></tr>
				<tr>
					<td class="content">
						<ol>
							<s:iterator value="#writtenBooks">
								<li><media:format/></li>
							</s:iterator>
						</ol>
					</td>
				</tr>
			</table>
		</s:if>
		<s:if test="!#translatedBooks.empty">
			<table class="contenttable" width="765">
				<tr><td class="header2">Translator</td></tr>
				<tr>
					<td class="content">
						<ol>
							<s:iterator value="#translatedBooks">
								<li><media:format/></li>
							</s:iterator>
						</ol>
					</td>
				</tr>
			</table>
		</s:if>
	</media:panel>
</s:if>