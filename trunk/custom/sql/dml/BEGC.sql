--------------------------------------------------------------------
-- StarterSet BEGC.sql
--------------------------------------------------------------------
-- |----------------------------------------------------------------
-- | Product ID: GSDM
-- | Project: RMB
-- | Date: 20-Apr-2018
-- | Requested By: 
-- | Approved By: 
-- | Prepared By: Hardik S.
-- |----------------------------------------------------------------
-- | Tables Affected: BEGC
-- |----------------------------------------------------------------

SET SCAN OFF;
SET DEFINE OFF;

--------------------------------------------------------------------

INSERT INTO ft_be_begc (begc_oid, config_typ, config_grp_nme, last_chg_tms, last_chg_usr_id, config_val_txt)
  SELECT 'begc000016', 'CLIENT_NAMESPACE', 'CUSTOM', SYSDATE, 'RMB:CSTM', 'RMB'
    FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ft_be_begc WHERE begc_oid = 'begc000016');
	
	
Update ft_be_begc set config_grp_nme = 'HOTDEPLOY1'	 where config_typ = 'EXTR' ;
