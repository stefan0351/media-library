<%@ page language="java" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="!summaries.empty">
	<media:panel id="summary" title="Summary">
		<s:iterator value="summaries" status="it">
			<p><media:format value="language" variant="icon only"/>
			<media:format value="summary" variant="preformatted"/></p>
			<s:if test="!#it.last"><hr size="1" color="black"></s:if>
		</s:iterator>
	</media:panel>
</s:if>

<media:panel id="details" title="Details">
	<table>
		<s:if test="!movie.germanTitle.empty || !movie.altNames.empty">
			<tr valign="top">
				<td class="content2"><b>Also Known As:</b></td>
				<td class="content2">
					<s:if test="!movie.germanTitle.empty">
						<s:property value="movie.germanTitle"/> (<media:format value="germany"/>)<br>
					</s:if>
					<s:iterator value="movie.altNames">
						<s:property value="name"/> (<media:format value="language"/>)<br>
					</s:iterator>
				</td>
			</tr>
		</s:if>
		<s:if test="!movie.genres.empty">
			<tr valign="top">
				<td class="content2"><b>Genre:</b></td>
				<td class="content2"><media:formatList value="movie.genres" sort="true"/></td>
			</tr>
		</s:if>
		<s:if test="!movie.languages.empty">
			<tr valign="top">
				<td class="content2"><b>Language:</b></td>
				<td class="content2"><media:formatList value="movie.languages" sort="true"/></td>
			</tr>
		</s:if>
		<s:if test="!movie.countries.empty">
			<tr valign="top">
				<td class="content2"><b>Country:</b></td>
				<td class="content2"><media:formatList value="movie.countries" sort="true"/></td>
			</tr>
		</s:if>
		<s:if test="movie.year!=null">
			<tr valign="top">
				<td class="content2"><b>Year:</b></td>
				<td class="content2"><s:property value="movie.year"/></td>
			</tr>
		</s:if>
		<s:if test="movie.runtime!=null">
			<tr valign="top">
				<td class="content2"><b>Runtime:</b></td>
				<td class="content2"><s:property value="movie.runtime"/> min</td>
			</tr>
		</s:if>
		<s:if test="!media.empty">
			<tr valign="top">
				<td class="content2"><b>Media:</b></td>
				<td class="content2">
					<s:iterator value="media">
						<s:if test="obsolete"><span style="text-decoration: line-through;"></s:if>
						<media:format value="top" variant="Full"/>
						<s:if test="obsolete"></span></s:if><br>
					</s:iterator>
				</td>
			</tr>
		</s:if>
		<s:if test="!movie.imdbKey.empty">
			<tr valign="top"><td class="content2"><b>Links:</b></td><td class="content2">
				<a target="_new" class="link"
				   href="http://www.imdb.com/title/<s:property value="movie.imdbKey" escape="false"/>/">
					<img src="<%=request.getContextPath()%>/file/?type=Icon&name=imdb" alt="IMDb" border="0"/>http://www.imdb.com/title/<s:property
						value="movie.imdbKey"/>/</a>
			</td></tr>
		</s:if>
	</table>
</media:panel>

<s:if test="!cast.empty">
	<media:panel id="cast" title="Cast">
		<table class="table1">
			<tr class="thead"><td class="tcell2">&nbsp;</td><td class="tcell2">Actor</td><td class="tcell2">Role</td>
			</tr>
			<s:iterator value="cast" status="it">
				<tr class="<s:if test="#it.even">trow1</s:if><s:else>trow2</s:else>">
					<td class="tcell2">
						<s:set var="picture" value="getPicture(top)"/>
						<s:if test="#picture!=null && #picture.thumbnail50x50!=null">
							<s:if test="actor!=null">
								<s:set var="pictureName" value="actor.name"/>
							</s:if>
							<s:else>
								<s:set var="pictureName" value="characterName"/>
							</s:else>
							<media:thumbnail title="%{#pictureName}" image="#picture"
											 thumbnail="#picture.thumbnail50x50"/>
						</s:if>
					</td>
					<td class="tcell2"><media:format value="actor"/></td>
					<td class="tcell2">... <media:format value="characterName" variant="preformatted"/></td>
				</tr>
			</s:iterator>
		</table>
	</media:panel>
</s:if>

<s:if test="!crew.empty">
	<media:panel id="crew" title="Crew">
		<table>
			<s:iterator var="creditType" value="crew.keySet()">
				<tr valign="top">
					<td class="content2"><b><s:property value="byName"/></b></td>
					<td class="content2">
						<s:iterator value="getCredits(top)">
							<media:format value="person"/>
							<s:if test="!subType.empty">
								(<media:format value="subType"/>)
							</s:if><br>
						</s:iterator>
					</td>
				</tr>
			</s:iterator>
		</table>
	</media:panel>
</s:if>
