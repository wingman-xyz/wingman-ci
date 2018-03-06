package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.InetAddress;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class HTMLResGenerator {
	private String strReportHTMLFile = "";
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	// Init the parameter for write log
	private static Logger Log = Logger.getLogger(TestBatch.class.getName());
	// Store value of color of last row in table report.
	private String strLastRowColor = "#C8B560";

	/**********************************************************************************************************
	 * ' Function Name: loadRefParameter ' Purpose: This function will load some
	 * ref parameter before the test run ' Inputs Parameters: ' Returns: '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void loadRefParameter() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("ref.properties"));
			strReportHTMLFile = prop.getProperty("strReportFile");
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: initHTMLReport ' Purpose: This function will generate
	 * the report and some informatin in title of report. ' Inputs Parameters: '
	 * Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void initHTMLReport() {
		String strOSName = "";
		String strOSVer = "";
		String strHostName = "";
		String strHostAddress = "";
		String strDateTime = "";
		String strApplication = "Wingman App";
		try {
			boolean envSystem = Boolean.valueOf(System.getProperty("envSystem"));
			if (envSystem) {
				strOSName = System.getProperty("deviceName");
				strOSVer = System.getProperty("platformVersion");
			} else {
				strOSName = System.getProperty("os.name");
				strOSVer = System.getProperty("os.version");
			}
			
			loadRefParameter();

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy _ HH:mm:ss");
			strDateTime = sdf.format(cal.getTime()).toString();

			// To identify the system
			InetAddress ownIP = InetAddress.getLocalHost();
			strHostName = ownIP.getHostName();
			strHostAddress = ownIP.getHostAddress();

			BufferedWriter out = new BufferedWriter(new FileWriter(
					strReportHTMLFile));
			out.write("==================================================================================================================<BR>\n");
            out.write("<font size=+1 color='#000000'>");
            out.write("<B>Application: \u00A0</B>" + strApplication + "<BR>\n");
            out.write("<B>Device Name: \u00A0</B>" + strOSName
                      + "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0<B>Platform:\u00A0</B>"
                      + strOSVer + "<BR>\n");
            out.write("<B>Host Name :\u00A0</B>" + strHostName
                      + "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0 <B>Host IP:\u00A0</B>"
                      + strHostAddress + "<BR>\n");
            out.write("<B>DateTime  :\u00A0</B>" + strDateTime + "<BR>\n");
            out.write("</font>\n");
            out.write("==================================================================================================================<BR><BR>\n");
            out.write("<table cellspacing=0 border=1 width=100%>\n");
            out.write("<tr>\n");
            out.write("<td width='10%'><B>Test Case ID</B></td>\n");
            out.write("<td width='50%'><B>Test Case Description</B></td>\n");
            out.write("<td width='10%'><B>Status</B></td>\n");
            out.write("<td width='40%'><B>Screenshot</B></td>\n");
            out.write("</tr>\n");

			out.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: createTableRowTestCase ' Purpose: This function will
	 * insert new row to next last row of table report as Test case. ' Inputs
	 * Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void createTableRowTestCase(String strTCID, String strTCDesc,
			boolean bStatus, String strCapture) {
		String strColor; // Color for status column
		String strRowColor; // Color for rows in table
		String strStatus; // Value for Status PASS or FAIL

		strRowColor = strLastRowColor;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					strReportHTMLFile, true));
			out.write("<tr>");
			out.write("<td width=10% BGCOLOR=" + strRowColor + ">" + strTCID
					+ "</td>\n");
			out.write("<td width=50% BGCOLOR=" + strRowColor + ">" + strTCDesc
					+ "</td>\n");
			if (bStatus) {
				strColor = "#009900";
				strStatus = "Pass";
			} else {
				strColor = "#FF0000";
				strStatus = "Fail";
			}
			if (strCapture.isEmpty()) {
				out.write("<td width=10% BGCOLOR=" + strRowColor
						+ "><font color=" + strColor + "><b>" + strStatus
						+ "</b></font></td>\n");
				strCapture = "\u00A0\u00A0";
			}

			else {
				out.write("<td width=10% BGCOLOR=" + strRowColor
						+ "><font color=" + strColor + "><b>" + strStatus
						+ "</b></font></td>\n");
			}
			out.write("<td width=40% BGCOLOR=" + strRowColor + ">" + strCapture
					+ "</td>\n");
			out.write("</tr>\n");
			out.close();
			if (strRowColor.contentEquals("#C8B560")) {
				strLastRowColor = "#ECD672";
			} else {
				strLastRowColor = "#C8B560";
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: createTableRowTestCase ' Purpose: This function will
	 * insert new row to next last row of table report as Test suite. ' Inputs
	 * Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void createTableRowTestSuite(String strTSID, String strTSDesc) {
		String strRowColor = "#C8D000";
		String strColor = "#FF0000";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					strReportHTMLFile, true));
			out.write("<tr>");
			out.write("<td width='10%' BGCOLOR=" + strRowColor
					+ "><font color='" + strColor + "'><B>" + strTSID
					+ "</B></td>\n");
			out.write("<td colspan='3' width='90%' BGCOLOR=" + strRowColor
					+ "><font color='" + strColor + "'><B>" + strTSDesc
					+ "</B></td>\n");
			out.write("</tr>\n");
			out.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: createTableRowResultTestSuite ' Purpose: This function
	 * will insert new row to next last row of table report as Test suite
	 * result. ' Inputs Parameters: JSONObject joResultTestSuite -- This store
	 * the result of testsuite as structure below. { "pass": <int>, "fail":
	 * "<int>, "notExecute": <int>, "totalTestcase": <int> } ' Returns: ' Author
	 * : ' Creation Date: /
	 **********************************************************************************************************/
	public void createTableRowResultTestSuite(JSONObject joResultTestSuite) {
		String strRowColor = "#777777";
		String strColor = "#FFFFFF";
		try {
			// Get the result of Test suite from JSONObject
			int intNumberOfPass = joResultTestSuite.getInt("pass");
			int intNumberOfFail = joResultTestSuite.getInt("fail");
			int intNumberOfNotExecute = joResultTestSuite
					.getInt("notExecute");

			String strDataPrint;
			strDataPrint = "Pass: " + intNumberOfPass + " --- Fail: "
					+ intNumberOfFail + " --- Not Execute: "
					+ intNumberOfNotExecute;

			BufferedWriter out = new BufferedWriter(new FileWriter(
					strReportHTMLFile, true));
			out.write("<tr>");
			out.write("<td colspan='4' width='100%' align='right' BGCOLOR="
					+ strRowColor + "><font color='" + strColor + "'><B>"
					+ strDataPrint + "</B></td>\n");
			out.write("</tr>\n");
			out.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: createTableRowFinalResult ' Purpose: This function will
	 * insert new row to next last row of table report as Test suite result. '
	 * Inputs Parameters: JSONObject joResultTestSuite -- This store the result
	 * of testsuite as structure below. { "pass": <int>, "fail": "<int>,
	 * "notExecute": "<int> } ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void createTableRowFinalResult(JSONObject joResultAllTestSuites) {
		String strRowColor = "#000000";
		String strColor = "#FFFFFF";
		try {
			// Get the result of Test suite from JSONObject
			int intNumberOfPass = joResultAllTestSuites.getInt("pass");
			int intNumberOfFail = joResultAllTestSuites.getInt("fail");
			int intNumberOfNotExecute = joResultAllTestSuites
					.getInt("notExecute");
			int intNumberOfTotalTestCase = intNumberOfPass + intNumberOfFail + intNumberOfNotExecute;
			String strNumberOfTotalTestCase = Integer
					.toString(intNumberOfTotalTestCase);

			String strDataPrint;
			strDataPrint = "Total Testcase: " + strNumberOfTotalTestCase
					+ " ----- Pass: " + intNumberOfPass + " --- Fail: "
					+ intNumberOfFail + "--- Not Execute: "
					+ intNumberOfNotExecute;

			BufferedWriter out = new BufferedWriter(new FileWriter(
					strReportHTMLFile, true));
			out.write("<tr> \u00A0</tr>\n");
			out.write("<tr>");
			out.write("<td colspan='4' width='100%' align='right' BGCOLOR="
					+ strRowColor + "><font color='" + strColor + "'><B>"
					+ strDataPrint + "</B></td>\n");
			out.write("</tr>\n");
			out.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: insertEndTime ' Purpose: This function will insert the
	 * End Time of test at the end of report ' Inputs Parameters: JSONObject
	 * joResultTestSuite -- This store the result of testsuite as structure
	 * below. { "pass": <int>, "fail": "<int>, "notExecute": "<int> } ' Returns:
	 * ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void insertEndTime() {
		String strRowColor = "#FFFFFF";
		String strColor = "#000000";
		String strDateTime;
		try {

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy _ HH:mm:ss");
			strDateTime = sdf.format(cal.getTime()).toString();

			BufferedWriter out = new BufferedWriter(new FileWriter(
					strReportHTMLFile, true));
			out.write("<tr> \u00A0</tr>\n");
			out.write("<tr>");
			out.write("<td colspan='4' width='100%' align='right' BGCOLOR="
					+ strRowColor + "><font color='" + strColor
					+ "'>End Time: " + strDateTime + "</td>\n");
			out.write("</tr>\n");
			out.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: captureScreenShot ' Purpose: This function will capture
	 * a screenshoot and save to screenshoot folder as .png file ' Inputs
	 * Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public String captureScreenShot() {
		String strPathFileName = "";
		String strFileName = "";
		Format formatter;
		String strDate = "";
		try {
			// Get the dir path
			File directory = new File(".");

			// get current date time with Date() to create unique file name
			formatter = new SimpleDateFormat("E, dd MMM yyyy__HH.mm.ss");
			// get current date time with Date()
			Date date = new Date();
			strDate = formatter.format(date);

			strFileName = strDate + ".png";
			strPathFileName = directory.getCanonicalPath() + "\\ScreenShots\\"
					+ strFileName;
			System.out.println(strFileName);

			// Capture the screen shot of the area of the screen defined by the
			// rectangle
			Robot robot = new Robot();
			BufferedImage bi = robot.createScreenCapture(new Rectangle(1440,
					900));
			ImageIO.write(bi, "png", new File(strPathFileName));
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return strFileName;
	}
}
