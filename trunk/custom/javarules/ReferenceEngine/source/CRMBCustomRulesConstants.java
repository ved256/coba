
import java.text.SimpleDateFormat;

import com.thegoldensource.jbre.*;


public class CRMBCustomRulesConstants
{

	/*
	 * Types of field
	 */
	enum FIELD_TYPES
	{
		INVALID, TEXT, INTEGER, DECIMAL, DATETIME;

		public String toString()
		{
			if (INVALID == this) return "INVALID";
			else if (TEXT == this) return "TEXT";
			else if (INTEGER == this) return "INTEGER";
			else if (DECIMAL == this) return "DECIMAL";
			else if (DATETIME == this) return "DATETIME";
			else return "NOT-SUPPORTED";
		}
	};


	
	/*
	 * Standard format used to convert date into string and vice-a-versa
	 */

	public static final String STANDARD_DATE_FORMAT_FOR_JAVA = "dd/MM/yyyy hh:mm:ss a";
	public static final String STANDARD_DATE_FORMAT_FOR_ORACLE = "DD/MM/YYYY HH:MI:SS AM";
	public static final String STANDARD_MM_DD_YYYY_FORMAT = "MM-dd-yyyy";

	public static final SimpleDateFormat stdformat = new SimpleDateFormat(STANDARD_MM_DD_YYYY_FORMAT);


	/*
	 * Frequently used string values
	 */
	public static final String EMPTY_STRING = "";



	/*
	 * message types
	 */
	public static final String MSG_CLASSIFICATION_UI = "WEBMSG";

	




}
