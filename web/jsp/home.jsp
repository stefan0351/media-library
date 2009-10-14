<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Home">
	<table width="100%">
		<tr style="vertical-align:top">
			<td width="50%">
				<table class="contenttable" width="100%">
					<tr><td class="header2">Recently Visited Shows</td></tr>
					<tr>
						<td class="content">
							<ul>
								<s:iterator value="shows">
									<li><media:format/></li>
								</s:iterator>
							</ul>
						</td>
					</tr>
				</table>
				<table class="contenttable" width="100%">
					<tr><td class="header2">Recently Visited Persons</td></tr>
					<tr>
						<td class="content">
							<ul>
								<s:iterator value="persons">
									<li><media:format/></li>
								</s:iterator>
							</ul>
						</td>
					</tr>
				</table>
			</td>
			<td width="50%">
				<table class="contenttable" width="100%">
					<tr><td class="header2">Recently Visited Movies</td></tr>
					<tr>
						<td class="content">
							<ul>
								<s:iterator value="movies">
									<li><media:format/></li>
								</s:iterator>
							</ul>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</media:panel>