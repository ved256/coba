
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import com.thegoldensource.jbre.*;

import org.apache.log4j.Logger;

public class CJavaRMBPSSIParentToChildLink implements JavaRule
{
	private static String m_strRuleName = "CJavaRMBPSSIParentToChildLink";
	private static final Logger logger = Logger.getLogger(m_strRuleName);
	private HashSet<String> m_setOfMessageTypes = null;
	private XMLMessage m_msg = null;
	private DatabaseAccess m_dbConnection = null;
	private NotificationCreator m_notificationCreator = null;
	private ProcessorContext m_pContext = null;
	
	public boolean initialize(String[] parameters) 
	{
		logger.debug("Initializing " + m_strRuleName);
 		for (int i = 0; i < parameters.length; ++i)
		{
			parameters[i] = parameters[i].trim();
			loggerDebug("Parameter " + (i + 1) + " : '" + parameters[i] + "'");

			String[] strParameterConfig = parameters[i].split("=", 2);
			if (null == strParameterConfig
					|| 2 != strParameterConfig.length)
			{
				loggerDebug("Invalid parameter configuration , parameter " + (i + 1) + " : '" + parameters[i] + "'");
				return false;
			}
			String strParameterValue = strParameterConfig[1].trim();

			if (strParameterValue.isEmpty())
			{
				loggerDebug("Empty value specified in parameter " + (i + 1) + " : '" + parameters[i] + "'");
				return false;
			}
			
			if (parameters[i].startsWith("MessageTypeList"))
			{
				HashSet<String> tmpContainer = new HashSet<String>(5);
                tmpContainer.addAll((Collection<String>) (Arrays.asList(strParameterValue.split(","))));
                if (parameters[i].startsWith("MessageTypeList"))
                {
                    m_setOfMessageTypes = tmpContainer;
                }
			}
		}
		
		loggerDebug("Message Types - '" + ((null == m_setOfMessageTypes) ? "ALL" : m_setOfMessageTypes.toString()) + "'");
		return true;
	}

	public boolean process(XMLMessage msg,
			DatabaseObjectContainer dboc, 
			ProcessorContext pContext, 
			DatabaseAccess dbConnection,
			NotificationCreator notificationCreator) throws GSException 
	{
		try
		{
			loggerDebug("Processing Java rule " + m_strRuleName);
			m_msg = msg;
	        m_dbConnection = dbConnection;
	        m_notificationCreator = notificationCreator;
	        m_pContext = pContext;
	        
	        SegmentId HEADER_SEGMENT = new SegmentId(0);
	        boolean bUImessage = ("WEBMSG".equals(m_msg.getStringField("MSG_CLASSIFICATION", HEADER_SEGMENT))) ? true : false;
	        
	        loggerDebug("Is UI message = " + bUImessage);
	        
	        if (!bUImessage)
	        {
	        	loggerDebug("Rule only works for UI message");
	        	return true;
	        }
	        
	        if (null != m_setOfMessageTypes)
            {
                String strMsgType = m_msg.getStringField("MSG_CLASSIFICATION", HEADER_SEGMENT);

                loggerDebug("Message Type = '" + strMsgType + "'");

                if (!m_setOfMessageTypes.contains(strMsgType))
                {
                	loggerDebug("Rule not enabled for message type");
                    return true;
                }
            }
	        
	        //Rule logic starts
	        SegmentId prntSSISSegId = new SegmentId(0);
        	String prntSSISSegName = m_msg.getSegmentType(prntSSISSegId);
        	String strPrntSSIOID = m_msg.getStringField("SSI_OID", prntSSISSegId);
        	
        	if(null != prntSSISSegName && "StandardSettlementInstructions".equals(prntSSISSegName))
        	{
        		loggerDebug("Parent SSIS segement found.");
        	}
        	else
        	{
        		loggerDebug("SSIS segement not found. Hence, exiting from Rule.");
        		return true;
        	}
        	
        	loggerDebug("Fetching child SSIS segment...");
        	int iTotal = m_msg.getSegmentCount();
        	
            for (int i=1; i < iTotal; i++)
            {
            	SegmentId currSegId = new SegmentId(i);
            	String currSegName = m_msg.getSegmentType(currSegId);
            	
            	if(null == currSegName)
            	{
            		continue;
            	}
            	
            	if("StandardSettlementInstructions".equals(currSegName))
            	{
            		loggerDebug("Child SSIS segment found.");
            		String strAction = m_msg.getAction(currSegId);
            		loggerDebug("Segment Action - " + strAction);
            		if (XMLMessage.A_INSERT.equals(strAction)
    						|| XMLMessage.A_UNKNOWN.equals(strAction)
    						|| XMLMessage.A_UPDATE.equals(strAction))
    				{
            			loggerDebug("Child SSIS segment found with Action - " + strAction + ".Hence, appending PRNT_SSI_OID");
            			m_msg.addField("PRNT_SSI_OID", currSegId, strPrntSSIOID);
            			loggerDebug("Added PRNT_SSI_OID");
    				}
            	}
            	else 
            	{
            		continue;
            	}
            }
		}
        catch (Exception e) 
        {
        	loggerDebug("Raising Java notification for - '" + e.getMessage() + "'");
        }
		return true;
	}

	public void loggerDebug(String msg)
	{
		if(logger.isDebugEnabled())
		{
			logger.debug(msg); 
		}
	}
}
