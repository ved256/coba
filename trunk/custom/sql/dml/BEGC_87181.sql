--------------------------------------------------------------------
-- StarterSet BEGC_87181.sql
--------------------------------------------------------------------
-- |----------------------------------------------------------------
-- | Product ID: GSDM
-- | Project: RMB
-- | Date: 17-Jul-2019
-- | Requested By: 
-- | Approved By: 
-- | Prepared By: Vipul R.
-- | Patch :- 8.7.1.8.1
-- |----------------------------------------------------------------
-- | Tables Affected: FT_T_BEGC,FT_T_DGDF,FT_T_DGDP
-- |----------------------------------------------------------------

SET SCAN OFF;
SET DEFINE OFF;

--------------------------------------------------------------------

UPDATE FT_T_BEGC SET CONFIG_VAL_TXT = 'true', LAST_CHG_TMS = SYSDATE, LAST_CHG_USR_ID = 'RMB:CSTM' WHERE config_grp_nme = 'ISSUROOTSEG';

UPDATE FT_T_BEGC SET CONFIG_VAL_TXT = 'true', LAST_CHG_TMS = SYSDATE, LAST_CHG_USR_ID = 'RMB:CSTM' WHERE config_grp_nme = 'CADFROOTSEG';


UPDATE FT_T_DGDF SET Multiplicity_TYP = 'SINGLE', LAST_CHG_TMS = SYSDATE, LAST_CHG_USR_ID = 'RMB:CSTM' WHERE BETD_OID = '++3qwBhS2etxm007' AND DGDF_OID = '++4hjSFy2ets801C';
 
UPDATE FT_T_DGDP SET Multiplicity_TYP = 'SINGLE', LAST_CHG_TMS = SYSDATE, LAST_CHG_USR_ID = 'RMB:CSTM' WHERE DGDF_OID = '++4hjSFy2ets801C';