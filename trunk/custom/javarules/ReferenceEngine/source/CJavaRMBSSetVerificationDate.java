import java.util.ArrayList;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;
import org.apache.log4j.Logger;
import java.util.Date;
import java.util.List;
import com.thegoldensource.jbre.DatabaseAccess;
import com.thegoldensource.jbre.DatabaseObjectContainer;
import com.thegoldensource.jbre.GSException;
import com.thegoldensource.jbre.JavaRule;
import com.thegoldensource.jbre.NotificationCreator;
import com.thegoldensource.jbre.ProcessorContext;
import com.thegoldensource.jbre.SegmentId;
import com.thegoldensource.jbre.XMLMessage;

public class CJavaRMBSSetVerificationDate implements JavaRule {
	private static Logger logger = Logger.getLogger("CJavaRMBSSetVerificationDate");
	private CJavaRMBSSICommons commons = null;
	SegmentId segId = null;
	// private XMLMessage globalMsg;

	ArrayList<Object[]> Role_Id = null;

	@Override
	public boolean initialize(String[] args) {
		logger.debug("Initialized CJavaRMBSSetVerificationDate");
		return true;
	}

	@Override
	public boolean process(XMLMessage msg, DatabaseObjectContainer dboc, ProcessorContext pContext,
			DatabaseAccess dbConnection, NotificationCreator notificationCreator) throws GSException {
		// globalMsg = msg;
		String ssi_oid=null;
		commons = new CJavaRMBSSICommons(msg, dboc, pContext, dbConnection, notificationCreator);
		if (isDeugEnabled()) {
			logger.debug("In Process: CJavaRMBSSetVerificationDate");
			logger.debug("In CJavaRMBSSetVerificationDate: Initial XML:" + msg.getXMLString());
		}
		String msgClassification = msg.getStringField("MSG_CLASSIFICATION", new SegmentId(0));

		if (isDeugEnabled()) {
			logger.debug("Message Classification: " + msgClassification);
		}

		if ((msgClassification == null || "".equals(msgClassification)) || ((!"WEBMSG".equals(msgClassification)))) {
			logger.debug("Incorrect message classification found from msg, hence exiting the rule.");
			return true;
		}

		SegmentId prntSSISSegId = new SegmentId(0);
		String prntSSISSegName = msg.getSegmentType(prntSSISSegId);
		String strPrntSSIOID = msg.getStringField("SSI_OID", prntSSISSegId);
		int flag=0;

		if (null != prntSSISSegName && "StandardSettlementInstructions".equals(prntSSISSegName)) {
			logger.debug("Parent SSIS segement found.");
		} else {
			logger.debug("SSIS segement not found. Hence, exiting from Rule.");
			return true;
		}

		String UserID = msg.getStringField("CHECKERUSERID", new SegmentId(0));
		if (UserID != null) {
			String Role_Id_AUGR = "SELECT USR_GRP_ID FROM FT_T_AUGR WHERE USR_GRP_OID IN(SELECT PRNT_USR_GRP_OID FROM FT_T_AUGP WHERE AUSR_OID IN (SELECT AUSR_OID FROM FT_T_AUSR WHERE trim(upper(USR_ID)) =TRIM(UPPER(:UserID<char[12]>))))";

			logger.debug("UserID" + UserID);

			ArrayList<String> UserIds = new ArrayList<String>();
			UserIds.add(UserID);

			CRMBCustomRulesConstants.FIELD_TYPES[] arrOfColumnTypes2 = { CRMBCustomRulesConstants.FIELD_TYPES.TEXT };

			Object[] arrOfParameters2 = { UserID };

			try {
				Role_Id = commons.getMultipleRowsFromDb(dbConnection, Role_Id_AUGR, arrOfParameters2,
						arrOfColumnTypes2);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// declaring variables
			if ("".equals(Role_Id) || Role_Id == null) {
				logger.debug("Role id is blank or user id is user1");
				return true;
			}

			for (Object[] role_id1 : Role_Id) {
				for (Object role_id2 : role_id1)

				{
					logger.debug("Role id2 " + role_id2.toString());
					if ((String.valueOf(role_id2).equals("SSIAUTH")) || (String.valueOf(role_id2).equals("SSIUNAUTH"))
							|| (String.valueOf(role_id2).equals("SSINA"))) {

						try {

							int iTotal = msg.getSegmentCount();

							// logger.debug(msg.getXMLString());

							for (int i = 1; i < iTotal; i++) {
								SegmentId currSegId = new SegmentId(i);
								String currSegName = msg.getSegmentType(currSegId);

								if (null == currSegName) {
									continue;
								}
								/*if("StandardSettlementInstructions".equals(currSegName))
								{
									ssi_oid = msg.getStringField("SSI_OID", currSegId);
								}*/
								if ("RMBSSICustom".equals(currSegName)) {

									String strAction = msg.getAction(currSegId);
									logger.debug("Segment Action - " + strAction);
									if (XMLMessage.A_INSERT.equals(strAction) || XMLMessage.A_UNKNOWN.equals(strAction)
											|| XMLMessage.A_UPDATE.equals(strAction)) {

										msg.addField("SSI_VERIFICATION_DTE", currSegId, new Date());
										flag=1;
									}
								} else {
									continue;
								}
							}
							if(flag==0)
							{
								logger.debug("flag"+flag);
								SegmentId	currSegId = msg.addSegment(XMLMessage.A_D_UNKNOWN, "RMBSSICustom");
								msg.addField("SSI_VERIFICATION_DTE", currSegId, new Date());
								msg.addField("SSI_OID", currSegId, strPrntSSIOID);
								logger.debug("Added segmnet-----");
							}
							/*
							 * SegmentId segmentSSIGenericDetails =
							 * msg.addSegment(XMLMessage.A_D_UNKNOWN,
							 * "RMBSSICustom");
							 * 
							 * msg.addField("SSI_VERIFICATION_DTE",
							 * segmentSSIGenericDetails, new Date());
							 */ }

						catch (Exception e) {
							logger.debug("In CJavaRMBSSetVerificationDate: process - ERROR OCCURED" + e);
						}

					} else {
						logger.debug("User id is not one of the maker checker" + Role_Id.toString());

					}
				}
			}

		}
		logger.debug("In CJavaRMBSSetVerificationDate: Final XML:" + msg.getXMLString());
		return true;

	}

	boolean isDeugEnabled() {
		if (logger.isDebugEnabled()) {
			return true;
		}
		return false;
	}
}