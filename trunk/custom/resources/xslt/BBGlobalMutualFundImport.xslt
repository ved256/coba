<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template name="extra-fields"><!-- The list of fields should be added here.
Separated by Newlines -->
COUNTRY_ISO
DES_NOTES
ISSUER
SECURITY_DES
EXCHANGE_TYPE
IS_ISLAMIC
144A_FLAG
SETTLE_DT
MTY_YEARS
LEGAL_ENTITY_IDENTIFIER
</xsl:template>
<xsl:template name="extra-header-fields">
CLOSINGVALUES=yes
DERIVED=yes
SECID=ISIN
SECMASTER=yes</xsl:template>
</xsl:stylesheet>
