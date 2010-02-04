<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Search Result">
	<p>Search for '<s:property value="text"/>'</p>
	<s:if test="nothingFound">
		<p><b>The search return no results.</b></p>
	</s:if>
	<s:if test="!shows.empty">
		<p><b><s:property value="shows.size"/> Show(s) found</b>
		<ol>
			<s:iterator value="shows">
				<li><media:format value="top"/></li>
			</s:iterator>
		</ol>
		</p>
	</s:if>
	<s:set var="episodes" value="episodes"/>
	<s:if test="!#episodes.empty">
		<p><b><s:property value="#episodes.childrenCount"/> Episode(s) found</b>
		<ol>
			<s:iterator var="show" value="#episodes.keySet()">
				<li><media:format value="#show"/>
					<s:iterator var="episode" value="getEpisodes(#show)">
						<br>- <media:format value="#episode"/>
					</s:iterator>
				</li>
			</s:iterator>
		</ol>
		</p>
	</s:if>
	<s:if test="!movies.empty">
		<p><b><s:property value="movies.size"/> Movie(s) found</b>
		<ol>
			<s:iterator value="movies">
				<li><media:format value="top"/>
					<s:if test="year!=null">(<s:property value="year"/>)</s:if>
				</li>
			</s:iterator>
		</ol>
		</p>
	</s:if>
	<s:if test="!persons.empty">
		<p><b><s:property value="persons.size"/> Person(s) found</b>
		<ol>
			<s:iterator value="persons">
				<li><media:format value="top"/></li>
			</s:iterator>
		</ol>
		</p>
	</s:if>
	<s:if test="!books.empty">
		<p><b><s:property value="books.size"/> Book(s) found</b>
		<ol>
			<s:iterator value="books">
				<li><media:format value="top"/></li>
			</s:iterator>
		</ol></p>
	</s:if>
</media:panel>
