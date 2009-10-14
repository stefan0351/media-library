<%@ taglib prefix="media" uri="/media-tags" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<media:panel title="Pairings">
	<ul>
		<s:iterator value="pairings">
			<li><a class="link"
				   href="<s:url action="ListFanFics"><s:param name="pairingId" value="id"/></s:url>"><s:property value="name"/></a>
					(<s:property value="fanFicCount"/>)
			</li>
		</s:iterator>
	</ul>
</media:panel>
