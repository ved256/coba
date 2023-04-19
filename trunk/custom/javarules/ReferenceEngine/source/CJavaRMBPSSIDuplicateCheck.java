import com.thegoldensource.jbre.*;

//import ICBCSContactDuplicateCheck.Extended;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class CJavaRMBPSSIDuplicateCheck implements JavaRule {

	private static String m_strRuleName = "CJavaRMBPSSIDuplicateCheck";
	private static final Logger logger = Logger.getLogger(m_strRuleName);
	private XMLMessage m_msg = null;
	private CJavaRMBSSICommons commons = null;
	public static final SegmentId headerSegment = new SegmentId(0);

	private static final String GSOLABEL = "GSOLABEL";

	public static final String[] NOTIFICATION_PARAMETERS = { "RULE_NAME", "MAIN_ENTITY_NME" };

	public static final String SSI_DUPLICATECheck = "SELECT SSIS.SSI_OID,SSI1.SETT_CURR_CDE,SSIS.SSI_ID ,SSIR.CST_ID FROM FT_T_SSIS SSIS, FT_T_SSI1 SSI1 ,FT_T_SSIR SSIR WHERE SSIS.SSI_OID = SSI1.SSI_OID  AND SSIS.END_TMS IS NULL AND SSIS.SSI_OID = SSIR.SSI_OID AND SSI1.SETT_CURR_CDE = :SETT_CURR_CDE<CHAR[256]> and SSIR.CST_ID =:CST_ID<CHAR[11]> and SSIR.SETTLE_RL_TYP = 'COUNTERPARTY' and PRNT_SSI_OID is null AND ROWNUM=1";

	public static final String SSI_DEACTIVATECheck = "SELECT SSIS.SSI_OID,SSIS.SSI_ID,SSIR.CST_ID,SSI1.SETT_CURR_CDE FROM FT_T_SSIS SSIS, FT_T_SSIR SSIR, FT_T_SSI1 SSI1 WHERE SSIS.SSI_OID = SSIR.SSI_OID  AND SSIS.SSI_OID = SSI1.SSI_OID and SSIR.CST_ID =:CST_ID<CHAR[11]> AND SSI1.SETT_CURR_CDE = :SETT_CURR_CDE<CHAR[256]> and SSIR.SETTLE_RL_TYP = 'COUNTERPARTY' and SSIS.PRNT_SSI_OID is null and SSIS.SETTLE_INSTRUC_STAT_TYP='Inactive' and SSIS.END_TMS IS NULL";

	//public static final String GETKYCSTATUS = "select cst_stat_typ from ft_T_csta where CST_ID=:CST_ID<CHAR[11]> and cst_stat_typ in ('FROZEN','DORMANT') and rownum=1";

	// public static final String SSI_DEACTIVATECHILD_Check = "SELECT
	// SSIS.SSI_OID,SSIS.SETTLE_CURR_CDE,SSIS.SETTLE_INSTRUC_DESC,SSIS.SSI_ID
	// ,SSIS.CST_ID FROM FT_T_SSIS SSIS WHERE SSIS.SETTLE_CURR_CDE =
	// :SETTLE_CURR_CDE<CHAR[11]> and SSIS.SETTLE_INSTRUC_DESC =
	// :SETTLE_INSTRUC_DESC<CHAR[256]> and SSIS.CST_ID =:CST_ID<CHAR[11]> and
	// PRNT_SSI_OID is not null and SETTLE_INSTRUC_STAT_TYP='Inactive' ";

	public static final String generateSSID = "select SSI_SEQ.nextVal from dual";

	public boolean initialize(String[] parameters) {
		loggerDebug("Initializing rule " + m_strRuleName);
		return true;
	}

	public boolean process(XMLMessage msg, DatabaseObjectContainer dboc, ProcessorContext pContext,
			DatabaseAccess dbConnection, NotificationCreator notificationCreator) {
		commons = new CJavaRMBSSICommons(msg, dboc, pContext, dbConnection, notificationCreator);

		String settlementcurrency = null;
		String settlementDesc = null;
		String getSSIID = null;
		String settlementcurrencyChild = null;
		String settlementDescChild = null;
		String getSSIIDChild = null;
		String cstID = null;
		String SSIID = null;
		String generateSSIID = null;
		String SSIID_child = null;
		String roleType = null;
		//String getStatus = null;
		int flagDuplicate = 0;
		int flagDeactivate = 0;
		ArrayList getSSIValues = new ArrayList<>();
		ArrayList getSSIDeactivateValues = new ArrayList<>();
		ArrayList getSSIDeactivateChildValues = new ArrayList<>();
		loggerDebug("Processing Java rule " + m_strRuleName);
		logger.debug("Starting ----" + m_strRuleName);
		m_msg = msg;

		logger.debug("HEADER XML Message = '" + msg.getXMLString() + "'");

		String strMSG_CLASSIFICATION = m_msg.getStringField("MSG_CLASSIFICATION", headerSegment);

		loggerDebug("Message Classification : " + strMSG_CLASSIFICATION);

		boolean bUImessage = ("WEBMSG".equals(strMSG_CLASSIFICATION)) ? true : false;

		if (!bUImessage) {
			loggerDebug("Is Not UI message So Exiting the Rule");
			return true;
		}

		SegmentId currentCheckSegID = new SegmentId(0);
		if (!"StandardSettlementInstructions".equals(msg.getSegmentType(currentCheckSegID))) {
			return true;
		}

		int intSegCount = msg.getSegmentCount();

		logger.debug("Printing BEFORE XML Message = '" + msg.getXMLString() + "'");
		try {

			for (int i = 0; i < intSegCount; i++) {
				SegmentId currentSegID = new SegmentId(i);
				// Get Case Name from StandardSettlementInstructions segment
				if ("StandardSettlementInstructions".equals(msg.getSegmentType(currentSegID))) {

					
					settlementDesc = msg.getStringField("SETTLE_INSTRUC_DESC", currentSegID);
					SSIID = msg.getStringField("SSI_ID", currentSegID);
					if (SSIID == null) {
						generateSSIID = commons.getFirstValDB(generateSSID);
						SSIID = generateSSIID;
					} else {
						SSIID = msg.getStringField("SSI_ID", currentSegID);
					}

					logger.debug("SSIID" + SSIID);
				}

				/*
				 * Handling for Deactivate Validation check it will check for
				 * short Name Counterparty if present it will add in to
				 * arraylist and then it will compare it with exiting SSI ID if
				 * found it will set flag if nothing found it will fech the
				 * status from DB and set it in variable and check for KYC
				 * Status whether it is FORZEN or DORMANT
				 */
				/*
				 * Handling for Duplicate Validation check it will check for
				 * combination of settlement currency , case Name , short Name
				 * Counterparty if present it will add in to arraylist and then
				 * it will compare it with exiting SSI ID if found it will set
				 * flag
				 */

				//Get Currency from RMBSSICustom segment
				if ("RMBSSICustom".equals(msg.getSegmentType(currentSegID))) {
					settlementcurrency = msg.getStringField("SETT_CURR_CDE", currentSegID);
				}
				//Get CST_ID from SSISStandardSettlementInstitutionRole segment
				if ("SSISStandardSettlementInstitutionRole".equals(msg.getSegmentType(currentSegID))) {
					logger.debug(" Inside SSISStandardSettlementInstitutionRole" + SSIID);
					roleType = msg.getStringField("SETTLE_RL_TYP", currentSegID);
					logger.debug("roleType" + roleType);

					cstID = msg.getStringField("CST_ID", currentSegID);

					logger.debug(" SSISStandardSettlementInstitutionRole CST_ID" + SSIID);
					logger.debug("SSIID" + SSIID);
					logger.debug("CST_ID" + cstID);
					logger.debug("settlementcurrency" + settlementcurrency);
					logger.debug("settlementDesc" + settlementDesc);

					logger.debug("roleType compare" + roleType);
					if ("COUNTERPARTY".equals(roleType)) {
						try {
							if (cstID != null) {
								getSSIDeactivateValues = commons.getFirstRowDB(SSI_DEACTIVATECheck, cstID,settlementcurrency,3);
								logger.debug("getSSIDeactivateValues" + getSSIDeactivateValues);

								// check for Deactivate SSI present

								if (!getSSIDeactivateValues.isEmpty()) {
									getSSIID = getSSIDeactivateValues.get(1).toString();
									logger.debug("getSSIID" + getSSIID);
								} else {
									logger.debug("Inside else COUNTERPARTY" + cstID);
									getSSIDeactivateValues = new ArrayList<>();
									//getStatus = commons.getFirstValDB(GETKYCSTATUS, cstID);
									//logger.debug("getStatus" + getStatus);
								}
								if (getSSIDeactivateValues.isEmpty()) {

									try {
										if (settlementcurrency != null && settlementDesc != null) {
											getSSIValues = commons.getFirstRowDB(SSI_DUPLICATECheck, settlementcurrency,
													 cstID, 3);
											logger.debug("getSSIValues" + getSSIValues);
											if (!getSSIValues.isEmpty()) {
												getSSIID = getSSIValues.get(2).toString();
												logger.debug("getSSIID" + getSSIID);
											} else {
												getSSIValues = new ArrayList<>();
											}
										}

									} catch (Exception e) {
										logger.debug("SQL query failed, message = '" + e.getMessage() + "'");
										logger.debug(e);
									} finally {
										dbConnection.close();
									}
									logger.debug("started compare");
									if (SSIID.equals(getSSIID)) {
										flagDuplicate++;
										// return true;
									}

								}
							}
						} catch (Exception e) {
							logger.debug("SQL query failed, message = '" + e.getMessage() + "'");
							logger.debug(e);
						} finally {
							dbConnection.close();
						}
						logger.debug("started compare");
						if (SSIID.equals(getSSIID)) {
							// return true;
							flagDeactivate++;
						} 
					}

				}

				/*
				 * else if("RMBPStandardSettlementInstruction.Acbs".equals(msg.
				 * getSegmentAttribute(currentSegID, GSOLABEL))) {
				 * 
				 * settlementcurrencyChild =
				 * msg.getStringField("SETTLE_CURR_CDE", currentSegID);
				 * settlementDescChild =
				 * msg.getStringField("SETTLE_INSTRUC_DESC", currentSegID);
				 * cstID = msg.getStringField("CST_ID", currentSegID);
				 * SSIID_child = msg.getStringField("SSI_ID", currentSegID);
				 * if(SSIID_child==null) { generateSSIID =
				 * commons.getFirstValDB(generateSSID);
				 * SSIID_child=generateSSIID; } else { SSIID_child =
				 * msg.getStringField("SSI_ID", currentSegID); }
				 * 
				 * logger.debug("SSIID_child" + SSIID_child);
				 * 
				 * if (settlementcurrency != null && settlementDesc != null) {
				 * try { if (settlementcurrency != null && settlementDesc !=
				 * null) { getSSIDeactivateChildValues =
				 * commons.getFirstRowDB(SSI_DEACTIVATECHILD_Check,
				 * settlementcurrency, settlementDesc, cstID, 4);
				 * logger.debug("getSSIDeactivateValues" +
				 * getSSIDeactivateChildValues);
				 * if(!getSSIDeactivateChildValues.isEmpty()) { getSSIIDChild =
				 * getSSIDeactivateChildValues.get(3).toString();
				 * logger.debug("getSSIID" + getSSIIDChild); }
				 * 
				 * } } catch (Exception e) {
				 * logger.debug("SQL query failed, message = '" + e.getMessage()
				 * + "'"); logger.debug(e); } finally { dbConnection.close(); }
				 * logger.debug("started compare"); if
				 * (SSIID.equals(getSSIIDChild)) { return true; } else if
				 * (!getSSIDeactivateChildValues.isEmpty()) { logger.
				 * debug("CJavaRMBPSSIDuplicateCheck getSSIDeactivateChildValues"
				 * + getSSIDeactivateChildValues); String[]
				 * arrOfNotificationParameterValues = { "SSIDeActivateCheck",
				 * getSSIIDChild }; notificationCreator.raiseException(60098,
				 * NOTIFICATION_PARAMETERS, arrOfNotificationParameterValues); }
				 * 
				 * }
				 * 
				 * 
				 * }
				 */

			}

			if (!getSSIDeactivateValues.isEmpty() && flagDeactivate == 0) {
				logger.debug("CJavaRMBPSSIDuplicateCheck getSSIDeactivateValues" + getSSIDeactivateValues);
				String[] arrOfNotificationParameterValues = { "SSIDeActivateCheck", getSSIID };
				notificationCreator.raiseException(60098, NOTIFICATION_PARAMETERS, arrOfNotificationParameterValues);
			} //else if ("FROZEN".equals(getStatus) || "DORMANT".equals(getStatus)) {
				//logger.debug("CJavaRMBPSSIDuplicateCheck getSSIDeactivateValues" + getSSIDeactivateValues);
				//String[] arrOfNotificationParameterValues = { "SSIDeActivateCheck", getSSIID };
				//notificationCreator.raiseException(60097, NOTIFICATION_PARAMETERS, arrOfNotificationParameterValues);}
			else if (!getSSIValues.isEmpty() && flagDuplicate == 0) {
				logger.debug("CJavaRMBPSSIDuplicateCheck getSSIValues" + getSSIValues);
				String[] arrOfNotificationParameterValues = { "SSIDuplicateCheck", getSSIID };
				notificationCreator.raiseException(60099, NOTIFICATION_PARAMETERS, arrOfNotificationParameterValues);
			}

		} catch (Exception e) {
			logger.error("SQL query failed, message = '" + e.getMessage() + "'");
			logger.debug(e);
		}
		logger.debug("Printing AFTER XML Message = '" + msg.getXMLString() + "'");

		return true;
	}

	public void loggerDebug(String msg) {
		if (logger.isDebugEnabled()) {
			logger.debug(msg);
		}
	}
}
