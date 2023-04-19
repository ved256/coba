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

public class CJavaRMBSSIStatusRule implements JavaRule {

	private static String m_strRuleName = "CJavaRMBPSSIVersionRule";
	private static final Logger logger = Logger.getLogger(m_strRuleName);
	private XMLMessage m_msg = null;  
	private CJavaRMBSSICommons commons = null;
	public static final String GET_SSI_REQUEST_STATUS_TYPE = "SELECT SETTLE_INSTRUC_DESC,SETTLE_INSTRUC_STAT_TYP FROM FT_T_SSIS WHERE SSI_OID =:SSI_OID<CHAR[11]>";
	public static final String GET_SSI_CHILD_REQUEST_STATUS_TYPE = "SELECT SETTLE_INSTRUC_DESC,SETTLE_INSTRUC_STAT_TYP FROM FT_T_SSIS WHERE SSI_OID =:child_SSI_OID<CHAR[11]>";
	public static final String GET_CHILDS_SSI_OID = "SELECT SSI_OID FROM FT_T_SSIS WHERE PRNT_SSI_OID =:SSI_OID<CHAR[11]> and PRNT_SSI_OID is not null";
	public static final SegmentId headerSegment = new SegmentId(0);
	private static final String SEGPROCESSEDIND = "SEGPROCESSEDIND";
	private static final String GSOLABEL = "GSOLABEL";
	private HashMap<String, String[]> segmentIgnoreList;
	public static final String[] NOTIFICATION_PARAMETERS = { "RULE_NAME", "MAIN_ENTITY_NME" };

	public boolean initialize(String[] parameters) {
		loggerDebug("Initializing rule " + m_strRuleName);
		return true;
	}   
	
	
	public boolean process(XMLMessage msg, DatabaseObjectContainer dboc, ProcessorContext pContext,
			DatabaseAccess dbConnection, NotificationCreator notificationCreator) {

		commons = new CJavaRMBSSICommons(msg, dboc, pContext, dbConnection, notificationCreator);
		String SSI_OID = null;
		String child_SSI_OID = null;
		String SSIID = null;
		String deactivateSSI_oid=null;
		String getParentStatus = null;
		String parentStatus = null;
		String getChildStatus=null;
		String getParentReuestTypeFromDB = null;
		String getChildReuestTypeFromDB = null;
		String parentRequestType=null;
		String childRequestType=null;
		String SSIS = "SSIS";
		String nextValue = null;
		ArrayList<String> getParentReuestTypeFromDBList = new ArrayList<>();
		ArrayList<String> getChildListFromDB= new ArrayList<>();
		ArrayList<String> getChildReuestTypeFromDBList= new ArrayList<>();
		ArrayList<String> getChildSSIValues = new ArrayList<>();
		String getParentValue = null;
		ArrayList<String> addSSIOIDValues = new ArrayList<String>();
		ArrayList<String> addDeactiveSSIOIDValues = new ArrayList<String>();
		Map<String, String> props = new HashMap<String, String>();
		String segment_nme = null;
		int deactivateSSIFlag = 0;
		loggerDebug("Processing Java rule " + m_strRuleName);
		logger.debug("Starting ----" + m_strRuleName);
		m_msg = msg;

		logger.debug("HEADER XML Message = '" + msg.getXMLString() + "'");

		String strMSG_CLASSIFICATION = m_msg.getStringField("MSG_CLASSIFICATION", headerSegment);

		String lastchgusr = m_msg.getStringField("LAST_CHG_USR_ID", headerSegment);

		loggerDebug("Message Classification : " + strMSG_CLASSIFICATION);

		boolean bUImessage = ("WEBMSG".equals(strMSG_CLASSIFICATION)) ? true : false;

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

			for (int i = 0; i < intSegCount; i++) {
				SegmentId currentSegID = new SegmentId(i);
				
				/*
				 * Handling for  StandardSettlementInstructions
				 * gets the Request Type Field at Parent level and store it
				 * Fetch Request Type and status from database and store it
				 * Fetch the child SSIS OIDS from database and store it in the ArrrayList
				 * @param ssi_oid   
				 */
				
				if ("StandardSettlementInstructions".equals(msg.getSegmentType(currentSegID)) && "RMBPStandardSettlementInstruction".equals(msg.getSegmentAttribute(currentSegID, GSOLABEL))) {
					SSI_OID = msg.getStringField("SSI_OID", currentSegID);
					getParentReuestTypeFromDBList = commons.getFirstRowDB(GET_SSI_REQUEST_STATUS_TYPE, SSI_OID, 2);
					logger.debug("getParentReuestTypeFromDBList" + getParentReuestTypeFromDBList);
					if (!getParentReuestTypeFromDBList.isEmpty()) {
						logger.debug("Inside parent if ");
						getParentReuestTypeFromDB = getParentReuestTypeFromDBList.get(0).toString();
						logger.debug("getParentReuestTypeFromDB-----" + getParentReuestTypeFromDB);
						getParentStatus= getParentReuestTypeFromDBList.get(1).toString();
						logger.debug("getParentStatus" + getParentStatus);
						if(getParentStatus==null)
						{
							logger.debug("Inside getParentStatus null");
							parentStatus=msg.getStringField("SETTLE_INSTRUC_STAT_TYP", currentSegID);
						}
						
						
					} 

					logger.debug("getParentReuestTypeFromDB" + getParentReuestTypeFromDB);
					parentRequestType = msg.getStringField("SETTLE_INSTRUC_DESC", currentSegID);
					parentStatus=msg.getStringField("SETTLE_INSTRUC_STAT_TYP", currentSegID);
					
					getChildListFromDB = commons.getFirstRowDB(GET_CHILDS_SSI_OID, SSI_OID);
					if (getChildListFromDB.isEmpty()) {
						logger.debug("No childs found ");
					} 
				
				}
				child_SSI_OID=null;

				/*
				 * Handling for  StandardSettlementInstructions Child Level
				 * gets the Request Type Field at Child level and store it 
				 * Fetch Request Type and status from database and store it
				 * @param  child_SSI_OID
				 */
				if (!getChildListFromDB.isEmpty()) {
					
					/*
					 * If there is only change in parent segment 
					 */
					if("RMBPStandardSettlementInstruction".equals(msg.getSegmentAttribute(currentSegID, GSOLABEL)))
					{
						child_SSI_OID=null;
						childRequestType=null;
					}
					/*
					 * If there is a change in child segment 
					 */
					else
					{
						child_SSI_OID = msg.getStringField("SSI_OID", currentSegID);
						childRequestType = msg.getStringField("SETTLE_INSTRUC_DESC", currentSegID);
					}
					/*
					 * If no child segment found then it will iterate through the chid SSI_OID list and checks for the desired condition for Activate , Deactivate , Re-Activate   
					 */
					if(child_SSI_OID==null)
					{
						Iterator itr = getChildListFromDB.iterator();
						while (itr.hasNext())
				        {
							child_SSI_OID = itr.next().toString();
							
							getChildReuestTypeFromDBList = commons.getFirstRowDB(GET_SSI_CHILD_REQUEST_STATUS_TYPE, child_SSI_OID,2);
							logger.debug("getChildReuestTypeFromDBList" + getChildReuestTypeFromDBList);
							if (!getChildReuestTypeFromDBList.isEmpty()) {
								getChildReuestTypeFromDB = getChildReuestTypeFromDBList.get(0).toString();
								logger.debug("getChildReuestTypeFromDBList" + getParentReuestTypeFromDB);
							} 
							getChildStatus= getChildReuestTypeFromDBList.get(1).toString();
							if(getChildStatus==null)
							{
								logger.debug("Inside getChildStatus null");
								
							}
							
							
						
							//addSSIOIDValues.add(SSI_OID);
							logger.debug("childRequestType"+childRequestType);
							logger.debug("child_SSI_OID"+child_SSI_OID);
						
							logger.debug("Before actual compare logic values");
							logger.debug("parentRequestType"+parentRequestType);
							logger.debug("getParentReuestTypeFromDB"+getParentReuestTypeFromDB);
							logger.debug("childRequestType"+childRequestType);
							logger.debug("getChildReuestTypeFromDB"+getChildReuestTypeFromDB);
							/*
							 * Handling for  Activate SSI Status
							 * if below condition satisfied it will add SSI_oid of both parent and child level to addSSIOIDValues arrayList 
							 */
						if(("New SSI".equalsIgnoreCase(parentRequestType)||"New SSI".equalsIgnoreCase(getParentReuestTypeFromDB) || parentRequestType.startsWith("Amend SSI", 0)|| getParentReuestTypeFromDB.startsWith("Amend SSI", 0)) && ("Late Settlements".equals(childRequestType)||"Late Settlements".equals(getChildReuestTypeFromDB) || "Third Party".equalsIgnoreCase(childRequestType)|| "Third Party".equalsIgnoreCase(getChildReuestTypeFromDB) ||  "New SSI".equalsIgnoreCase(childRequestType)|| "New SSI".equalsIgnoreCase(getChildReuestTypeFromDB)))
						{
							logger.debug("Inside Activate SSI Status");
							addSSIOIDValues.add(SSI_OID);
							addSSIOIDValues.add(child_SSI_OID);
							logger.debug("addSSIOIDValues"+addSSIOIDValues);
						}
						

						/*
						 * Handling for   De-Activate SSI Status
						 * if below condition satisfied it will add SSI_oid of child level to addDeactiveSSIOIDValues arrayList  
						 */
						
						if((parentRequestType.startsWith("Amend SSI", 0)|| getParentReuestTypeFromDB.startsWith("Amend SSI", 0)) && ("De-activate SSI".equals(childRequestType))||"De-activate SSI".equals(getChildReuestTypeFromDB))
						{
							logger.debug("Inside DE-Activate SSI Status");
							addDeactiveSSIOIDValues.add(child_SSI_OID);
							logger.debug("addDeactiveSSIOIDValues"+addDeactiveSSIOIDValues);
						}
						
						/*
						 * Handling for  Re-Activate SSI Status
						 * if below condition satisfied it will add SSI_oid of child level to addSSIOIDValues arrayList 
						 */
						
						if((parentRequestType.startsWith("Amend SSI", 0)|| getParentReuestTypeFromDB.startsWith("Amend SSI", 0)) && ( "Active".equals(parentStatus)||"Active".equals(getParentStatus)) && ("Activate SSI".equals(childRequestType)||"Activate SSI".equals(getChildReuestTypeFromDB)))
						{
							logger.debug("Inside RE-Activate SSI Status");
							addSSIOIDValues.add(child_SSI_OID);
							logger.debug("addSSIOIDValues"+addSSIOIDValues);
						}

							 
							
				        }
						getChildListFromDB.clear();
					}
					else
					{
					getChildReuestTypeFromDBList = commons.getFirstRowDB(GET_SSI_CHILD_REQUEST_STATUS_TYPE, child_SSI_OID,2);
					logger.debug("getChildReuestTypeFromDBList" + getChildReuestTypeFromDBList);
					if (!getChildReuestTypeFromDBList.isEmpty()) {
						getChildReuestTypeFromDB = getChildReuestTypeFromDBList.get(0).toString();
						logger.debug("getChildReuestTypeFromDBList" + getParentReuestTypeFromDB);
					} 
					getChildStatus= getChildReuestTypeFromDBList.get(1).toString();
					if(getChildStatus==null)
					{
						logger.debug("Inside getChildStatus null");
						
					}
					
					
					childRequestType = msg.getStringField("SETTLE_INSTRUC_DESC", currentSegID);
					//addSSIOIDValues.add(SSI_OID);
					logger.debug("childRequestType"+childRequestType);
					logger.debug("child_SSI_OID"+child_SSI_OID);
				

					/*
					 * Handling for  Activate SSI Status
					 * if below condition satisfied it will add SSI_oid of both parent and child level to addSSIOIDValues arrayList 
					 */
				if(("New SSI".equalsIgnoreCase(parentRequestType)||"New SSI".equalsIgnoreCase(getParentReuestTypeFromDB) ||parentRequestType.startsWith("Amend SSI", 0)|| getParentReuestTypeFromDB.startsWith("Amend SSI", 0)) && ("Late Settlements".equals(childRequestType)||"Late Settlements".equals(getChildReuestTypeFromDB) || "Third Party".equalsIgnoreCase(childRequestType)|| "Third Party".equalsIgnoreCase(getChildReuestTypeFromDB)||  "New SSI".equalsIgnoreCase(childRequestType)|| "New SSI".equalsIgnoreCase(getChildReuestTypeFromDB)))
				{
					logger.debug("Inside Activate SSI Status");
					addSSIOIDValues.add(SSI_OID);
					addSSIOIDValues.add(child_SSI_OID);
					logger.debug("addSSIOIDValues"+addSSIOIDValues);
				}
				

				/*
				 * Handling for   De-Activate SSI Status
				 * if below condition satisfied it will add SSI_oid of child level to addDeactiveSSIOIDValues arrayList   
				 */
				
				if((parentRequestType.startsWith("Amend SSI", 0)|| getParentReuestTypeFromDB.startsWith("Amend SSI", 0)) && ("De-activate SSI".equals(childRequestType))||"De-activate SSI".equals(getChildReuestTypeFromDB))
				{
					logger.debug("Inside DE-Activate SSI Status");
					//addSSIOIDValues.add(SSI_OID);
					addDeactiveSSIOIDValues.add(child_SSI_OID);
					logger.debug("addDeactiveSSIOIDValues"+addDeactiveSSIOIDValues);
				}
				
				/*
				 * Handling for  Re-Activate SSI Status
				 * if below condition satisfied it will add SSI_oid of child level to addSSIOIDValues arrayList 
				 */
				
				if((parentRequestType.startsWith("Amend SSI", 0)|| getParentReuestTypeFromDB.startsWith("Amend SSI", 0)) && ( "Active".equals(parentStatus)||"Active".equals(getParentStatus)) && ("Activate SSI".equals(childRequestType)||"Activate SSI".equals(getChildReuestTypeFromDB)))
				{
					logger.debug("Inside RE-Activate SSI Status");
					//addSSIOIDValues.add(SSI_OID);
					addSSIOIDValues.add(child_SSI_OID);
					logger.debug("addSSIOIDValues"+addSSIOIDValues);
				}
				}
				}
			}
			
			// Handling for Activate - Re-Activate SSIs
			
			HashSet<String> hset = new HashSet<String>(addSSIOIDValues);
			hset.remove(null);
			hset.remove("");
			Iterator value = hset.iterator();
			getParentValue = null;
			HashSet<String> hsetchildValues = new HashSet<String>();
			String nextval = null;
			loggerDebug("CJavaRMBSSIStatusRule hset" + hset);
			while (value.hasNext()) {

				SegmentId currentSegID = msg.addSegment(XMLMessage.A_D_UNKNOWN, "StandardSettlementInstructions");
				nextval=value.next().toString();
				msg.addField("SSI_OID", currentSegID,nextval );
				msg.addField("SETTLE_INSTRUC_STAT_TYP", currentSegID, "Active");
				msg.addField("START_TMS", currentSegID, new Date());
				msg.addField("LAST_CHG_TMS", currentSegID, new Date());
				msg.addField("LAST_CHG_USR_ID", currentSegID, lastchgusr);

			}
			
			//Handling for De-Activate SSI's
			
			HashSet<String> hsetdeactive = new HashSet<String>(addDeactiveSSIOIDValues);
			hsetdeactive.remove(null);
			hsetdeactive.remove("");
			Iterator valueDeactive = hsetdeactive.iterator();
			getParentValue = null;
			String valnextValDeactive = null;
			loggerDebug("CJavaRMBSSIStatusRule hsetdeactive" + hsetdeactive);
			while (valueDeactive.hasNext()) {

				SegmentId currentSegID = msg.addSegment(XMLMessage.A_D_UNKNOWN, "StandardSettlementInstructions");
				valnextValDeactive=valueDeactive.next().toString();
				msg.addField("SSI_OID", currentSegID,valnextValDeactive );
				msg.addField("SETTLE_INSTRUC_STAT_TYP", currentSegID, "Inactive");
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
