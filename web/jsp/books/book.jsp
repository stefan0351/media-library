<%@ page language="java" %>

<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="germanSummary" value="book.getSummaryText(@com.kiwisoft.media.LanguageManager@GERMAN)"/>
<s:set var="englishSummary" value="book.getSummaryText(@com.kiwisoft.media.LanguageManager@ENGLISH)"/>
<s:if test="#germanSummary!=null || #englishSummary!=null">
	<media:panel title="Summary">
		<s:if test="#englishSummary!=null">
			<p><media:format value="@com.kiwisoft.media.LanguageManager@ENGLISH" variant="icon only"/>
				<media:format value="#englishSummary" variant="preformatted"/></p>
		</s:if>
		<s:if test="#germanSummary!=null">
			<s:if test="#englishSummary!=null">
				<hr size="1" color="black">
			</s:if>
			<p><media:format value="@com.kiwisoft.media.LanguageManager@GERMAN" variant="icon only"/>
				<media:format value="#germanSummary" variant="preformatted"/></p>
		</s:if>
	</media:panel>
</s:if>

<media:panel title="Details">
	<dl>
		<s:set var="authors" value="book.authors"/>
		<s:if test="!#authors.empty">
			<dt>Author:</dt>
			<dd><media:formatList value="#authors"/></dd>
		</s:if>
		<s:set var="translators" value="book.translators"/>
		<s:if test="!#translators.empty">
			<dt>Translated by:</dt>
			<dd><media:formatList value="#translators"/></dd>
		</s:if>
		<s:if test="!book.seriesName.empty">
			<dt>Series:</dt>
			<dd>Part <s:if test="book.seriesNumber!=null"><s:property value="book.seriesNumber"/></s:if> of the
				<a class="link" href="<s:url action="BookSeries"><s:param name="seriesName" value="book.seriesName"/></s:url>">&quot;<s:property value="book.seriesName"/>&quot; Series</a></dd>
		</s:if>
		<s:if test="book.language!=null">
			<dt>Language:</dt>
			<dd><media:format value="book.language"/></dd>
		</s:if>
		<s:if test="book.publisher!=null">
			<dt>Published by:</dt>
			<dd><s:property value="book.publisher"/></dd>
		</s:if>
		<s:if test="book.publishedYear!=null && book.edition!=null">
			<dt>Edition:</dt>
			<dd><s:property value="book.edition"/> <s:property value="book.publishedYear"/></dd>
		</s:if>
		<s:if test="book.binding!=null">
			<dt>Binding:</dt>
			<dd><s:property value="book.binding"/></dd>
		</s:if>
		<s:if test="book.pageCount!=null && book.pageCount>0">
			<dt>Pages:</dt>
			<dd><s:property value="book.pageCount"/></dd>
		</s:if>
		<s:set var="isbn" value="book.isbn"/>
		<s:if test="isbn!=null">
			<dt>ISBN:</dt>
			<dd><s:property value="isbn"/></dd>
		</s:if>
		<s:if test="book.show!=null">
			<dt>References:</dt>
			<dd><media:format value="book.show"/></dd>
		</s:if>
	</dl>
</media:panel>
