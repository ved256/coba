<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text"/>
	<xsl:template name="extra-fields"><!-- The list of fields should be added here.
Separated by Newlines -->
COUNTRY_ISO
ID_BB
SECURITY_DES
EXCHANGE_TYPE
OPT_EXER_TYP
OPT_EXPIRE_DT
OPT_PUT_CALL
OPT_UNDL_CRNCY
OPT_UNDL_TICKER
WRT_PX_TYP
IS_ISLAMIC
144A_FLAG
ISSUE_DT
MATURITY
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
<!-- Stylus Studio meta-information - (c)1998-2003 Copyright Sonic Software Corporation. All rights reserved.
<metaInformation>
<scenarios/><MapperInfo srcSchemaPath="" srcSchemaRoot="" srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
</metaInformation>
-->