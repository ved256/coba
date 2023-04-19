import java.util.ArrayList;
import org.apache.log4j.Logger;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import com.thegoldensource.jbre.DatabaseAccess;
import com.thegoldensource.jbre.DatabaseObjectContainer;
import com.thegoldensource.jbre.GSException;
import com.thegoldensource.jbre.JavaRule;
import com.thegoldensource.jbre.NotificationCreator;
import com.thegoldensource.jbre.ProcessorContext;
import com.thegoldensource.jbre.SegmentId;
import com.thegoldensource.jbre.XMLMessage;

public class CJavaRMBSSetVerificationStatus implements JavaRule {
	private static Logger logger = Logger.getLogger("CJavaRMBSSetVerificationStatus");
	private CJavaRMBSSICommons commons = null;
	private static final String GSOLABEL = "GSOLABEL";
	SegmentId segId = null;
	ArrayList<Object[]> Role_Id = null;

	@Override
	public boolean initialize(String[] args) {
		logger.debug("Initialized CJavaRMBSSetVerificationStatus");
		return true;
	}

	@Override
	public boolean process(XMLMessage msg, DatabaseObjectContainer dboc, ProcessorContext pContext,
			DatabaseAccess dbConnection, NotificationCreator notificationCreator) throws GSException {
		commons = new CJavaRMBSSICommons(msg, dboc, pContext, dbConnection, notificationCreator);
		String getChildStatus = null;
		String getSSIOID = null;
		String lastchgusr = msg.getStringField("LAST_CHG_USR_ID", new SegmentId(0));
		ArrayList<String> addSSIOIDValues = new ArrayList<String>();
		if (isDeugEnabled()) {
			logger.debug("In Process: CJavaRMBSSetVerificationStatus");
			logger.debug("In CJavaRMBSSetVerificationStatus: Initial XML:" + msg.getXMLString());
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
		int flag = 0;
		/* Check if segment is presnet or not */
		if (null != prntSSISSegName && "StandardSettlementInstructions".equals(prntSSISSegName)) {
			logger.debug("Parent SSIS segement found.");
		} else {
			logger.debug("SSIS segement not found. Hence, exiting from Rule.");
			return true;
		}

		String UserID = msg.getStringField("CHECKERUSERID", new SegmentId(0));
		if (UserID != null) {
			/* Fetch UserID of the logged in user */
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
			/* If blank return */
			if ("".equals(Role_Id) || Role_Id == null) {
				logger.debug("Role id is blank or user id is user1");
				return true;
			}

			for (Object[] role_id1 : Role_Id) {
				for (Object role_id2 : role_id1)

				{
					logger.debug("Role id2 " + role_id2.toString());

					/* Check the User Role if found checker role then proceed */

					if ((String.valueOf(role_id2).equals("SSIAUTH")) || (String.valueOf(role_id2).equals("SSIUNAUTH"))
							|| (String.valueOf(role_id2).equals("SSINA"))) {

						try {

							int iTotal = msg.getSegmentCount();
							for (int i = 1; i < iTotal; i++) {
								SegmentId currSegId = new SegmentId(i);
								String currSegName = msg.getSegmentType(currSegId);

								if (null == currSegName) {
									continue;
								}

								if ("StandardSettlementInstructions".equals(msg.getSegmentType(currSegId))
										&& "RMBPStandardSettlementInstruction"
												.equals(msg.getSegmentAttribute(currSegId, GSOLABEL))) {
									// do nothing
								}
								/*
								 * Get the child segment and fetch SSI_oid from
								 * the segment and add it to arraylist
								 * addSSIOIDValues
								 *
								 */
								else {
									getChildStatus = msg.getStringField("SWAP_CIRCUIT_TYP", currSegId);
									getSSIOID = msg.getStringField("SSI_OID", currSegId);
									if (getChildStatus != null && getSSIOID != null && "PA".equals(getChildStatus)) {
										addSSIOIDValues.add(getSSIOID);
										logger.debug(
												"CJavaRMBSSetVerificationStatus addSSIOIDValues" + addSSIOIDValues);
									}
								}
							}
						}

						catch (Exception e) {
							logger.debug("In CJavaRMBSSetVerificationStatus: process - ERROR OCCURED" + e);
						}

					} else {
						logger.debug("User id is not one of the maker checker" + Role_Id.toString());

					}
				}
			}

		}
		if (!addSSIOIDValues.isEmpty()) {

			HashSet<String> hset = new HashSet<String>(addSSIOIDValues);
			hset.remove(null);
			hset.remove("");
			Iterator value = hset.iterator();

			String valnextVal = null;
			logger.debug("CJavaRMBSSetVerificationStatus hset" + hset);
			while (value.hasNext()) {
				SegmentId currentSegID = msg.addSegment(XMLMessage.A_D_UNKNOWN, "StandardSettlementInstructions");
				valnextVal = value.next().toString();
				msg.addField("SSI_OID", currentSegID, valnextVal);
				/*
				 * Set verification status field to Verified
				 */
				msg.addField("SWAP_CIRCUIT_TYP", currentSegID, "Verified");
				msg.addField("START_TMS", currentSegID, new Date());
				msg.addField("LAST_CHG_TMS", currentSegID, new Date());
				msg.addField("LAST_CHG_USR_ID", currentSegID, lastchgusr);
			}
		}
		logger.debug("In CJavaRMBSSetVerificationStatus: Final XML:" + msg.getXMLString());
		return true;

	}

	boolean isDeugEnabled() {
		if (logger.isDebugEnabled()) {
			return true;
		}
		return false;
	}
}