import com.thegoldensource.jbre.*;

//import ICBCSContactDuplicateCheck.Extended;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class RMBSClearCloneSSIFields implements JavaRule {

	private static String m_strRuleName = "RMBSClearCloneSSIFields";
	private static final Logger logger = Logger.getLogger(m_strRuleName);
	private XMLMessage m_msg = null;
	private CJavaRMBSSICommons commons = null;
	public static final SegmentId headerSegment = new SegmentId(0);

	public static final String[] NOTIFICATION_PARAMETERS = { "RULE_NAME", "MAIN_ENTITY_NME" };

	public boolean initialize(String[] parameters) {
		loggerDebug("Initializing rule " + m_strRuleName);
		return true;
	}

	public boolean process(XMLMessage msg, DatabaseObjectContainer dboc, ProcessorContext pContext,
			DatabaseAccess dbConnection, NotificationCreator notificationCreator) {
		commons = new CJavaRMBSSICommons(msg, dboc, pContext, dbConnection, notificationCreator);

		String getCloneFlagValue = null;
		loggerDebug("Processing Java rule " + m_strRuleName);

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
		int ssiTotal = msg.getSegmentCount();
		for (int j = 1; j < ssiTotal; j++) {
			SegmentId currSegId = new SegmentId(j);
			String currSegName = msg.getSegmentType(currSegId);

			if ("RMBSSICustom".equals(currSegName)) {

				getCloneFlagValue = msg.getStringField("CLONE_FLAG", currSegId);
				logger.debug("getCloneFlagValue " + getCloneFlagValue);
			}
		}

		try {

			int iTotal = msg.getSegmentCount();

			for (int i = 1; i < iTotal; i++) {
				SegmentId currSegId = new SegmentId(i);
				String currSegName = msg.getSegmentType(currSegId);

				if (null == currSegName) {
					continue;
				}

				if (getCloneFlagValue != null && "C".equalsIgnoreCase(getCloneFlagValue)
						&& "StandardSettlementInstructions".equals(currSegName)) {

					String strAction = msg.getAction(currSegId);
					logger.debug("Segment Action - " + strAction);
					if (XMLMessage.A_INSERT.equals(strAction) || XMLMessage.A_UNKNOWN.equals(strAction)
							|| XMLMessage.A_UPDATE.equals(strAction)) {

						msg.removeField("SSI_ID", currSegId);
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("SQL query failed, message = '" + e.getMessage() + "'");
			logger.debug(e);
		} finally {
			dbConnection.close();
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
