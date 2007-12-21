<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">

	<xsl:template match="medium">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="a4-landscape" page-height="21cm" page-width="29.7cm">
					<fo:region-body margin-bottom="1cm" margin-right="1cm" margin-left="1cm" margin-top="1cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="a4-landscape" format="1">
				<fo:flow flow-name="xsl-region-body">
					<fo:table table-layout="fixed" width="268" height="180mm" border-width="1pt" border-style="solid">
						<fo:table-column column-width="129mm"/>
						<fo:table-column column-width="10mm"/>
						<fo:table-column column-width="129mm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell height="180mm">
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell height="180mm">
										<fo:block-container width="180mm" height="10mm" reference-orientation="90">
											<fo:block text-align="left" margin-left="3mm" margin-top="3mm">The Last Mimzy</fo:block>
										</fo:block-container>
								</fo:table-cell>
								<fo:table-cell height="180mm">
									<fo:block vertical-align="center" text-align="center"><fo:external-graphic display-align="center" src="url('http://localhost:8080/movies/posters/last_mimzy.jpg')" content-width="129mm" content-height="180mm"/></fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

</xsl:stylesheet>

