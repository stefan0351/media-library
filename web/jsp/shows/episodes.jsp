<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="media" uri="/media-tags" %>

<s:bean var="comparator" name="com.kiwisoft.utils.NaturalComparator"/>
<s:bean var="episodes" name="java.util.ArrayList"/>
<s:set var="episodes" value="#episodes.addAll(show.episodes.elements()), #episodes"/>

<s:iterator value="seasons">
	<media:panel id="%{'season'+number}" title="%{top}">
		<ul>
			<s:sort comparator="#comparator" source="episodes">
				<s:iterator>
					<li><b><media:format value="#episodes.remove(top), top"/></b>
						<s:if test="!germanTitle.empty">(<s:property value="germanTitle"/>)</s:if>
					</li>
				</s:iterator>
			</s:sort>
		</ul>
	</media:panel>
</s:iterator>

<s:if test="!#episodes.empty">
	<media:panel id="episodes" title="Episodes">
		<ul>
			<s:sort comparator="#comparator" source="#episodes">
				<s:iterator>
					<li><b><media:format value="top"/></b>
						<s:if test="!germanTitle.empty">(<s:property value="germanTitle"/>)</s:if>
					</li>
				</s:iterator>
			</s:sort>
		</ul>
	</media:panel>
</s:if>

<s:if test="!show.movies.empty">
	<media:panel id="movies" title="Movies">
		<ul>
			<s:iterator value="show.movies">
				<li><b><media:format value="top"/></b></li>
			</s:iterator>
		</ul>
	</media:panel>
</s:if>
