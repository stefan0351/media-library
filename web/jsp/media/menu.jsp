<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="menutable">
	<tr><td class="menuheader">Media</td></tr>
	<tr><td class="menuitem"><a class="menulink" href="<s:url action="ListMedia"/>">Index</a></td></tr>

	<tr>
		<td>
			<hr size=1 color=black>
		</td>
	</tr>

	<tr><td class="menuitem"><a class="menulink" href="<%=request.getContextPath()%>/create.pdf?template=/media/mediaByKey.fo.vsl">
		<img src="<%=request.getContextPath()%>/icons/printer.png" border="0" title="Create PDF"> Print Version</a></td>
	</tr>
</table>
