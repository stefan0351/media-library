<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">

	<xsl:template match="videos">

		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

			<fo:layout-master-set>

				<fo:simple-page-master master-name="all"
					page-height="21cm" page-width="29.7cm"
					margin-top="1cm" margin-bottom="1cm"
					margin-left="1cm" margin-right="1cm">
					<fo:region-body margin-top="2cm" margin-bottom="2cm" column-count="3" column-gap="0.5cm"/>
					<fo:region-before extent="2cm"/>
					<fo:region-after extent="2cm"/>
				</fo:simple-page-master>

			</fo:layout-master-set>

			<fo:page-sequence master-reference="all" format="1">

				<!-- header with running glossary entries -->
				<!--				<fo:static-content flow-name="xsl-region-before">-->
				<!--					<fo:block text-align="start" font-size="10pt" font-family="serif" line-height="1em + 2pt">-->
				<!--						<fo:retrieve-marker retrieve-class-name="term" retrieve-boundary="page" retrieve-position="first-starting-within-page"/>-->
				<!--						<fo:leader leader-alignment="reference-area" leader-pattern="dots" leader-length="4in"/>-->
				<!--						<fo:retrieve-marker retrieve-class-name="term" retrieve-boundary="page" retrieve-position="last-ending-within-page"/>-->
				<!--					</fo:block>-->
				<!--				</fo:static-content>-->

				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="center" font-size="10pt" font-family="serif" line-height="1em + 2pt">
						<fo:page-number/> of <fo:page-number-citation ref-id="last-page"/>
					</fo:block>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:block space-after.optimum="10pt">
						<fo:table table-layout="fixed" width="100%" border-width="1pt" border-style="solid" table-omit-header-at-break="false" table-omit-footer-at-break="true">
							<fo:table-column column-width="10mm"/>
							<fo:table-column column-width="69mm"/>
							<fo:table-column column-width="10mm"/>
							<fo:table-header>
								<fo:table-row background-color="rgb(200,200,255)">
									<fo:table-cell><fo:block font-size="8pt">Key</fo:block></fo:table-cell>
									<fo:table-cell><fo:block font-size="8pt">Name</fo:block></fo:table-cell>
									<fo:table-cell><fo:block font-size="8pt">Storage</fo:block></fo:table-cell>
								</fo:table-row>
							</fo:table-header>
							<fo:table-body>
								<xsl:apply-templates select="video"/>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block id="last-page"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template match="video">
		<fo:table-row>
			<xsl:attribute name="background-color">
				<xsl:choose>
					<xsl:when test="position() mod 2 =0">rgb(255,255,255)</xsl:when>
					<xsl:otherwise>rgb(235,235,235)</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<fo:table-cell>
				<fo:block font-size="8pt">
					<xsl:value-of select="@key"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="8pt">
					<xsl:value-of select="@name"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block font-size="8pt">
					<xsl:value-of select="@storage"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

</xsl:stylesheet>

