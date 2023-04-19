--------------------------------------------------------------------
-- Starterset BEGC_8719.sql
--------------------------------------------------------------------
-- |----------------------------------------------------------------
-- | Product ID: GSDM
-- | Project: RMB
-- | Date: 16-Oct-2019
-- | Requested By: 
-- | Approved By: 
-- | Prepared By: Vipul R.
-- | Patch :- 8.7.1.9
-- |----------------------------------------------------------------
-- | Tables Affected: FT_BE_BEGC
-- |----------------------------------------------------------------

SET SCAN OFF;
SET DEFINE OFF;

--------------------------------------------------------------------

UPDATE FT_BE_BEGC SET CONFIG_VAL_TXT = '{"MessageType":"SD","EventName":"WORKSTATION"}' WHERE CONFIG_TYP='CalendarDefinition' AND BEGC_OID = 'GNRLCADF==';


INSERT INTO FT_T_BEGC
   (BEGC_OID, CONFIG_TYP, CONFIG_VAL_TXT, CONFIG_GRP_NME, LAST_CHG_TMS, 
    LAST_CHG_USR_ID)
 SELECT 'BEGCFRP1==', 'RMBForcedRePublish', '{"MessageType":"SD","EventName":"RMBCustomForceRePublishEvent","EventParams":{"Messages":"EOBJ"}}', 'GENERAL', SYSDATE, 
    'RMB:CSTM' FROM DUAL WHERE NOT EXISTS (SELECT 'X' FROM FT_T_BEGC WHERE CONFIG_TYP = 'RMBForcedRePublish' AND CONFIG_GRP_NME = 'GENERAL');

INSERT INTO FT_T_BEGC
   (BEGC_OID, CONFIG_TYP, CONFIG_VAL_TXT, CONFIG_GRP_NME, LAST_CHG_TMS, 
    LAST_CHG_USR_ID)
 SELECT 'BEGCCGR1==', 'RMBCustomCalendarGroupDetails', '{"MessageType":"SD","EventName":"RMBCalendarGroupCalidPublishingEvent","EventParams":{"Messages":"EOBJ"}}', 'GENERAL', SYSDATE, 
    'RMB:CSTM' FROM DUAL WHERE NOT EXISTS (SELECT 'X' FROM FT_T_BEGC WHERE CONFIG_TYP = 'RMBCustomCalendarGroupDetails' AND CONFIG_GRP_NME = 'GENERAL');

INSERT INTO FT_T_BEGC
   (BEGC_OID, CONFIG_TYP, CONFIG_VAL_TXT, CONFIG_GRP_NME, LAST_CHG_TMS, 
    LAST_CHG_USR_ID)
 SELECT 'BEGCISGR==', 'InstrumentGroup', '{"EventParams":{"Messages":"EOBJ"},"EventName":"RMBInstrumentGroupIssuePublishingEvent","MessageType":"SD"}', 'GENERAL', SYSDATE, 
    'RMB:CSTM' FROM DUAL WHERE NOT EXISTS (SELECT 'X' FROM FT_T_BEGC WHERE CONFIG_TYP = 'InstrumentGroup' AND CONFIG_GRP_NME = 'GENERAL');
