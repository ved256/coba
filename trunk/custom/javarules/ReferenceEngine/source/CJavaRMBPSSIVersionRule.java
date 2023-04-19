import com.thegoldensource.jbre.*;

//import ICBCSContactDuplicateCheck.Extended;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class CJavaRMBPSSIVersionRule implements JavaRule {

	private static String m_strRuleName = "CJavaRMBPSSIVersionRule";
	private static final Logger logger = Logger.getLogger(m_strRuleName);
	private XMLMessage m_msg = null;
	private CJavaRMBSSICommons commons = null;
	public static String getSSIOID = "SELECT SSI_OID FROM FT_T_SSIR WHERE SSIR_OID =:SSIR_OID<CHAR[11]>";
	public static String getColumnListFromCLDF = "select COL_NME from ft_t_cldf where tbl_id ='SSIS' and col_nme not in('SSI_INTERNAL_ID','SETTLE_INSTRUC_DESC')";
	public static String getColumnListFromCLDFRMBSSICustom = "select COL_NME from ft_t_cldf where tbl_id ='SSI1' and col_nme not in('BU_UNIT','COST_CNTRE','DEPT','DIVSN' , 'EMAIL_ID','TEL_NO','LAST_CHG_USR_ID','MKR_NME','RVWR_MNEM','RVWR_NME','SSI_REQ_FOR','CONT_PERSN','SSI_URG_REQ','SSI_URG_REASN','SSI_AUTH_STATUS')";
	public static String checkParentSSI = "select SSI_OID from ft_t_ssis where PRNT_SSI_OID is null and SSI_OID =:SSI_OID<CHAR[11]>";
	public static String fincChildSSI = "select SSI_OID from ft_t_ssis where PRNT_SSI_OID =:SSI_OID<CHAR[11]>";
	public static final SegmentId headerSegment = new SegmentId(0);
	private static final String SEGPROCESSEDIND = "SEGPROCESSEDIND";
	private static final String GSOLABEL = "GSOLABEL";
	private HashMap<String, String[]> segmentIgnoreList;
	public static final String[] NOTIFICATION_PARAMETERS = { "RULE_NAME", "MAIN_ENTITY_NME" };

	public boolean initialize(String[] parameters) {
		loggerDebug("Initializing rule " + m_strRuleName);
		if (null == parameters) {
			logger.debug("CJavaRMBPSSIVersionRule: Invalid rule configuration.");
			return true;

		}
		loggerDebug("parameters0" + parameters[0]);
		loggerDebug("parameters1" + parameters[1]);
		segmentIgnoreList = new HashMap<>();
		String[] ignoreList = parameters[0].split(",");
		/*
		 * Get parmerters from xmgs and split and add it in ignore list Created
		 * an Hashmap of segmentIgnoreList
		 */
		loggerDebug("ignoreList" + ignoreList);
		for (String i : ignoreList) {
			String[] temp = i.split(",");
			logger.debug(temp);

			for (String j : temp) {
				segmentIgnoreList.put(j, temp);
			}

		}
		return true;
	}

	public boolean process(XMLMessage msg, DatabaseObjectContainer dboc, ProcessorContext pContext,
			DatabaseAccess dbConnection, NotificationCreator notificationCreator) {

		commons = new CJavaRMBSSICommons(msg, dboc, pContext, dbConnection, notificationCreator);
		String SSI_OID = null;
		String SSIID = null;
		String getTableName = null;
		String SSIR_OID = null;
		String SSIS = "SSIS";
		String nextValue = null;
		ArrayList<String> getSSIValues = new ArrayList<>();
		ArrayList<String> getSSIValuesRMBSSICustom= new ArrayList<>();
		ArrayList<String> getChildSSIValues = new ArrayList<>();
		String getParentValue = null;
		ArrayList<String> addSSIOIDValues = new ArrayList<String>();
		Map<String, String> props = new HashMap<String, String>();
		String segment_nme = null;
		int flagChild = 0;
		loggerDebug("Processing Java rule " + m_strRuleName);
		logger.debug("Starting ----" + m_strRuleName);
		m_msg = msg;

		logger.debug("HEADER XML Message = '" + msg.getXMLString() + "'");

		String strMSG_CLASSIFICATION = m_msg.getStringField("MSG_CLASSIFICATION", headerSegment);

		String lastchgusr = m_msg.getStringField("LAST_CHG_USR_ID", headerSegment);

		loggerDebug("Message Classification : " + strMSG_CLASSIFICATION);

		boolean bUImessage = ("WEBMSG".equals(strMSG_CLASSIFICATION)|| "RMBSSI".equals(strMSG_CLASSIFICATION)) ? true : false;
		
		if (!bUImessage) {
			loggerDebug("Is Not UI message So Exiting the Rule");
			return true;
		}
		/*
		 * check if the street Ref contains the StandardSettlementInstructions
		 * segment if not exit
		 */
		SegmentId currentCheckSegID = new SegmentId(0);
		if (!"StandardSettlementInstructions".equals(msg.getSegmentType(currentCheckSegID))) {
			return true;
		}

		int intSegCount = msg.getSegmentCount();

		logger.debug("Printing BEFORE XML Message = '" + msg.getXMLString() + "'");
		try {
			int flag = 0;
			int flagRMBSSICustom=0;
			for (int i = 0; i < intSegCount; i++) {
				String oldValue = null;
				String checkValue = null;
				SegmentId currentSegID = new SegmentId(i);

				/*
				 * Handling for  StandardSettlementInstructions
				 * Checks if any field is modified if modified it will set the flag which will indicate that Use SSI IND field needs to set to Y for all the child records 
				 */
				if ("StandardSettlementInstructions".equals(msg.getSegmentType(currentSegID))
						&& "RMBPStandardSettlementInstruction".equals(msg.getSegmentAttribute(currentSegID, GSOLABEL))
						&& ("U".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND)))) {

					getSSIValues = commons.getFirstRowDB(getColumnListFromCLDF);
					loggerDebug("getSSIValues" + getSSIValues);
					Iterator itr = getSSIValues.iterator();
					System.out.println("The ArrayList elements are:");
					while (itr.hasNext()) {
						oldValue = msg.getFieldAttribute(itr.next().toString(), currentSegID, "OLD_VALUE");
						loggerDebug("oldValue" + oldValue);
						if (oldValue == null) {
							oldValue = "";
							loggerDebug("checkValue if" + oldValue);
						} else {
							loggerDebug("checkValue else" + oldValue);
							flag++;
							loggerDebug("flag value else" + flag);
						}

					}
					loggerDebug("flag value" + flag);
					if (flag >= 1) {
						loggerDebug("flag value inside" + flag);
						SSI_OID = msg.getStringField("SSI_OID", currentSegID);
						addSSIOIDValues.add(SSI_OID);
					}
				}
				
				else if ("RMBSSICustom".equals(msg.getSegmentType(currentSegID))
						&& ("U".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))))
				{
					getSSIValuesRMBSSICustom = commons.getFirstRowDB(getColumnListFromCLDFRMBSSICustom);
					loggerDebug("getSSIValuesRMBSSICustom" + getSSIValuesRMBSSICustom);
					Iterator itr = getSSIValuesRMBSSICustom.iterator();
					System.out.println("The ArrayList elements are:");
					while (itr.hasNext()) {
						oldValue = msg.getFieldAttribute(itr.next().toString(), currentSegID, "OLD_VALUE");
						loggerDebug("oldValue RMBSSICustom" + oldValue);
						if (oldValue == null) {
							oldValue = "";
							loggerDebug("checkValue if  RMBSSICustom" + oldValue);
						} else {
							loggerDebug("checkValue else RMBSSICustom" + oldValue);
							flagRMBSSICustom++;
							loggerDebug("flag value else RMBSSICustom" + flag);
						}

					}
					loggerDebug("flag value RMBSSICustom" + flagRMBSSICustom);
					if (flagRMBSSICustom >= 1) {
						loggerDebug("flag value inside RMBSSICustom" + flagRMBSSICustom);
						SSI_OID = msg.getStringField("SSI_OID", currentSegID);
						addSSIOIDValues.add(SSI_OID);
					}

					
				}

				/*
				 * Check if the segment is not in the ignore list and segment
				 * process indicator should be C or U i.e create or Update if
				 * found get the ssi_oid and add it in the arraylist
				 * addSSIOIDValues if not found and segment name is
				 * SSIRStandardSettlementAccount then get the SSIR_OID field and
				 * from ssir_oid derive ssi_oid after deriving ssi_oid add it in
				 * the arraylist addSSIOIDValues
				 */
				else if (flag == 0 && !segmentIgnoreList.containsKey(msg.getSegmentType(currentSegID))
						&& ("C".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))
								|| "U".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))
								|| "D".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND)))) {
					SSI_OID = msg.getStringField("SSI_OID", currentSegID);
					loggerDebug("SSI_OID" + SSI_OID);
					loggerDebug("Segmnet Type" + msg.getSegmentType(currentSegID));
					if (!segmentIgnoreList.containsKey(msg.getSegmentType(currentSegID)) && SSI_OID == null
							&& ("SSIRStandardSettlementAccount".equals(msg.getSegmentType(currentSegID))
									&& ("C".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))
											|| "U".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))
											|| "D".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))))) {
						SSIR_OID = msg.getStringField("SSIR_OID", currentSegID);
						loggerDebug("SSIR_OID" + SSIR_OID);
						// segment_nme=msg.getSegmentType(currentSegID);
						SSI_OID = commons.getFirstValDB(getSSIOID, SSIR_OID);
						if (SSI_OID == null) {
							SSI_OID = null;
						}
						loggerDebug("SSIOID from SSIR_OID" + SSI_OID);

					} 
					
					/* 
					 * StandardSettlementComment Handling
					 * check and compare if found parent it will ignore and in case of child it will add SSIOID to list 
					 */
					else if (!segmentIgnoreList.containsKey(msg.getSegmentType(currentSegID))
							&& ("StandardSettlementComment".equals(msg.getSegmentType(currentSegID))
									&& ("C".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))
											|| "U".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND)) 
											|| "D".equals(msg.getSegmentAttribute(currentSegID, SEGPROCESSEDIND))))) {

						getParentValue = null;
						getParentValue = commons.getFirstValDB(checkParentSSI, SSI_OID);
						loggerDebug("getParentValue main " + getParentValue.toString());
						if (getParentValue.isEmpty()) {
							loggerDebug("getParentValue inside if" + getParentValue);
							loggerDebug("CJavaRMBPSSIVersionRule StandardSettlementComment child segment found ");
						} else {
							loggerDebug("getParentValue inside else" + getParentValue);
							SSI_OID = null;
							loggerDebug("CJavaRMBPSSIVersionRule StandardSettlementComment parent segment found ");

						}
					}

					addSSIOIDValues.add(SSI_OID);
					loggerDebug("CJavaRMBPSSIVersionRule Added segment" + SSI_OID);
				}
			}
			loggerDebug("props" + props);
			HashSet<String> hset = new HashSet<String>(addSSIOIDValues);
			hset.remove(null);
			hset.remove("");

			loggerDebug("CJavaRMBPSSIVersionRule hset" + hset);
			Iterator value = hset.iterator();
			getParentValue = null;
			HashSet<String> hsetchildValues = new HashSet<String>();
			String valnextVal = null;
			while (value.hasNext()) {
				loggerDebug("Inside while");
				valnextVal = value.next().toString();
				loggerDebug("getParentValue" + valnextVal);
				getParentValue = commons.getFirstValDB(checkParentSSI, valnextVal);
				loggerDebug("getParentValue" + getParentValue);
				if (getParentValue.isEmpty()) {
					loggerDebug("getParentValue inside if" + getParentValue);
					flagChild++;
				} else {
					loggerDebug("getParentValue inside else" + getParentValue);
					loggerDebug("getParentValue null");
					getChildSSIValues = commons.getFirstRowDB(fincChildSSI, valnextVal);
					hsetchildValues.add(valnextVal);
					hsetchildValues.addAll(getChildSSIValues);
					hsetchildValues.remove(null);
					hsetchildValues.remove("");
					// break;
				}

			}
			loggerDebug("flagChild" + flagChild);
			loggerDebug("hsetchildValues" + hsetchildValues);
			loggerDebug("CJavaRMBPSSIVersionRule hsetchildValues" + hsetchildValues);
			if (flagChild >= 1 && hsetchildValues.isEmpty()) {
				loggerDebug("CJavaRMBPSSIVersionRule inside if flagchild");
				hsetchildValues.addAll(addSSIOIDValues);
				loggerDebug("CJavaRMBPSSIVersionRule addSSIOIDValues" + addSSIOIDValues);
				hsetchildValues.remove(null);
				hsetchildValues.remove("");
			}
			Iterator itrChildvalue = hsetchildValues.iterator();
			loggerDebug("CJavaRMBPSSIVersionRule hsetchildValues final while" + hsetchildValues);
			while (itrChildvalue.hasNext()) {

				SegmentId currentSegID = msg.addSegment(XMLMessage.A_D_UNKNOWN, "StandardSettlementInstructions");
				msg.addField("SSI_OID", currentSegID, itrChildvalue.next().toString());
				msg.addField("USE_SSI_IND", currentSegID, "Y");
				msg.addField("START_TMS", currentSegID, new Date());
				msg.addField("LAST_CHG_TMS", currentSegID, new Date());
				msg.addField("LAST_CHG_USR_ID", currentSegID, lastchgusr);

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
