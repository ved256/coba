import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.text.Segment;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.thegoldensource.jbre.DatabaseAccess;
import com.thegoldensource.jbre.DatabaseObjectContainer;
import com.thegoldensource.jbre.GSException;
import com.thegoldensource.jbre.NotificationCreator;
import com.thegoldensource.jbre.ProcessorContext;
import com.thegoldensource.jbre.SegmentId;
import com.thegoldensource.jbre.XMLMessage;


public class CJavaRMBSSICommons {
	private static Logger logger = Logger.getLogger("CJavaRMBCommons");
	
	private XMLMessage msg = null;
	private DatabaseObjectContainer dboc = null;
	private ProcessorContext pContext = null;
	private DatabaseAccess dbConnection = null;
	
	private NotificationCreator notificationCreator = null;
	private List<SegmentId> segIdsISID = new ArrayList<SegmentId>();
	
	public CJavaRMBSSICommons(XMLMessage msg,
			         DatabaseObjectContainer dboc,
			         ProcessorContext pContext,
			         DatabaseAccess dbConnection,
			         NotificationCreator notificationCreator) {
		this.msg = msg;
		this.dboc = dboc;
		this.pContext = pContext;
		this.dbConnection = dbConnection;
		this.notificationCreator = notificationCreator;
	}
	
	
	
	public DatabaseAccess getDbConnection() {
		return dbConnection;
	}


	/**
	 * Get Segments of a specific Type
	 * @param msg XMLMessage
	 * @param segType Type of segement e.g. Issue, IssueIdentifier 
	 * @return A list of SegmentId objects
	 */
	public List<SegmentId> getSegmentIds(String segType) {
		List<SegmentId> segIdList = new ArrayList<SegmentId>();
		for (int i=0; i<msg.getSegmentCount(); i++) {
			SegmentId segId = new SegmentId(i);			
			if (msg.getSegmentType(segId).equals(segType)) {
				segIdList.add(segId);
			}
		}		
		return segIdList;	
	}
	
	public SegmentId getSegmentId(String segType) {
		List<SegmentId> segIdList = getSegmentIds(segType);
		if (segIdList.size()>0){
			return segIdList.get(0);
		}else {
			return null;
		}		
	}
	
	public SegmentId getSegmentId(String segType, String filterColNme, String filterColVal) {
		List<SegmentId> segIdList = getSegmentIds(segType, filterColNme, filterColVal);
		if (segIdList.size()>0){
			return segIdList.get(0);
		}else {
			return null;
		}		
	}
	
	
	public List<SegmentId> getSegmentIds(String segType, String filterColNme, String filterColVal) {
		List<SegmentId> segIdList = new ArrayList<SegmentId>();
		for (int i=0; i<msg.getSegmentCount(); i++) {
			SegmentId segId = new SegmentId(i);			
			if (msg.getSegmentType(segId).equals(segType)) {
				
				if (filterColNme!=null && !"".equalsIgnoreCase(filterColNme) && filterColVal!=null && !"".equals(filterColVal)) {
					String	fColNameStr = msg.getStringField(filterColNme, segId);
					
					if (fColNameStr == null || "".equals(fColNameStr) ){
						fColNameStr = getKeyColFromOID (segId, filterColNme);
					}
					if (fColNameStr==null){
						continue;
					}
					if ( filterColVal.equalsIgnoreCase(fColNameStr) ) {
						segIdList.add(segId);	
					}					
				}else {
					segIdList.add(segId);	
				}
			}
		}		
		return segIdList;	
	}
	
	
	
	
	
	/**
	 * Returns the message classification from the header
	 * @param msg
	 * @return
	 */
	public String getMsgClassification() {
		
		String classification = msg.getStringField("MSG_CLASSIFICATION", new SegmentId(0));
		if (classification == null){
			return "";
		}
		
		return classification;
	}
	
	/**
	 * Returns the data_src_id from the header/0th segment
	 * @param msg
	 * @return
	 */
	public String getMsgDataSourceID() {
		
		String strDataSrcID = msg.getStringField("DATA_SRC_ID", new SegmentId(0));
		if (strDataSrcID == null)
			strDataSrcID = new String("");
		
		return strDataSrcID;
	}
	
	/**
	 * Check if the given message is a message from the Web Screens
	 * @return true if a screen message, false if it is a vendor message
	 */
	public boolean isScreenMessage() {
		if ("WEBMSG".equals(getMsgClassification())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the main segemnet type e.g. Issue, Issuer, FinancialInstituion,...
	 * @return main segment type
	 */
	public String getMainEntityType() {
		String entityTableType = msg.getStringField("MAIN_ENTITY_TBL_TYP", new SegmentId(0));
		if (entityTableType == null)
			entityTableType = new String("");

		return entityTableType;
	}

	/**
	 * Returns if the message is "manually" overriden 
	 * @param msg
	 * @return
	 */
	public boolean isMsgOverride() {
		String value = msg.getStringField("DATA_ID", new SegmentId(0));
		if (value != null && "Y".equalsIgnoreCase(value)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return a Identifier with a given context if the identifier is in the message
	 * @param msg XmlMessage
	 * @param idCtxt Context
	 * @return IssueIdentifier or an empty string if there was no identifier in the message with this context
	 */	
	public String getMsgIssueIdentifier(String idCtxt) {

		if (segIdsISID.isEmpty()){
			segIdsISID = getSegmentIds("IssueIdentifier");
		}
		String id=null;
		String currentCtxt = null;
		
		for (SegmentId segId : segIdsISID) {		
			//currentCtxt = msg.Field("ID_CTXT_TYP", segId);
			if (currentCtxt != null && idCtxt.equals(currentCtxt)) {
				//id = msg.Field("ISS_ID", segId);
				logger.debug(idCtxt + "=" + id);	
				if (id!=null){
					return id;					
				} else {
					return "";
				}
			}
		}
		logger.debug(idCtxt + "=");		
		return "";
	}
	public SegmentId getMsgIssueIdentifierSegment(String idCtxt) {
		if (segIdsISID.isEmpty()){
			segIdsISID = getSegmentIds("IssueIdentifier");
		}
		String currentCtxt = null;

		for (SegmentId segId : segIdsISID) {
			//currentCtxt = msg.Field("ID_CTXT_TYP", segId);
			if (currentCtxt != null && idCtxt.equals(currentCtxt)) {
				return segId;
			}
		}
		logger.debug(idCtxt + "=");
		return null;
	}

	
	public String getDBInstrumentIdentifier(String id, String idCtxt) {
		String instrId = new String("");
		String strDataSrcID = new String("");
		String sql = "SELECT instr_id" +
				     "  FROM ft_t_isid " +
				     " WHERE id_ctxt_typ = :p_id_ctxt_typ<char[21]>" +
				     "   AND (iss_id = :p_iss_id<char[101]>)" +				     
				     "   AND start_tms <= SYSDATE AND (end_tms IS NULL OR end_tms > SYSDATE)";		

		
		if( pContext.isRunningInVDDBMode() )
		{
			strDataSrcID = getMsgDataSourceID();
			if( strDataSrcID == null )
				strDataSrcID = "";
			
			sql += " AND data_src_id = :p_data_src_id<char[41]>"; 
		}
		
		try {
			logger.debug("Executing: " + sql);

			dbConnection.setSQL(sql);
			dbConnection.addParameter(idCtxt);
			dbConnection.addParameter(id);

			if( pContext.isRunningInVDDBMode() )
				dbConnection.addParameter(strDataSrcID);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
				return "";
			}

			// Only fetch once, get the first result in case there are more
			if (!dbConnection.isEndOfStream()) {
				instrId = dbConnection.getNextString().trim();
			}

		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 
		
		if (instrId.equals(""))
			logger.debug("getDBInstrumentIdentifier - Instrument NOT found - INSTR_ID=" + instrId + ", ID_CTXT_TYP=" + idCtxt + ", ISS_ID=" + id );
		else
			logger.debug("getDBInstrumentIdentifier - Instrument found - INSTR_ID=" + instrId + ", ID_CTXT_TYP=" + idCtxt + ", ISS_ID=" + id );
		
		return instrId;		
	}	
	
	
	public String getMsgIssueTyp() {
		String issueTyp = null;
		SegmentId segId = new SegmentId(0); 
		if (!isScreenMessage()){
			segId = new SegmentId(0);
		} else {
			for (int i=0; i<msg.getSegmentCount(); i++) {
				if ("Issue".equalsIgnoreCase(msg.getSegmentType(new SegmentId(i)))) {
					segId = new SegmentId(i);				
				}
			}		
		}

		logger.debug("Trying to determine IssueTyp...");		
		//issueTyp = msg.Field("ISS_TYP", segId);			
		logger.debug("IssueTyp=" + issueTyp);
		if(issueTyp!=null){
			return issueTyp;
		} else {
			return "";
		}
	}	
	public Date getDateFromMsg(String segmentNme,String col_nme)
	{
		Date ret=null;
		if (segmentNme==null || "".equals(segmentNme)){
			return null;
		}
		List<SegmentId> seg = getSegmentIds(segmentNme);
		if(seg==null || seg.size() == 0)
			return null;
		else
		{	
			if (! (getSegmentProcessInd(seg.get(0)).equals("D") || getSegmentProcessInd(seg.get(0)).equals("P")) ){
				ret=msg.getDateTimeField(col_nme,seg.get(0));
			}
		}
		if(ret!=null)
			return ret;
		else
			return null;
	}
	public Date getDateFromMsgWithFilter(String segmentNme,String colNme, String filterColNme, String filterColVal)
	{
		Date rtDte=null;
		if (segmentNme==null || "".equals(segmentNme)){
			return null;
		}
		List<SegmentId> segList = getSegmentIds(segmentNme);
		if(segList==null || segList.size() == 0)
			return null;
		else
		{
			if (filterColNme!=null && !"".equalsIgnoreCase(filterColNme) && filterColVal!=null && !"".equals(filterColVal)) {
				for (SegmentId segId : segList ) {
					if (getSegmentProcessInd(segId).equals("D") || getSegmentProcessInd(segId).equals("P")){
						continue;
					}
					
					String	fColNameStr = msg.getStringField(filterColNme, segId);
					
					if (fColNameStr == null || "".equals(fColNameStr) ){
						fColNameStr = getKeyColFromOID (segId, filterColNme);
					}
					if (fColNameStr==null){
						continue;
					}
					if ( filterColVal.equalsIgnoreCase(fColNameStr) ) {
						rtDte=msg.getDateTimeField(colNme,segId);
							if (rtDte!=null ) {
							break; }	
					}
					
				}
			}else {
				if (! (getSegmentProcessInd(segList.get(0)).equals("D") || getSegmentProcessInd(segList.get(0)).equals("P")) ){
					rtDte=msg.getDateTimeField(colNme,segList.get(0));
				}
			}
		}
			return rtDte;
	}
	
	public String getStringFromMsgWithFilter(String segmentNme,String colNme, String filterColNme, String filterColVal)
	{
		String rtStr=null;
		List<SegmentId> segList = getSegmentIds(segmentNme);
		if(segList==null || segList.size() == 0){
			return null;}
		else
		{
			if (filterColNme!=null && !"".equalsIgnoreCase(filterColNme) && filterColVal!=null && !"".equals(filterColVal)) {
				for (SegmentId segId : segList ) {
					if (getSegmentProcessInd(segId).equals("D") || getSegmentProcessInd(segId).equals("P")){
						continue;
					}
					String	fColNameStr = msg.getStringField(filterColNme, segId);
					
					if (fColNameStr == null || "".equals(fColNameStr) ){
						fColNameStr = getKeyColFromOID (segId, filterColNme);
					}
					if (fColNameStr==null){
						continue;
					}
					if ( filterColVal.equalsIgnoreCase(fColNameStr) ) {
						rtStr=msg.getStringField(colNme,segId);		
							if (rtStr!=null ) {
							break; }	
					}					
				}
			}else {
				if (! (getSegmentProcessInd(segList.get(0)).equals("D") || getSegmentProcessInd(segList.get(0)).equals("P")) ){
					rtStr=msg.getStringField(colNme,segList.get(0));
				}
				
			}
		}

		return rtStr;
	}
	
	public String getCLSFOidFromCLValue (String indusClSetId, String clValue){
		return getFirstValDB("SELECT CLSF_OID FROM FT_T_INCL WHERE START_TMS < SYSDATE AND (END_TMS IS NULL OR END_TMS > SYSDATE) AND INDUS_CL_SET_ID='"+indusClSetId+"' AND CL_VALUE ='"+clValue+"'");
	}
	
	public void addIACLSegmentToMsg(String iacl_oid,String issact_id,String indus_cl_set_id,String clsf_oid, String cl_value,String user_id,Date start_tms,Date end_tms, boolean useMatchKey)
	{
		SegmentId ibq1SegId = msg.addSegment(XMLMessage.A_D_UNKNOWN, "IADCIssueActionClassification");
				if(useMatchKey)		{			msg.setSegmentAttribute(ibq1SegId, "MATCH", "IACL_MATCH_SET_PURP");		}		if(iacl_oid!=null)
			msg.addField("IACL_OID", ibq1SegId, iacl_oid);
		
		if (issact_id != null) {			
			msg.addField("ISSACT_ID", ibq1SegId, issact_id); }
		
		if (indus_cl_set_id != null) {
		msg.addField("INDUS_CL_SET_ID", ibq1SegId, indus_cl_set_id); }
		
		if (clsf_oid != null) {
		msg.addField("CLSF_OID", ibq1SegId, clsf_oid); }
		
		if (cl_value != null) {
		msg.addField("CL_VALUE", ibq1SegId, cl_value); }
		
		if (start_tms != null) {
		msg.addField("START_TMS", ibq1SegId, start_tms); }
		
		if (end_tms != null) {
		msg.addField("END_TMS", ibq1SegId, end_tms); }
		if (user_id != null) {
		msg.addField("LAST_CHG_USR_ID", ibq1SegId, user_id); }
	}
	
	private String getTblIdFromSegName (String segmentName){
		String tblId = "";
		//GET TBL_ID FROM DB
		if (segmentName==null || "".equals(segmentName)){
			logger.debug("Invalid Segment name  : "+segmentName);
			return null;
		}
				
		String sqlGetTblID = "SELECT SEGMENT_DESC FROM FT_T_XSEG XSEG WHERE XSEG.SEGMENT_NME=:segment_nme<char[255]>";
		try {
			logger.debug("Executing: " + sqlGetTblID);

			dbConnection.setSQL(sqlGetTblID);
			dbConnection.addParameter(segmentName);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			// Only fetch once, get the first result in case there are more
			if (!dbConnection.isEndOfStream()) {
				tblId=dbConnection.getNextString().trim();
				logger.debug("Retrieved Table ID : "+tblId);
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		}
		return tblId;
		
	}

	private List<Date> getDateFieldListFromDB(String segmentNme,String colNme,String filterColNme, String filterColVal, String oidColumnName, String oidColVal)
	{
		List<Date> dateList = new ArrayList<Date>();
		String tbl_id=null;
		Date tempDte = null;
		//GET TBL_ID FROM DB
		if (segmentNme==null || "".equals(segmentNme)){
			logger.debug("SegmentId not found for segment name  : "+segmentNme);
			return null;
		}
		if (colNme==null || "".equals(colNme)){
			logger.debug("Column Name not found for segment name  : "+colNme);
			return null;
		}
		
		tbl_id = getTblIdFromSegName(segmentNme);
		
		if (tbl_id==null || "".equals(tbl_id)){
			logger.error("Unable to get Table id for Segment : " + segmentNme);
			return null;
		}
		//GET DATE FROM DB
		String sqlGetDateFromDB = "SELECT "+colNme+" FROM FT_T_"+tbl_id+" WHERE "+oidColumnName+" = "+"'"+oidColVal+"' ";
		
		if (!tbl_id.equals("IASS")){
			sqlGetDateFromDB = sqlGetDateFromDB+" AND (END_TMS > SYSDATE OR END_TMS IS NULL)";
		}
		if (filterColNme!=null && !"".equals(filterColNme)) {
			sqlGetDateFromDB = sqlGetDateFromDB + " AND "+filterColNme+"='"+filterColVal+"' ";
		}
		if (tbl_id.equals("ICD1"))	{
			sqlGetDateFromDB = "select distinct("+colNme+") from ft_t_icd1 icd1, ft_t_iad1 iad1 where icd1.iad1_oid=iad1.iad1_oid "+
								" and iad1.issact_id='"+oidColVal+"' "+
								" and (iad1.end_tms is null or iad1.end_tms>sysdate) "+
								" and (icd1.end_tms is null or icd1.end_tms>sysdate) ";
		}
		
		try {
			logger.debug("Executing: " + sqlGetDateFromDB);

			dbConnection.setSQL(sqlGetDateFromDB);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			// Only fetch once, get the first result in case there are more
			while (!dbConnection.isEndOfStream()) {
				Date dte=dbConnection.getNextDate();
				if (dte==null){
					continue;
				}				
				tempDte=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dte));
				logger.debug("Retrieved Database Date : "+tempDte);
				dateList.add(tempDte);
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 

		return dateList;
	}
	
	
	
	public List<String> getStringListFromMsg(String segment_nme,String col_nme)
	{
		if (segment_nme==null || "".equals(segment_nme)){
			return null;
		}
		ArrayList<String> valList=new ArrayList<String>();
		List<SegmentId> segList = getSegmentIds(segment_nme);
		String tmpStr=null;
		
		for (SegmentId segId : segList){
			if (getSegmentProcessInd(segId).equals("D") || getSegmentProcessInd(segId).equals("P")){
				continue;
			}
			tmpStr = msg.getStringField(col_nme,segId);
			valList.add(tmpStr);
			logger.debug("Segment Name : "+segment_nme+" Column Name : "+col_nme+" Value :"+tmpStr);
		}
		
		return valList;
	}
	public String getStringFieldFromMsg (String segmentNme,String colNme, String filterColNme, String filterColVal)
	{
		return getStringFromMsgWithFilter(segmentNme, colNme, filterColNme,  filterColVal);
	}
	public String getStringFieldFromMsg(String segment_nme,String col_nme)
	{
		String val=null;
		List<SegmentId> seg = getSegmentIds(segment_nme);
		if(seg==null || seg.size()>0){
			for (SegmentId segId : seg){
				if (getSegmentProcessInd(segId).equals("D") || getSegmentProcessInd(segId).equals("P")){
					continue;
				}
				val=msg.getStringField(col_nme,segId);
				if (val!=null && !"".equals(val)){
					return val;
				}
			}
			}
		
		return val;
	}
	public BigDecimal getDecimalFieldFromMsg(String segment_nme,String col_nme)
	{
		BigDecimal val = null;
		List<SegmentId> seg = getSegmentIds(segment_nme);
		if(seg==null || seg.size()>0){
			if (! (getSegmentProcessInd(seg.get(0)).equals("D") || getSegmentProcessInd(seg.get(0)).equals("P")) ){
				val=msg.getDecimalField(col_nme,seg.get(0));
			}
			}
		
		return val;
	}
	public String getStringFieldFromMsg(SegmentId segmentId, String col_nme)
	{
		String val=null;
		if(segmentId!=null ){
			if (! (getSegmentProcessInd(segmentId).equals("D") || getSegmentProcessInd(segmentId).equals("P")) ){
				val=msg.getStringField(col_nme,segmentId);
			}
			}
		
		return val;
	}
	
	public String getNestedStringFieldFromMsg(SegmentId prntSegId, String segment_nme,String col_nme)
	{
		String val=null;
		
		logger.debug("Inside getNestedStringFieldFromMsg Current SegmentId is ->"+prntSegId);
		if (prntSegId == null){
			logger.debug("Returning from getNestedStringFieldFromMsg as prntSegId is null ");
			return null; 
		}
		
		val = getStringFromImmediateChild(prntSegId, col_nme);
		if (val == null || "".equals(val)){
			int childCnt = msg.countSegmentChildren(prntSegId);
			logger.debug("Inside getNestedStringFieldFromMsg for segment->"+prntSegId+"child count is ->"+childCnt);
			if (childCnt > 0){
				for (int i=0; i<childCnt ; i++) {
					prntSegId.add(i);
					logger.debug("Inside getNestedStringFieldFromMsg calling nested for segment->"+prntSegId );
					val = getNestedStringFieldFromMsg(prntSegId, segment_nme, col_nme);
					if (val!=null && !"".equals(val) ){
						logger.debug("Inside getNestedStringFieldFromMsg returning val ->"+val);
						return val ;
					}
					prntSegId.removeLast();
					
				} 
			}
		}		
		return val;
	}
	
	
	private String getStringFromImmediateChild (SegmentId segId, String colNme){
			String val=null;
			
			int childCnt = msg.countSegmentChildren(segId);
			logger.debug("Inside getStringFromImmediateChild Current SegmentId is ->"+segId+ " and childs are "+childCnt);
			if (childCnt > 0){
				for (int i=0; i<childCnt ; i++) {
					segId.add(i);
					logger.debug("Inside getStringFromImmediateChild Current SegmentId is ->"+segId);
					val = getStringFieldFromMsg(segId, colNme);
					if (val!=null && !"".equals(val) ){
						logger.debug("Inside getStringFromImmediateChild returning val ->"+val);
						return val;
					}
					
				}
			}
			return val;
		
	}
	
	public String getValidStringFieldFromMsg(String segment_nme,String col_nme)
	{
		String val=null;
		List<SegmentId> seg = getSegmentIds(segment_nme);
		if(seg==null || seg.size()>0)
			for (SegmentId segId : seg){
				if (getSegmentProcessInd(segId).equals("D") || getSegmentProcessInd(segId).equals("P")){
					continue;
				}
			val=msg.getStringField(col_nme,segId);
			if (val!=null && !"".equals("")){
				logger.debug("Segment Name : "+segment_nme+" Column Name : "+col_nme+" Value :"+val);
				return val;
			}
			}
		
		return val;
	}
	
	public Date getDateFieldFromMsg(String segment_nme,String col_nme)
	{
		Date val=null;
		List<SegmentId> seg = getSegmentIds(segment_nme);
		if(seg==null || seg.size()>0){
			if (! (getSegmentProcessInd(seg.get(0)).equals("D") || getSegmentProcessInd(seg.get(0)).equals("P")) ){
				val=msg.getDateTimeField(col_nme,seg.get(0));
				} 			
		}	
		logger.debug("Segment Name : "+segment_nme+" Column Name : "+col_nme+" Value :"+val);
		return val;
	}
	
	private String getStringFromMsgDBWithFilter (String segment_nme,String col_nme,String filterColNme, String filterColVal, String oidColName, String oidColVal){
		
		if ((segment_nme!=null && !"".equals(segment_nme)) && (col_nme!=null && !"".equals(col_nme))){
		String val = getStringFromMsgWithFilter(segment_nme, col_nme, filterColNme, filterColVal);
		logger.debug("value for Message is "+val);
		if (val==null || "".equals(val)){			
			val = getStrFieldFromDB(segment_nme, col_nme, filterColNme, filterColVal, oidColName, oidColVal);
			logger.debug("value for Database is "+val);
			return val;			
		}else {
			return val;
		}
		}else 
			return null;
	}
	
	public String getStringFromMsgDB (String segment_nme,String col_nme, String oidColName, String oidColVal){
		return getStringFromMsgDBWithFilter (segment_nme,col_nme, null, null, oidColName, oidColVal);
		}
	public String getStringFromMsgDB (String segment_nme,String col_nme,String filterColNme, String filterColVal, String oidColName, String oidColVal){
		return getStringFromMsgDBWithFilter (segment_nme,col_nme, filterColNme, filterColVal, oidColName, oidColVal);
		}
	public String getStringFromDB (String segment_nme,String col_nme,String oidColName, String oidColVal){
		return getStrFieldFromDB (segment_nme,col_nme,  null, null, oidColName, oidColVal);
		}
	public String getStringFromDB (String segment_nme,String col_nme, String filterColNme, String filterColVal, String oidColName, String oidColVal){
		return getStrFieldFromDB (segment_nme,col_nme, filterColNme, filterColVal, oidColName, oidColVal);
		}
	public List<String> getStringListFromDB (String segment_nme,String col_nme, String filterColNme, String filterColVal, String oidColName, String oidColVal){
		return getFieldListFromDB (segment_nme,col_nme, filterColNme, filterColVal, oidColName, oidColVal);
		}
	
	public Date getDateFieldFromDB (String segment_nme,String col_nme,String oidColName, String oidColVal){
		return getDateFieldFromDB(segment_nme,col_nme,  null, null, oidColName, oidColVal);
		}
	
	private String getStrFieldFromDB(String segment_nme,String col_nme,String filterColNme, String filterColVal, String oidColName, String oidColVal)
	{
		List<String> valList = getFieldListFromDB( segment_nme, col_nme, filterColNme, filterColVal, oidColName, oidColVal);
		if (valList!=null && valList.size()>0){
			return valList.get(0);
		} else {
			return null;
		}
	}
	public Date getDateFieldFromDB(String segment_nme,String col_nme,String filterColNme, String filterColVal, String oidColName, String oidColVal)
	{
		List<Date> valList = getDateFieldListFromDB(segment_nme, col_nme, filterColNme, filterColVal, oidColName, oidColVal);
		if (valList!=null && valList.size()>0){
			return valList.get(0);
		} else {
			return null;
		}
	}
	
	private List<String> getFieldListFromDB(String segment_nme,String col_nme,String filterColNme, String filterColVal, String oidColumnName, String oidColVal)
	{
		List<String> valList = new ArrayList<String>();
		String tbl_id=null;
		//GET TBL_ID FROM DB
		if (segment_nme==null || "".equals(segment_nme)){
			logger.debug("SegmentId not found for segment name  : "+segment_nme);
			return null;
		}
		if (col_nme==null || "".equals(col_nme)){
			logger.debug("Column Name not found for segment name  : "+col_nme);
			return null;
		}

		tbl_id = getTblIdFromSegName(segment_nme);
		
		if (tbl_id==null || "".equals(tbl_id)){
			logger.error("Unable to get Table id for Segment : " + segment_nme);
			return null;
		}
		
		if (oidColumnName==null || "".equals(oidColumnName)){
			logger.error("Primary Column name undefined : " + oidColumnName);
			return null;
		}
		
		if (oidColVal==null || "".equals(oidColVal)){
			logger.error("Primary Column Value undefined : " + oidColVal);
			return null;
		}
		
		//GET FEILD FROM DB
		String sqlGetDataFromDB = "SELECT "+col_nme+" FROM FT_T_"+tbl_id+" WHERE "+oidColumnName+" = "+"'"+oidColVal+"' ";
		if (!tbl_id.equals("IASS")){
			sqlGetDataFromDB+="AND (END_TMS > SYSDATE OR END_TMS IS NULL)";
		}
		if (filterColNme!=null && !"".equals(filterColNme)) {
			sqlGetDataFromDB = sqlGetDataFromDB + " AND "+filterColNme+"='"+filterColVal+"' ";
		}
		 if (tbl_id.equals("ICD1"))	{
			
			 if (!col_nme.contains(".") && !col_nme.contains("<ASXDELIM>") ){
				 col_nme = "ICD1."+col_nme;
			 }
				sqlGetDataFromDB = "select distinct("+col_nme+") from ft_t_icd1 icd1, ft_t_iad1 iad1 where icd1.iad1_oid=iad1.iad1_oid "+
									" and iad1.issact_id='"+oidColVal+"' "+
									" and (iad1.end_tms is null or iad1.end_tms>sysdate) "+
									" and (icd1.end_tms is null or icd1.end_tms>sysdate) ";
		}
		 
		try {
			logger.debug("Executing: " + sqlGetDataFromDB);

			dbConnection.setSQL(sqlGetDataFromDB);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
				return null;
			}

			// Only fetch once, get the first result in case there are more
			while (!dbConnection.isEndOfStream()) {
				valList.add(dbConnection.getNextString());
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR during executing Constructed Query: " + e.getMessage());
			}
		} 
		return valList;
	}
	
	
	/*private String getTblIdFromSegName (String segmentName){
		String tblId = "";
		//GET TBL_ID FROM DB
		if (segmentName==null || "".equals(segmentName)){
			logger.debug("Invalid Segment name  : "+segmentName);
			return null;
		}
		
		tblId = CJavaASXMetaDataCache.getSegDescFromName(segmentName);
		if (tblId!=null && !"".equals(tblId)){
			return tblId;
		}
		
		String sqlGetTblID = "SELECT SEGMENT_DESC FROM FT_T_XSEG XSEG WHERE XSEG.SEGMENT_NME=:segment_nme<char[255]>";
		try {
			logger.debug("Executing: " + sqlGetTblID);

			dbConnection.setSQL(sqlGetTblID);
			dbConnection.addParameter(segmentName);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			// Only fetch once, get the first result in case there are more
			if (!dbConnection.isEndOfStream()) {
				tblId=dbConnection.getNextString().trim();
				logger.debug("Retrieved Table ID : "+tblId);
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		}
		
		CJavaASXMetaDataCache.setSegDescForName(segmentName, tblId);
		return tblId;
		
	}
	
	public String getTblAndOIDIdFromSegName (String segmentName){
		String tblId = "";
		String OidColNme = "";
		//GET TBL_ID FROM DB
		if (segmentName==null || "".equals(segmentName)){
			logger.debug("Invalid Segment name  : "+segmentName);
			return null;
		}
		
		tblId = CJavaASXMetaDataCache.getSegDescFromName(segmentName);
		if (tblId!=null && !"".equals(tblId) ) {
			OidColNme = CJavaASXMetaDataCache.getTblOidFromName(tblId);
				if (OidColNme!=null && !"".equals(OidColNme)){
					return tblId+"::"+OidColNme;
			}
		}
		
		String sqlGetTblID = " SELECT DISTINCT TIDC.TBL_ID ,TIDC.COL_NME FROM FT_T_XSEG XSEG, FT_T_TIDC TIDC, FT_T_TIDX TIDX WHERE  XSEG.SEGMENT_NME=:segment_nme<char[255]> "+
							" AND TIDC.TIDX_OID = TIDX.TIDX_OID AND XSEG.SEGMENT_DESC = TIDC.TBL_ID AND TIDC.TBL_ID = TIDX.TBL_ID AND  TBL_INDEX_TYP='P'"; 
		
		try {
			logger.debug("Executing: " + sqlGetTblID);

			dbConnection.setSQL(sqlGetTblID);
			dbConnection.addParameter(segmentName);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			// Only fetch once, get the first result in case there are more
			if (!dbConnection.isEndOfStream()) {
				tblId=dbConnection.getNextString().trim();
				logger.debug("Retrieved Table ID : "+tblId);
			}
			if (!dbConnection.isEndOfStream()) {
				OidColNme=dbConnection.getNextString().trim();
				logger.debug("Retrieved Table ID : "+tblId);
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		}
		
		CJavaASXMetaDataCache.setTblOidForName(tblId, OidColNme);
		
		return tblId+"::"+OidColNme;
		
	}*/
	public Date calculateBusinessDate(Date d,int num_days,String cal_id )
	{
			return calculateBusinessDate(d,num_days,cal_id, null);
	}
	
	public Date calculateBusinessDate(Date d,int num_days,String cal_id, String direction)
	{
		logger.debug("Started Calculating business Date");
		Date r_date=null;
		String sqlGetBusinessDay =null;
		
		String str_date=new SimpleDateFormat("dd-MM-yyyy").format(d);
		if (cal_id!=null && !"".equals(cal_id)){
			
			if(num_days>0){
				if (direction==null || "".equals(direction)) {
				direction="N";
				}
			}	
			else
			{
				if (direction==null || "".equals(direction)) {
					direction="P";
					}
				num_days=num_days*-1;
			}
			
			sqlGetBusinessDay = "select getBusinessDay(to_date('"+str_date+"','DD-MM-YYYY'),"+num_days+",'"+cal_id+"','"+direction+"') from dual";
		}else {
			sqlGetBusinessDay = "select to_char( trunc(to_date('"+str_date+"','DD-MM-YYYY'))+ "+num_days+" , 'YYYY-MM-DD') from dual";	
		}
		try {
			logger.debug("Executing: " + sqlGetBusinessDay);

			dbConnection.setSQL(sqlGetBusinessDay);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			// Only fetch once, get the first result in case there are more
			if (!dbConnection.isEndOfStream()) {
				String dte=dbConnection.getNextString();
				if (dte!=null )
				{	
					if(dte.trim().isEmpty()==false)
					{	
						logger.debug(" Printingggggggggggg Dte"+dte);
						dte=dte.substring(0,10);
						r_date=new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dte);
					}
				}
				logger.debug("Retrieved Database Date : "+r_date);
				return r_date;
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 
		return null;
	}
	
	private String getKeyColFromOID (SegmentId segId,  String filterColNme){
		String oid = "";
		String oidColName = "";
		String tblName = "";
		if (segId == null || "".equals(segId)){
			logger.error("Empty segment Id ");
			return null;
		}
		String segmentName = msg.getSegmentType(segId);

		if (segmentName==null || "".equals(segmentName)){
			logger.error("Error fetching segment name for segId="+segId);
			return null;		
		}
		if (segmentName.equals("IssueClassification") ){
			oidColName = "ISS_CLSF_OID";
			tblName = "FT_T_ISCL";
		}	
		else if(segmentName.equals("IADCIssueActionCharacteristics"))
		{
			oidColName = "IAC1_OID";
			tblName = "FT_T_IAC1";
		}
		else if(segmentName.equals("IssueActionStatistic"))
		{
			oidColName = "IASS_OID";
			tblName = "FT_T_IASS";
		}
		else if(segmentName.equals("IADCIssueActionClassification"))
		{
			oidColName = "IACL_OID";
			tblName = "FT_T_IACL";
		}
		else
		{	
			logger.error("kindly provide details of SegmentName"+segmentName+" filterColName"+filterColNme);
		}
		
			oid = getStringFieldFromMsg(segId, oidColName);
			if (oid==null || "".equals(oid)){
				return null;
			}
			return getFirstValDB("SELECT "+filterColNme+" FROM "+tblName+" WHERE "+oidColName+" = '"+oid+"' ");
		
	}
	
	public boolean hasMsgValueModified(String segmentName ,String colNme ){
		return hasMsgValueModified( segmentName , colNme, null , null );
	}
	
	public boolean hasMsgValueModified(String segmentName ,String colNme, String filterColNme, String filterColVal){
		
		List<SegmentId> segIdList ;
		segIdList = getSegmentIds(segmentName);
		return hasMsgValueModified(segIdList ,colNme, filterColNme, filterColVal);
	}
	
	public boolean hasMsgValueModified(SegmentId segId ,String colNme, String filterColNme, String filterColVal){
		
		List<SegmentId> segIdList = new ArrayList<SegmentId>(); 
		segIdList.add(segId);
		return hasMsgValueModified(segIdList ,colNme, filterColNme, filterColVal);
	}
		
	public boolean hasMsgValueModified(List<SegmentId> segIdList ,String colNme, String filterColNme, String filterColVal){	
		String tempStr = null;
		String segProcessedInd = null;
		String fColNameStr = null;
		
		if (segIdList ==null){
			logger.debug("segIdList is null");
			return false;
		}
		for ( SegmentId segId : segIdList ) {
			segProcessedInd = msg.getSegmentAttribute(segId,"SEGPROCESSEDIND");
			if (filterColNme!=null && !"".equalsIgnoreCase(filterColNme) && filterColVal!=null && !"".equals(filterColVal)) {
					fColNameStr = msg.getStringField(filterColNme, segId);
					
					if (fColNameStr == null || "".equals(fColNameStr) ){
						fColNameStr = getKeyColFromOID (segId, filterColNme);
					}
					if (fColNameStr==null){
						return true;
					}
					if ( filterColVal.equalsIgnoreCase(fColNameStr) ){
						tempStr =  msg.getFieldAttribute(colNme, segId, "OLD_VALUE");		
						if (segProcessedInd!=null && ( segProcessedInd.equals("C") ||  segProcessedInd.equals("D") ||  segProcessedInd.equals("P")) ){
							logger.debug("New Segment in condition returing true "+segProcessedInd);
							return true;
						}
					}					
			}else {
				tempStr =  msg.getFieldAttribute(colNme, segId, "OLD_VALUE");
				if (segProcessedInd!=null && ( segProcessedInd.equals("C")  ||  segProcessedInd.equals("D") ||  segProcessedInd.equals("P")) ){
					logger.debug("New Segment returing true "+segProcessedInd);
					return true;
				}
			}
			
	   if (tempStr == null) {
		   logger.debug("before Continue "+tempStr);
			continue;
		} else {
			logger.debug("returing true "+tempStr);
			return true;
		}
		}
		return false;
	}
	
	
	public String getOldStringValue(String segmentName ,String colNme ){
		return getOldStringValue( segmentName , colNme, null , null );
	}
	
	public String getOldStringValue(String segmentName ,String colNme, String filterColNme, String filterColVal){
		
		List<SegmentId> segIdList = null; 
		segIdList = getSegmentIds(segmentName, filterColNme, filterColVal);
		String tempStr = "";
		String segProcessedInd = null;
		for ( SegmentId segId : segIdList ) {
			segProcessedInd = msg.getSegmentAttribute(segId,"SEGPROCESSEDIND");
			if (filterColNme!=null && !"".equalsIgnoreCase(filterColNme) && filterColVal!=null && !"".equals(filterColVal)) {

				String	fColNameStr = msg.getStringField(filterColNme, segId);
				
				if (fColNameStr == null || "".equals(fColNameStr) ){
					fColNameStr = getKeyColFromOID (segId, filterColNme);
				}
				if (fColNameStr==null){
					continue;
				}
				if ( filterColVal.equalsIgnoreCase(fColNameStr) ) {
						tempStr =  msg.getFieldAttribute(colNme, segId, "OLD_VALUE");		
						if (segProcessedInd!=null && ( segProcessedInd.equals("C")  ||  segProcessedInd.equals("D") ||  segProcessedInd.equals("P")) ){
							logger.debug("New Segment in condition returing true "+segProcessedInd);
							return "";
						}
					}					
			}else {
				tempStr =  msg.getFieldAttribute(colNme, segId, "OLD_VALUE");
				if (segProcessedInd!=null && ( segProcessedInd.equals("C")  ||  segProcessedInd.equals("D") ||  segProcessedInd.equals("P")) ){
					logger.debug("New Segment returing true "+segProcessedInd);
					return "";
				}
			}
			
	   if (tempStr == null) {
		   logger.debug("before Continue "+tempStr);
			continue;
		} else {
			logger.debug("returing  "+tempStr);
			return tempStr;
		}
		}
		return tempStr;
	}

	/*public HashMap<String , List<ADJConfigBean>> fetchADJ1Configs(){
		List<ADJConfigBean> adjConfigBeanList = null;
		HashMap<String , List<ADJConfigBean> > ADJConfIssactMap = new HashMap<String, List<ADJConfigBean>>();

		
		String sql = " SELECT CAJ1_OID, ISSACT_TYP, ADJ_TYP_CDE, CMPNT_TYP,  ADJ_COND_SEG_NME ,  ADJ_COND_COL_NME ,  ADJ_COND_FILTER_COL_NME,  ADJ_COND_FILTER_COL_VAL,  ADJ_COND_COL_VAL,   upper(trim(CHESS_SUB_SEC)), upper(trim(CHESS_OBJ_SEC)), upper(trim(CHESS_CONV_SEC)), ADJ_DTE_SEG_NME, ADJ_DTE_COL_NME, ADJ_DTE_FILTER_COL_NME, ADJ_DTE_FILTER_COL_VAL, DECODE( ADJ_DTE_NO_OF_DAYS,NULL,0,ADJ_DTE_NO_OF_DAYS) , ADJ_DTE_CAL_ID, ADJ_APPLY_CDE, DAIRY_TYP_CDE,  DIARY_SEQUENCE_NUM " +
				" FROM FT_T_CAJ1 WHERE (END_TMS IS NULL or END_TMS>SYSDATE) ";		

			logger.debug("Executing: " + sql);

			dbConnection.setSQL(sql);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			while (!dbConnection.isEndOfStream()) {
				ADJConfigBean tmpADJ1=new ADJConfigBean();
				tmpADJ1.setCaj1Oid(dbConnection.getNextString());
				tmpADJ1.setIssactTyp(dbConnection.getNextString());
				tmpADJ1.setAdjTypCde(dbConnection.getNextString());
				tmpADJ1.setCmpntTyp(dbConnection.getNextString());
				tmpADJ1.setAdjCondSegNme(dbConnection.getNextString());
				tmpADJ1.setAdjCondColNme(dbConnection.getNextString());
				tmpADJ1.setAdjCondFilterColNme(dbConnection.getNextString());
				tmpADJ1.setAdjCondFilterColVal(dbConnection.getNextString());
				tmpADJ1.setAdjCondColVal(dbConnection.getNextString());
				tmpADJ1.setChessSubSecurity(dbConnection.getNextString());
				tmpADJ1.setChessObjSecurity(dbConnection.getNextString());
				tmpADJ1.setChessConvSecurity(dbConnection.getNextString());
				tmpADJ1.setAdjDteSegNme(dbConnection.getNextString());
				tmpADJ1.setAdjDteColNme(dbConnection.getNextString());
				tmpADJ1.setAdjDteFilterColNme(dbConnection.getNextString());  
				tmpADJ1.setAdjDteFilterColVal(dbConnection.getNextString()); 
				tmpADJ1.setAdjDteNoOfDays(dbConnection.getNextInt()); 
				tmpADJ1.setAdjDteCalId(dbConnection.getNextString()); 
				tmpADJ1.setAdjApplyCde(dbConnection.getNextString()); 
				tmpADJ1.setDairyTypeCde(dbConnection.getNextString()); 
				tmpADJ1.setDairySeqNum(dbConnection.getNextDecimal()); 

				if(ADJConfIssactMap.containsKey(tmpADJ1.getIssactTyp())){
					adjConfigBeanList = ADJConfIssactMap.get(tmpADJ1.getIssactTyp());
				}else {
					adjConfigBeanList = new ArrayList<ADJConfigBean>();					
				}
				
				adjConfigBeanList.add(tmpADJ1);
				ADJConfIssactMap.put(tmpADJ1.getIssactTyp(), adjConfigBeanList);	
				
				}
		logger.debug("Returning MAP "+ADJConfIssactMap);
		return ADJConfIssactMap;
	}*/
	
	public String getIssactTyp(String issactId){
		String issactTyp = msg.getStringField("ISSACT_TYP",new SegmentId(0));
		
		return (issactTyp != null) ? issactTyp : getDBIssactTyp(issactId);
	}
	
	public String getDBIssactTyp(String issact_id) {
		String issact_typ="";
		String sql = "SELECT trim(ISSACT_TYP) " +
				     "  FROM ft_t_iadc " +
				     " WHERE ISSACT_ID = :p_issact_id<char[11]>" +			     
				     "  AND (END_TMS IS NULL or end_tms >sysdate)";		

		try {
			logger.debug("Executing: " + sql);

			dbConnection.setSQL(sql);
			dbConnection.addParameter(issact_id);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			// Only fetch once, get the first result in case there are more
			if (!dbConnection.isEndOfStream()) {
				issact_typ = dbConnection.getNextString();
			}

		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return issact_typ;
	}
	
	
	public List<String> getFirstColListDB(String query) {
		if (query==null || "".equals(query)){
			return null;
		}
		
		logger.debug("Executing: " + query);
		
		List<String>  resltList = new ArrayList<String>();
		try {
			dbConnection.setSQL(query);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}
			while (!dbConnection.isEndOfStream()) {
				resltList.add(dbConnection.getNextString());
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return resltList;
	}
	
	public String getFirstValDB(String query) {
		
		if (query==null || "".equals(query)){
			return null;
		}
		
		logger.debug("Executing: " + query);
		
		try {
			dbConnection.setSQL(query);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}
			if (!dbConnection.isEndOfStream()) {
				return dbConnection.getNextString().trim();
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return null;
		
		}
	
public Date getFirstDateValDB(String query) {
		
		if (query==null || "".equals(query)){
			return null;
		}
		
		logger.debug("Executing: " + query);
		
		try {
			dbConnection.setSQL(query);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}
			if (!dbConnection.isEndOfStream()) {
				return dbConnection.getNextDate();
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return null;
		
		}
	
	
	/**
	 * Checks if the instrument in the message is in scope of OE
	 * @param msg
	 * @param db
	 * @param context
	 * @return
	 */
	
	
	/**
	 * This function Drills down the nested segments to three child levels.
	 * @param segId - original segment Id
	 * @param lst - list containing list of all three levels
	 * @param maxVal - number of segments at each level
	 * @return
	 */
	public List<List<String>> findNestedIdentifiers(SegmentId segId, int maxVal){
		logger.debug("processing findNestedIdentifiers()");
		List<String> firstLevel = new ArrayList<String>();
		List<String> secondLevel = new ArrayList<String>();
		List<String> thirdLevel = new ArrayList<String>();
		List<List<String>> mList = new ArrayList<List<String>>();
		String s = null;
		int MAXVAL = maxVal;
		logger.debug("Looping for getting all nested Segment Ids : start");
		for(int p = 0 ; p < MAXVAL ; p++){
			for(int n = 0 ; n < MAXVAL ; n++){
				for(int m = 0 ; m < MAXVAL ; m++){
				s = segId + "-" + p + "-" + n + "-" + m;
					if(!thirdLevel.contains(s)){
						thirdLevel.add(s);
					}
						for(int l = 0 ; l < MAXVAL ; l++){
							for(int k = 0 ; k < MAXVAL ; k++){
								s = segId + "-" + l + "-" + k;
								if(!secondLevel.contains(s)){
									secondLevel.add(s);
									}
								for(int j = 0 ; j < MAXVAL ; j++){
									s = segId + "-" + String.valueOf(j);
									if(!firstLevel.contains(s)){
										firstLevel.add(s);
									}
								}
							}
						}
						
					}
				}
			}
		logger.debug("Looping for getting all nested Segment Ids : start");
		Collections.sort(firstLevel);
		Collections.sort(secondLevel);
		Collections.sort(thirdLevel);
		logger.debug("Sorted all Lists containing segment Id's");
		//System.out.println("printing firstLevel segments :" +firstLevel);
		//System.out.println("printing secondLevel segments:" +secondLevel);
		//System.out.println("printing thirdLevel segments:" +thirdLevel);
		mList.add(firstLevel);
		mList.add(secondLevel);
		mList.add(thirdLevel);
		logger.debug("Returning List of all three nested segment lists");
		return mList;
	}
	
	public String getNextSeqValue(String seq) {

		String sqlFetchID = null;
		sqlFetchID = "SELECT " + seq + ".nextval FROM DUAL";
		String strId = null;
		try {
			dbConnection.setSQL(sqlFetchID);
			dbConnection.execute();
			while (!dbConnection.isEndOfStream()) {
				strId = dbConnection.getNextString();
			}
			dbConnection.close();
		} catch (Exception e) {
			logger.error("SQL query ("+sqlFetchID+") to get Next sequence failed, message = '" + e.getMessage() + "'");
			logger.debug(e);
		}
		return strId;
	}
	
	/*public HashMap<String , List<REMConfigBean> > fetchREMConfig(DatabaseAccess dbConnection) {
		List<REMConfigBean> REMConfigBeanList =null;
		HashMap<String , List<REMConfigBean> > REMConfIssactMap = new HashMap<String, List<REMConfigBean>>();
		
		String sql = "SELECT  REM1_OID, SUB_TYP, REM_TYP_CDE ,DRV_DUE_DTE_SEG_NME,DRV_DUE_DTE_COL_NME,DRV_DUE_DTE_FILTER_COL_NME,DRV_DUE_DTE_FILTER_COL_VAL, DRV_DUE_DTE_NO_OF_DAYS,DRV_DUE_DTE_CAL_ID, ENTITY_PROC_IND, NVL(SPECIAL_COND_CHK_REQ,'N') FROM FT_T_REM1 " +
						" WHERE (END_TMS IS NULL or END_TMS > SYSDATE)";
		
		try {
			logger.debug("Executing: " + sql);

			dbConnection.setSQL(sql);
			
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}

			while (!dbConnection.isEndOfStream()) {
				REMConfigBean REMConfigBn=new REMConfigBean();
				REMConfigBn.setRem1Oid(dbConnection.getNextString());
				REMConfigBn.setSubTyp(dbConnection.getNextString());
				REMConfigBn.setRemTypCde(dbConnection.getNextString());
				REMConfigBn.setDrvDueDteSegNme(dbConnection.getNextString());
				REMConfigBn.setDrvDueDteColNme(dbConnection.getNextString());
				REMConfigBn.setDrvDueDteFilterColNme(dbConnection.getNextString());
				REMConfigBn.setDrvDueDteFilterColVal(dbConnection.getNextString());
				REMConfigBn.setDrvDueDteNoOfDays(dbConnection.getNextString());
				REMConfigBn.setDrvDueDteCalId(dbConnection.getNextString());
				REMConfigBn.setEntityProcInd(dbConnection.getNextString());
				REMConfigBn.setSpecialCondChkReq(dbConnection.getNextString());
				
				//logger.debug("Fetched Reminder details for oid="+REMConfigBn.getRem1Oid());
				
				if(REMConfIssactMap.containsKey(REMConfigBn.getSubTypEntityPrcInd())){
					REMConfigBeanList = REMConfIssactMap.get(REMConfigBn.getSubTypEntityPrcInd());
				}else {
					REMConfigBeanList = new ArrayList<REMConfigBean>();					
				}
				
				REMConfigBeanList.add(REMConfigBn);
				REMConfIssactMap.put(REMConfigBn.getSubTypEntityPrcInd(), REMConfigBeanList);
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 
		return REMConfIssactMap;
	}*/
	
	public String getLastChgUsrId (){
		String firstVal = parseXMLFetchFirstAttibuteValue( msg.getXMLString(), "LASTCHGUSRID", "VALUE");
		if (firstVal == null || "".equals(firstVal)){
			String usrId = msg.getStringField("LAST_CHG_USR_ID",new SegmentId(0));
			if (usrId == null || "".equals(usrId) ){
				return "";
			}else {
				return usrId;
			}
		} else {
			return firstVal;
		}
	}
	
	public String parseXMLFetchFirstAttibuteValue(String xmlString, String fieldName, String attributeName )
    {
		Document document;
        DocumentBuilder documentBuilder;
        DocumentBuilderFactory documentBuilderFactory;
        NodeList nodeList;
        String tempVal ="";
        ArrayList<String> arList = new ArrayList<String>();
        try
        {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource ist = new InputSource(new StringReader(xmlString));
            document = documentBuilder.parse(ist);
            nodeList = document.getElementsByTagName("*");
            document.getDocumentElement().normalize();

            for (int index = 0; index < nodeList.getLength(); index++)
            {
                Node node = nodeList.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {   Element element = (Element) node;
                	
                if (element.getTagName().startsWith(fieldName))
                {
                    tempVal = element.getAttribute(attributeName);
                    if (tempVal != null && !"".equals(tempVal) ) {
                    	logger.debug("parseXMLFetchAttibuteValueList : Value : " + tempVal+" for fieldName:"+fieldName+" attributeName:"+attributeName);
	                    return tempVal;
                    }
                }
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
        return tempVal;
    }
	
	public void addRMDSegmentToMsg(String issactId, String remTypCde,
			String userId, Date sysdate, Date dueDate, String rmd1Oid,
			XMLMessage msg) {

		SegmentId rmd1SegId = msg.addSegment(XMLMessage.A_D_UNKNOWN,
				"ReminderGeneration");

		if (issactId != null) {
			msg.addField("CROSS_REF_ID", rmd1SegId, issactId);

			msg.addField("TBL_ID", rmd1SegId, "IADC");

			if (rmd1Oid != null) {
				msg.addField("RMD1_OID", rmd1SegId, rmd1Oid);
			}

			if (remTypCde != null) {
				msg.addField("REM_TYP_CDE", rmd1SegId, remTypCde);
			}
			if (dueDate != null) {
				msg.addField("DUE_DTE", rmd1SegId, dueDate);
			}

			if (userId != null) {
				msg.addField("CREATED_USR_ID", rmd1SegId, userId);
			}

			if (sysdate != null) {
				msg.addField("LAST_CHG_TMS", rmd1SegId, sysdate);
			}

			if (userId != null) {
				msg.addField("LAST_CHG_USR_ID", rmd1SegId, userId);
			}

		}

	}
	
	boolean verifyISINCode(String isinCode12){
		if (isinCode12 == null || isinCode12.length()!=12){
			return false;
		}
		String isinCode = isinCode12.substring(0, 11);
		int s = 0;
		int a = (isinCode.length() == 12) ? 1 : 2;
		for (int i = isinCode.length() - 1; i >= 0; i--) {
			int c = isinCode.charAt(i);
			if (c > '9') {
				// Character
				c -= ('A' - 10);
				s += (3 - a) * (c / 10) + a * c + (a - 1) * (c % 10) / 5;
			} else {
				// Number
				c -= '0';
				s += a * c + (a - 1) * (c / 5);
				a = 3 - a;
			}
		}
		s %= 10;
		s = (10 - s % 10) % 10;
		logger.debug("Inside ISIN Verification check digit ="+s);
		try {
		
		logger.debug("and actual is ="+Integer.parseInt(isinCode12.substring(11)));
		if( s == Integer.parseInt(isinCode12.substring(11)))
			return true;
		else		
			return false;
			}
			catch (Exception ex){
			return false;
			}
	}
	
	public String getSegmentProcessInd (SegmentId segId){
	String segProcessedInd = msg.getSegmentAttribute(segId,"SEGPROCESSEDIND");
	
		if (segProcessedInd==null){
			return "";
		}else {
			return segProcessedInd;
		}
	
	}
	public List<String> getStringListFromMsgWithFilter(String segment_nme,String col_nme,String filterColNme,String filterColVal )
	{
		if (segment_nme==null || "".equals(segment_nme)){
			return null;
		}
		ArrayList<String> valList=new ArrayList<String>();
		List<SegmentId> segList = getSegmentIds(segment_nme);
		String tmpStr=null;
		String retStr=null;
		for (SegmentId segId : segList){
			if (getSegmentProcessInd(segId).equals("D") || getSegmentProcessInd(segId).equals("P")){
				continue;
			}
			tmpStr = msg.getStringField(filterColNme,segId);
			if(tmpStr.equals(filterColVal)){
				retStr = msg.getStringField(col_nme, segId);
			}
			valList.add(retStr);
			logger.debug("Segment Name : "+segment_nme+" Column Name : "+col_nme+" Value :"+tmpStr);
		}
		
		return valList;
	}
	
	public List<SegmentId> getChildSegmentIds(XMLMessage msg, String chldSegType) 
	{
		
		List<SegmentId> segIdList = new ArrayList<SegmentId>();
		try 
		{
			for (int i = 0; i < msg.getSegmentCount(); i++) 
			{
				SegmentId segId = new SegmentId(i);
					int childSegCount = msg.countSegmentChildren(segId);
					if (childSegCount > 0) 
					{
						SegmentId chldSegment = new SegmentId(segId.toString());
						for (int p = 0; p < childSegCount; p++) 
						{
							chldSegment.add(p);
							if (chldSegType.contains(msg.getSegmentType(chldSegment))) 
							{
								segIdList.add(new SegmentId(chldSegment.toString()));							
							}
							chldSegment.removeLast();
						}
						chldSegment.removeLast();
					}
			}
		} 
		catch (Exception e) 
		{
			if (logger.isDebugEnabled()) logger.debug("Error - " + this.getClass().getName() + ".getChildSegmentIds - " + e.getMessage());
		}
		return segIdList;
	}
	
	public ArrayList<Object[]> getMultipleRowsFromDb(DatabaseAccess dbConnection, String strSQL,
			Object[] arrOfParameters, CRMBCustomRulesConstants.FIELD_TYPES[] arrOfColumnTypes) throws Exception {
		if (null == dbConnection || null == strSQL || strSQL.isEmpty() || null == arrOfColumnTypes
				|| 0 >= arrOfColumnTypes.length) {
			
				logger.debug("Invalid parameters");
			

			return null;
		}

		try { 
			dbConnection.setSQL(strSQL);

			if (null != arrOfParameters && 0 < arrOfParameters.length) {
				
					logger.debug("Set parameters");
				

				for (int i = 0; i < arrOfParameters.length; ++i) {
					Object objValue = arrOfParameters[i];

					if (objValue instanceof String) {
						dbConnection.addParameter((String) objValue);
					} else if (objValue instanceof Integer) {
						dbConnection.addParameter(((Integer) objValue).intValue());
					} else if (objValue instanceof BigDecimal) {
						dbConnection.addParameter((BigDecimal) objValue);
					} else if (objValue instanceof Date) {
						dbConnection.addParameter((Date) objValue);
					} else {
						
							logger.debug("Invalid parameter value");
						

						throw new Exception("Invalid parameter value");
					}
				}
			}

			if (!dbConnection.execute()) {
				
					logger.debug("Query execution failed");
				

				throw new Exception("Query execution failed");
			}

			if (dbConnection.isEndOfStream()) {
				
					logger.debug("No rows found");
				
            if (dbConnection != null)
			{
				dbConnection.close();
			}
				return null;
			}

			ArrayList<Object[]> result = new ArrayList<Object[]>();

			Object[] row = null;

			int i = 0;
			int iTotalValues = 0;

			while (!dbConnection.isEndOfStream()) {
				if (0 == i) {
					row = new Object[arrOfColumnTypes.length];
				}

				if (CRMBCustomRulesConstants.FIELD_TYPES.TEXT == arrOfColumnTypes[i]) {
					String strValue = dbConnection.getNextString();

					if (null != strValue) {
						strValue = strValue.trim();

						if (strValue.isEmpty()) {
							strValue = null;
						}
					}

					row[i] = strValue;
				} else if (CRMBCustomRulesConstants.FIELD_TYPES.INTEGER == arrOfColumnTypes[i]) {
					try {
						row[i] = dbConnection.getNextInt();
					} catch (Exception e) {
						
						 /* bug in product if decimal field in database is NULL
						  then exception is thrown by BRE interface ignore this
						  exception*/
						 
						row[i] = null;
					}
				} else if (CRMBCustomRulesConstants.FIELD_TYPES.DECIMAL == arrOfColumnTypes[i]) {
					try {
						row[i] = dbConnection.getNextDecimal();
					} catch (Exception e) {
						
						 /* bug in product if decimal field in database is NULL
						  then exception is thrown by BRE interface ignore this
						  exception*/
						
						row[i] = null;
					}
				} else if (CRMBCustomRulesConstants.FIELD_TYPES.DATETIME == arrOfColumnTypes[i]) {
					row[i] = dbConnection.getNextDate();
				} else {
				
						logger.debug("Invalid data type specified to get value from database");
					

					throw new Exception("Invalid data type specified to get value from database");
				}

				++i;
				++iTotalValues;

				if (i == arrOfColumnTypes.length) {
					result.add(row);
					i = 0;

					
						logger.debug("row found");
					
				}
			}

			if (result.isEmpty()) {
				
					logger.debug("No rows found");
				

				return null;
			}

			if (0 != (iTotalValues % arrOfColumnTypes.length)) {
				
					logger.debug("Expected count of result values (in multiple of " + arrOfColumnTypes.length
							+ ") are not matching with total values read (" + iTotalValues + ") from database");
				

				throw new Exception("Expected count of result values (in multiple of " + arrOfColumnTypes.length
						+ ") are not matching with total values read (" + iTotalValues + ") from database");
			}

			return result;
		} catch (Exception e) {
			throw new Exception("SQL Error - " + e.getMessage() + ", for SQL = " + strSQL + ", parameters = "
					+ ((null == arrOfParameters) ? "[]" : convertToString(arrOfParameters)));
		} finally {
			dbConnection.close();
		}
	}

	/**
	 * Utility method to convert the given array to string value
	 * @param arrOfValue
	 * @return
	 * @throws Exception
	 */
	public String convertToString(Object[] arrOfValue) throws Exception {
		if (null == arrOfValue) {
			return null;
		}
		
		String result = "";

		for (int i = 0; i < arrOfValue.length; ++i) {
			if (0 < i) {
				result += ", ";
			}

			result += convertToString(arrOfValue[i]);
		}

		return ("[" + result + "]");
	}

	public String convertToString(Object objValue) throws Exception {
		if (null == objValue) {
			return null;
		}

		String strValue = null;

		try {
			if (objValue instanceof String) {
				strValue = (String) objValue;
			} else if (objValue instanceof Integer) {
				strValue = ((Integer) objValue).toString();
			} else if (objValue instanceof BigDecimal) {
				strValue = ((BigDecimal) objValue).toString();
			} else if (objValue instanceof Date) {
				SimpleDateFormat df = new SimpleDateFormat(CRMBCustomRulesConstants.STANDARD_DATE_FORMAT_FOR_JAVA);

				strValue = df.format((Date) objValue);
			}
		} catch (Exception e) {
		}

		return strValue;
	}


	
	//Added notification method
	public void raiseNotifcn(int error, Map<String, String> mkey)
	{
		try
		{
			logger.debug("Raising Notification");
			String[] paramNames = new String[mkey.size()];
			String[] paramValues = new String[mkey.size()];
			int index = 0;
			for (Map.Entry<String, String> mapEntry : mkey.entrySet()) 
			{
				paramNames[index] = mapEntry.getKey();
				paramValues[index] = mapEntry.getValue();
				index++;
			} 

			notificationCreator.raiseException(error, paramNames, paramValues);

		}
		catch (Exception e)
		{
			logger.debug("Java Exception occured inside method raiseNotifcn()");
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getFirstRowDB(String query,String param1,String param2,String param3, int numberOfColumns) {
		
		if (query==null || "".equals(query)){
			return null;
		}
		ArrayList<String> arrResults = new ArrayList<String>();
		logger.debug("Executing: " + query);
		
		try {
			dbConnection.setSQL(query);
			dbConnection.addParameter(param1);
			dbConnection.addParameter(param2);
			dbConnection.addParameter(param3);
		//	dbConnection.addParameter(param4);
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}
			if (!dbConnection.isEndOfStream()) {
				
				for(int i=1;i<=numberOfColumns;i++){
					arrResults.add(dbConnection.getNextString());				
				}
				//return arrResults;
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return arrResults;
		
	}
	
public ArrayList<String> getFirstRowDB(String query,String param1,String param2,String param3,String param4, int numberOfColumns) {
		
		if (query==null || "".equals(query)){
			return null;
		}
		ArrayList<String> arrResults = new ArrayList<String>();
		logger.debug("Executing: " + query);
		
		try {
			dbConnection.setSQL(query);
			dbConnection.addParameter(param1);
			dbConnection.addParameter(param2);
			dbConnection.addParameter(param3);
			dbConnection.addParameter(param4);
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}
			if (!dbConnection.isEndOfStream()) {
				
				for(int i=1;i<=numberOfColumns;i++){
					arrResults.add(dbConnection.getNextString());				
				}
				//return arrResults;
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return arrResults;
		
	}
	
public ArrayList<String> getFirstRowDB(String query,String param1,String param2, int numberOfColumns) {
		
		if (query==null || "".equals(query)){
			return null;
		}
		ArrayList<String> arrResults = new ArrayList<String>();
		logger.debug("Executing: " + query);
		
		try {
			dbConnection.setSQL(query);
			dbConnection.addParameter(param1);
			dbConnection.addParameter(param2);
			//dbConnection.addParameter(param3);
		//	dbConnection.addParameter(param4);
			if (!dbConnection.execute()) {
				logger.error("ERROR: Failed to execute SQL statement.");
			}
			if (!dbConnection.isEndOfStream()) {
				
				for(int i=1;i<=numberOfColumns;i++){
					arrResults.add(dbConnection.getNextString());				
				}
				//return arrResults;
			}
		} catch (Exception e) {
			if (!(e instanceof GSException)) {
				logger.error("ERROR: " + e.getMessage());
			}
		} 	
		return arrResults;
		
	}

public ArrayList<String> getFirstRowDB(String query,String param1,int numberOfColumns) {
	
	if (query==null || "".equals(query)){
		return null;
	}
	ArrayList<String> arrResults = new ArrayList<String>();
	logger.debug("Executing: " + query);
	
	try {
		dbConnection.setSQL(query);
		dbConnection.addParameter(param1);
		//dbConnection.addParameter(param2);
		//dbConnection.addParameter(param3);
	//	dbConnection.addParameter(param4);
		if (!dbConnection.execute()) {
			logger.error("ERROR: Failed to execute SQL statement.");
		}
		if (!dbConnection.isEndOfStream()) {
			
			for(int i=1;i<=numberOfColumns;i++){
				arrResults.add(dbConnection.getNextString());				
			}
			//return arrResults;
		}
	} catch (Exception e) {
		if (!(e instanceof GSException)) {
			logger.error("ERROR: " + e.getMessage());
		}
	} 	
	return arrResults;
	
}
	
public String getFirstValDB(String query,String id) {
	
	if (query==null || "".equals(query)){
		return null;
	}
	String val = new String();
	logger.debug("Executing: " + query);
	
	try {
		dbConnection.setSQL(query);
		dbConnection.addParameter(id);
		if (!dbConnection.execute()) {
			logger.error("ERROR: Failed to execute SQL statement.");
		}
		if (!dbConnection.isEndOfStream()) {
			val= dbConnection.getNextString().trim();
		}
	} catch (Exception e) {
		if (!(e instanceof GSException)) {
			logger.error("ERROR: " + e.getMessage());
		}
	} 	
	return val;
	
}

public ArrayList<String> getFirstRowDB(String query,String id) {
	
	if (query==null || "".equals(query)){
		return null;
	}
	ArrayList<String> arrResults = new ArrayList<String>();
	logger.debug("Executing: " + query);
	
	try {
		dbConnection.setSQL(query);
		dbConnection.addParameter(id);
		if (!dbConnection.execute()) {
			logger.error("ERROR: Failed to execute SQL statement.");
		}
		while (!dbConnection.isEndOfStream()) {
				arrResults.add(dbConnection.getNextString());				
		}
	} catch (Exception e) {
		if (!(e instanceof GSException)) {
			logger.error("ERROR: " + e.getMessage());
		}
	} 	
	return arrResults;
	
}

public ArrayList<String> getFirstRowDB(String query) {
	
	if (query==null || "".equals(query)){
		return null;
	}
	ArrayList<String> arrResults = new ArrayList<String>();
	logger.debug("Executing: " + query);
	
	try {
		dbConnection.setSQL(query);
		if (!dbConnection.execute()) {
			logger.error("ERROR: Failed to execute SQL statement.");
		}
		while (!dbConnection.isEndOfStream()) {
				arrResults.add(dbConnection.getNextString());				
		}
	} catch (Exception e) {
		if (!(e instanceof GSException)) {
			logger.error("ERROR: " + e.getMessage());
		}
	} 	
	return arrResults;
	
}
	
	
	}
