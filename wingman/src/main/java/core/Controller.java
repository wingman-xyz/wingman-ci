package core;

import io.appium.java_client.AppiumDriver;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;

public class Controller extends Common {
	// Ref parameter will be load from ref.properties file
	private String strTestCasesFileName = "";
	private String strDataFileName = "";
	private String strTestCasesFile = "";
	private String strDataFile = "";
	private String strSuiteSheetName = "";

	// Init the parameter for write log
	private static Logger Log = Logger.getLogger(TestBatch.class.getName());
	static HTMLResGenerator HTMLResGenerator = new HTMLResGenerator();

	protected WriteExcel writeExcel = new WriteExcel();
	protected ReadExcel readExcel = new ReadExcel();
	protected Keyword keyword = new Keyword();

	private Properties prop = new Properties();

	// Store the result of each steps in suite.
	/*
	 * joResultTestCaseDetails { "tcID": [ { "tsID": "<string>", "result":
	 * "<boolean>" }, ..., ], ..., }
	 */
	JSONObject joResultTestCaseDetails = new JSONObject();

	// Store the result of test case.
	/*
	 * joResultTestSuiteDetails { "tcID" <string>: "result" <boolean>, .... }
	 */
	JSONObject joResultTestSuiteDetails = new JSONObject();

	// Store the result of each test suite in TestSuites sheet
	/*
	 * joResultAllTestSuites { "testsuiteID" : { "pass": <int>, "fail": "<int>,
	 * "notExecute": "<int> }, ... }
	 */
	JSONObject joResultAllTestSuites = new JSONObject();

	public Controller() {
		loadRefParameter();
		writeExcel.setOutputFile(strTestCasesFileName, strDataFileName);
	}

	/**********************************************************************************************************
	 * ' Function Name: loadRefParameter ' Purpose: This function will load some
	 * ref parameter before the test run ' Inputs Parameters: ' Returns: '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	private void loadRefParameter() {
		Properties prop = new Properties();
		try {
			String strPath = "";
			prop.load(new FileInputStream("ref.properties"));
			strPath = prop.getProperty("strPathData");
			strTestCasesFile = strPath + prop.getProperty("strTestCasesFile");
			strDataFile = strPath + prop.getProperty("strDataFile");
			strTestCasesFileName = prop.getProperty("strTestCasesFile");
			strDataFileName = prop.getProperty("strDataFile");
			strSuiteSheetName = prop.getProperty("strSuiteSheetName");
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: getTestSuitesToExecute ' Purpose: This function will
	 * return the list of Test Suites will be execute (Y) in TestCases.xls '
	 * Inputs Parameters: ' Returns: JSONArray jaExecuteTestcases with structure
	 * as below [{ "execute": "<string>", "testsuiteID": "<string>",
	 * "description": "<string>", "suiteSheet": "<string>", "testcaseSheet":
	 * "<string>", }, ...., ] ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getTestSuitesToExecute() {
		/*
		 * [{ "execute": "<string>", "testsuiteID": "<string>", "description":
		 * "<string>", "suiteSheet": "<string>", "testcaseSheet": "<string>", },
		 * ...., ]
		 */
		JSONArray jaExecuteTestcases = new JSONArray();

		// (0)Execute (1)TestSuiteID (2)Description (3)SuiteSheet
		// (4)TestCaseSheet
		int intColumn = 5;

		try {
			String[][] tblData = readExcel.readInputData(strTestCasesFile,
					strSuiteSheetName, intColumn);

			int intRow = tblData.length;
			for (int intR = 0; intR < intRow; intR++) {
				String strExecute = tblData[intR][0];
				String strTestCaseID = tblData[intR][1];
				String strDescription = tblData[intR][2];
				String strSuiteSheet = tblData[intR][3];
				String strTestCaseSheet = tblData[intR][4];

				JSONObject joInputData = new JSONObject();
				joInputData.put("execute", strExecute);
				joInputData.put("testsuiteID", strTestCaseID);
				joInputData.put("description", strDescription);
				joInputData.put("suiteSheet", strSuiteSheet);
				joInputData.put("testcaseSheet", strTestCaseSheet);
				if (strExecute.contentEquals("Y"))
					jaExecuteTestcases.put(joInputData);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaExecuteTestcases;
	}

	/**********************************************************************************************************
	 * ' Function Name: getTestCasesToExecute ' Purpose: This function will
	 * return the list of TC will be execute (Y) in TestCases.xls ' Inputs
	 * Parameters: strTestCasesFile ' Returns: JSONArray jaExecuteTestcases with
	 * structure as below [{ "execute": "<string>", "testcaseID": "<string>",
	 * "description": "<string>", "dataID": "<string>", }, ...., ] ' Author : '
	 * Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getTestCasesToExecute(String strUseCaseSheet) {
		/*
		 * [{ "execute": "<string>", "testcaseID": "<string>", "description":
		 * "<string>", "dataID": "<string>", }, ..., ]
		 */
		JSONArray jaExecuteTestcases = new JSONArray();

		// (0)Execute (1)TestCase (2)Description (3)DataID
		int intColumn = 4;

		try {
			String[][] tblData = readExcel.readInputData(strTestCasesFile,
					strUseCaseSheet, intColumn);

			int intRow = tblData.length;
			for (int intR = 0; intR < intRow; intR++) {
				String strExecute = tblData[intR][0];
				String strTestCaseID = tblData[intR][1];
				String strDescription = tblData[intR][2];
				String strDataID = tblData[intR][3];

				JSONObject joInputData = new JSONObject();
				joInputData.put("execute", strExecute);
				joInputData.put("testcaseID", strTestCaseID);
				joInputData.put("description", strDescription);
				joInputData.put("dataID", strDataID);
				if (strExecute.contentEquals("Y"))
					jaExecuteTestcases.put(joInputData);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaExecuteTestcases;
	}

	/**********************************************************************************************************
	 * ' Function Name: getTestSteps ' Purpose: This function will get the all
	 * steps for each Test case in TestCases.xls ' Inputs Parameters:
	 * strTestCasesFile ' Returns: JSONArray jaExecuteSteps with structure as
	 * below [{ "teststepID": "<string>", "description": "<string>", "keyword":
	 * "<string>", "object": "<string>", "dataObject": "<string>", }, ...., ] '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getTestSteps(String strSheetName, String strTestCaseExecute) {
		/*
		 * [{ "testcaseID": "<string>", "teststepID": "<string>", "description":
		 * "<string>", "keyword": "<string>", "object": "<string>",
		 * "dataObject": "<string>", }, ..., ]
		 */
		JSONArray jaExecuteSteps = new JSONArray();

		// (0)TestCaseID (1)StepID (2)Description (3)Keyword (4)Object
		// (5)DataObject
		int intColumn = 6;

		try {
			String[][] tblData = readExcel.readInputData(strTestCasesFile,
					strSheetName, intColumn);

			int intRow = tblData.length;
			for (int intR = 0; intR < intRow; intR++) {
				String strTestCaseID = tblData[intR][0];
				String strTestStepID = tblData[intR][1];
				String strDescription = tblData[intR][2];
				String strKeyword = tblData[intR][3];
				String strObject = tblData[intR][4];
				String strDataObject = tblData[intR][5];

				if (strTestCaseExecute.contentEquals(strTestCaseID)) {
					JSONObject joTestcase = new JSONObject();
					joTestcase.put("testcaseID", strTestCaseID);
					joTestcase.put("teststepID", strTestStepID);
					joTestcase.put("description", strDescription);
					joTestcase.put("keyword", strKeyword);
					joTestcase.put("object", strObject);
					joTestcase.put("dataObject", strDataObject);

					jaExecuteSteps.put(joTestcase);
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaExecuteSteps;
	}

	/**********************************************************************************************************
	 * ' Function Name: getDataObjects ' Purpose: This function will get Data
	 * Object Name from Test Steps in TestCases.xls ' Inputs Parameters:
	 * strTestCasesFile ' Returns: JSONArray jaExecuteSteps with structure as
	 * below [{ "teststepID": "<string>", "description": "<string>", "keyword":
	 * "<string>", "object": "<string>", "dataObject": "<string>", }, ...., ] '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	public String[] getDataObjects(JSONArray jaTestSteps) {
		/*
		 * jaTestSteps [{ "teststepID": "<string>", "description": "<string>",
		 * "keyword": "<string>", "object": "<string>", "dataObject":
		 * "<string>", }, ..., ]
		 */
		String[] arrDataObject = null;
		try {
			int intLength = jaTestSteps.length();

			// Store number of Data object
			int intNumberOfDataObject = 0;

			// Get the number of Data Object
			for (int i = 0; i < intLength; i++) {
				JSONObject jo = new JSONObject();
				jo = jaTestSteps.getJSONObject(i);
				String strDataObject = jo.getString("dataObject").trim();
				if (!strDataObject.isEmpty())
					intNumberOfDataObject++;
			}
			arrDataObject = new String[intNumberOfDataObject];

			int intIncreaseNumber = 0;
			for (int i = 0; i < intLength; i++) {
				JSONObject jo = new JSONObject();
				jo = jaTestSteps.getJSONObject(i);
				String strDataObject = jo.getString("dataObject").trim();
				if (!strDataObject.isEmpty()) {
					arrDataObject[intIncreaseNumber] = strDataObject;
					intIncreaseNumber++;
				}

			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return arrDataObject;
	}

	/**********************************************************************************************************
	 * ' Function Name: getDataForEachTestCase ' Purpose: This function will Get
	 * the REAL DATA from dataobject for run Test cases in TestData.xls ' Inputs
	 * Parameters: strTestCasesFile ' Returns: JSONObject joExecuteData with
	 * structure as below { "dataObjectName" : "value",... } ' Author : '
	 * Creation Date: /
	 **********************************************************************************************************/
	public JSONObject getDataForEachTestCase(String strSheetName,
			String strDataID, JSONArray jaTestSteps, String[] arrDataObjects) {
		/*
		 * { "dataObjectName" : "value",... }
		 */
		JSONObject joExecuteData = new JSONObject();

		try {
			String[][] tblData = readExcel.readInputData(strDataFile,
					strSheetName);

			int intLength = arrDataObjects.length;
			int intNumberOfColumnData = readExcel.getNumberOfColumn(
					strDataFile, strSheetName);

			// Store the index of row data
			int intIndexOfRow = 0;

			// Get index of ROW to get data
			for (int i = 0; i < tblData.length; i++) {
				if (strDataID.contentEquals(tblData[i][0])) {
					intIndexOfRow = i;
					break;
				}
			}

			// Get Data and input to JSON Object
			for (int i = 0; i < intLength; i++) {
				String strDataObjectName = arrDataObjects[i];
				for (int j = 0; j < intNumberOfColumnData; j++) {
					String strHeader = tblData[0][j].toString().trim();
					if (strHeader.contentEquals(strDataObjectName)) {
						String strData = tblData[intIndexOfRow][j];
						joExecuteData.put(strDataObjectName, strData);
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return joExecuteData;
	}

	/**********************************************************************************************************
	 * ' Function Name: runTestCase ' Purpose: This function will run Testcase
	 * that had execute = Y ' Inputs Parameters: (WebDriver webDriver,
	 * JSONObject joTestCase) webDriver: WebDriver to run the test joTestCase:
	 * List of test case will be execute ' Returns: true or false true if the
	 * test case PASS false if the test case FAIL ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public boolean runTestCase(AppiumDriver<WebElement> webDriver, String strTestcaseSheet,
			JSONObject joTestCase) {
		/*
		 * jaTestSteps [{ "testcaseID": "<string>", "teststepID": "<string>",
		 * "description": "<string>", "keyword": "<string>", "object":
		 * "<string>", "dataObject": "<string>", }, ..., ]
		 */
		JSONArray jaTestSteps;

		boolean bResultTC = true;
		boolean bResultTS = true;

		JSONObject joData;
		JSONArray jaTestCase = new JSONArray();

		String strTestCaseID = "";
		String strDataID = "";

		try {
			prop.load(new FileInputStream("or.properties"));
			strTestCaseID = joTestCase.getString("testcaseID");
			strDataID = joTestCase.getString("dataID");

			// Get all steps of TC base on Test case ID then will execute
			jaTestSteps = getTestSteps(strTestcaseSheet, strTestCaseID);

			// Get list of Data Object
			String[] arrDataObjects = getDataObjects(jaTestSteps);

			// Get Data for TC
			joData = getDataForEachTestCase(strTestcaseSheet, strDataID,
					jaTestSteps, arrDataObjects);

			/*
			 * joData { "dataObjectName": "value", ... }
			 */
			int intLength = jaTestSteps.length();

			// Walk throught all test steps and execute
			for (int i = 0; i < intLength; i++) {
				JSONObject jo = jaTestSteps.getJSONObject(i);
				String strTeststepID = jo.getString("teststepID");
				String strKeyword = jo.getString("keyword").trim();
				String strObject = jo.getString("object").trim();
				String strDataObject = jo.getString("dataObject").trim();
				String strData = "";

				if (!strDataObject.isEmpty())
					strData = joData.getString(strDataObject);

				String oObject = prop.getProperty(strObject);

				if (!strObject.isEmpty() && oObject == null) {
					System.out
							.println("The Object Name: "
									+ strObject
									+ " does not existed in or.properties file, please check again!!!!!!");
				}
				
				if (strKeyword.compareTo("input") == 0)
					bResultTS = keyword.input(webDriver, oObject, strObject, strData);
				else if (strKeyword.compareTo("tap") == 0)
					bResultTS = keyword.tap(webDriver, oObject, strObject);
				else if (strKeyword.compareTo("verifyValue") == 0)
					bResultTS = keyword.verifyValue(webDriver, oObject, strObject, strData);
				else if (strKeyword.compareTo("tapOnPhoneNumber") == 0)
					bResultTS = keyword.tapOnPhoneNumber(webDriver, oObject, strData);
				else if (strKeyword.compareTo("inputPassword") == 0)
					bResultTS = keyword.inputPassword(webDriver, oObject, strData);
				else if (strKeyword.compareTo("verifyElememtIsExisted") == 0)
					bResultTS = keyword.verifyElememtIsExisted(webDriver, oObject, strObject);
				else if (strKeyword.compareTo("verifyElememtIsDisplayedOnScreen") == 0)
					bResultTS = keyword.verifyElememtIsDisplayedOnScreen(webDriver, oObject, strObject);
				else if (strKeyword.compareTo("verifyElememtIsEnabledOnScreen") == 0)
					bResultTS = keyword.verifyElememtIsEnabledOnScreen(webDriver, oObject, strObject);
				else if (strKeyword.compareTo("allowPermissionPopUp") == 0)
					bResultTS = keyword.allowPermissionPopUp(webDriver);
				else if (strKeyword.compareTo("getResourcePage") == 0)
					bResultTS = keyword.getResourcePage(webDriver);
				else if (strKeyword.compareTo("verifyElememtIsNotExisted") == 0)
					bResultTS = keyword.verifyElememtIsNotExisted(webDriver, oObject, strObject);
				else if (strKeyword.compareTo("verifyElememtIsNotDisplayedOnScreen") == 0)
					bResultTS = keyword.verifyElememtIsNotDisplayedOnScreen(webDriver, oObject, strObject);
				else if (strKeyword.compareTo("verifyElememtIsNotEnabledOnScreen") == 0)
					bResultTS = keyword.verifyElememtIsNotEnabledOnScreen(webDriver, oObject, strObject);
				else
					System.out
							.println("The keyword: "
									+ strKeyword
									+ " does not existed in controler, please check again!!!!!!");

				/*
				 * { "tsID": <string>, "result": "<boolean>" }
				 */
				JSONObject joTestStepsResult = new JSONObject();

				joTestStepsResult.put("tsID", strTeststepID);
				joTestStepsResult.put("result",String.valueOf(bResultTS));
				jaTestCase.put(joTestStepsResult);
				if (!bResultTS)
					bResultTC = false;

			} // -- Exit for
				// Store the result of test steps to write the result
			/*
			 * joResultTestCaseDetails { "tcID": [ { "tsID": <string>, "result":
			 * "<boolean>" }, ..., ], ..., }
			 */
			joResultTestCaseDetails.put(strTestCaseID, jaTestCase);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
			bResultTC = false;
		}
		return bResultTC;
	}

	/**********************************************************************************************************
	 * ' Function Name: runTestSuite ' Purpose: This function will run all
	 * Testcases that had execute = Y ' Inputs Parameters: (WebDriver webDriver)
	 * webDriver: WebDriver to run the test ' Returns: ' Author : ' Creation
	 * Date: /
	 **********************************************************************************************************/
	public void runTestSuite(AppiumDriver<WebElement> webDriver) {
		// Store the list of test cases will be execute
		/*
		 * jaTestCaseExecute [{ "execute": "<string>", "testcaseID": "<string>",
		 * "description": "<string>", "dataID": "<string>", }, ..., ]
		 */
		JSONArray jaTestCaseExecute;

		// Store the list of test suites will be execute
		/*
		 * jaTestSuiteExecute [{ "execute": "<string>", "testsuiteID":
		 * "<string>", "description": "<string>", "suiteSheet": "<string>",
		 * "testcaseSheet": "<string>", }, ..., ]
		 */
		JSONArray jaTestSuiteExecute;

		// Get list of test suites to be execute
		jaTestSuiteExecute = getTestSuitesToExecute();

		// Store the path and name of screenshoot in case fail.
		String strCapture = "";
		HTMLResGenerator.loadRefParameter();

		try {
			int intNumnerOfSuite = jaTestSuiteExecute.length();
			int intTotalNumberOfPass = 0;
			int intTotalNumberOfFail = 0;
			int intTotalNumberOfNotExecute = 0;

			HTMLResGenerator.initHTMLReport();
			// LOOP for Run all Test Suites
			for (int i = 0; i < intNumnerOfSuite; i++) {
				JSONObject joTestSuite;
				joTestSuite = jaTestSuiteExecute.getJSONObject(i);

				String strSuiteID = joTestSuite.getString("testsuiteID");
				String strDescription = joTestSuite.getString("description");
				String strSuiteSheet = joTestSuite.getString("suiteSheet");
				String strTestcaseSheet = joTestSuite
						.getString("testcaseSheet");

				String strRunningSuite = "===========================================================================================\n";
				strRunningSuite = strRunningSuite + "RUNNING TEST SUITE: "
						+ strSuiteID + " " + strDescription + "\n";
				strRunningSuite = strRunningSuite
						+ "===========================================================================================";
				Log.info(strRunningSuite);
				System.out.println(strRunningSuite);

				// Write test suite to report
				HTMLResGenerator.createTableRowTestSuite(strSuiteID,
						strDescription);

				// Get list of test cases to be execute
				jaTestCaseExecute = getTestCasesToExecute(strSuiteSheet);

				int intNumberOfTCs = jaTestCaseExecute.length();
				int intNumberOfPass = 0;
				int intNumberOfFail = 0;
				int intNumberOfNotExecute = 0;
				// LOOP for run all Test cases in test suite
				for (int j = 0; j < intNumberOfTCs; j++) {
					strCapture = "";
					boolean bResultTC = false;
					JSONObject joTestCase;
					joTestCase = jaTestCaseExecute.getJSONObject(j);

					String strTCID = joTestCase.getString("testcaseID");
					String strDesc = joTestCase.getString("description");
					String strRunningTC = "-------------------------------------------------------------------------------------------\n";
					strRunningTC = strRunningTC + "Running test case: "
							+ strTCID + " " + strDesc;
					Log.info(strRunningTC);
					System.out.println(strRunningTC);

					bResultTC = runTestCase(webDriver, strTestcaseSheet,
							joTestCase);
					if (bResultTC) {
						intNumberOfPass++;
						String strResult = "The Testcase " + strTCID + " PASS";
						System.out.println(strResult);
					} else {
						intNumberOfFail++;
						String strResult = "The Testcase " + strTCID + " FAIL";
						System.out.println(strResult);
						strCapture = HTMLResGenerator.captureScreenShot();
					}
					// Write result of test to report
					HTMLResGenerator.createTableRowTestCase(strTCID, strDesc,
							bResultTC, strCapture);
					// Store the result of test case.
					/*
					 * joResultTestSuiteDetails { "tcID" <string>: "result"
					 * <boolean>, .... }
					 */
					joResultTestSuiteDetails.put(strTCID, bResultTC);
				} // -- Exit for of run all test cases for al suite

				// Write result to TestCase Details sheet
				writeExcel.writeResultTestCaseDetails(strSuiteSheet,
						strTestcaseSheet, joResultTestCaseDetails);
				// Write result to TestSuite Details sheet
				writeExcel.writeResultTestSuiteDetails(strSuiteSheet,
						joResultTestSuiteDetails);
				// Set Empty again for new suite
				joResultTestCaseDetails = new JSONObject();
				joResultTestSuiteDetails = new JSONObject();

				// Get the number of test case not run.
				int intTotalTestCase = readExcel.getNumberOfRow(
						strTestCasesFile, strSuiteSheet);
				intNumberOfNotExecute = intTotalTestCase
						- (intNumberOfPass + intNumberOfFail) - 1;

				intTotalNumberOfPass += intNumberOfPass;
				intTotalNumberOfFail += intNumberOfFail;
				intTotalNumberOfNotExecute += intNumberOfNotExecute;

				// Prepare data to input to final result of test suite
				JSONObject joResultTestSuite = new JSONObject();
				joResultTestSuite.put("pass", intNumberOfPass);
				joResultTestSuite.put("fail", intNumberOfFail);
				joResultTestSuite.put("notExecute", intNumberOfNotExecute);

				// Write result of test suite to HTML file
				HTMLResGenerator
						.createTableRowResultTestSuite(joResultTestSuite);
				/*
				 * joResultAllTestSuites { "testsuiteID": { "pass": <int>,
				 * "fail": "<int>, "notExecute": "<int> } }
				 */
				joResultAllTestSuites.put(strSuiteID, joResultTestSuite);
			} // -- Exit for Run all Test Suites
			writeExcel.writeResultTestSuitesSheet(strSuiteSheetName,
					joResultAllTestSuites);

			int intTotalTestCases = intTotalNumberOfPass + intTotalNumberOfFail
					+ intTotalNumberOfNotExecute;
			JSONObject joTotalResultOfTest = new JSONObject();
			joTotalResultOfTest.put("pass", intTotalNumberOfPass);
			joTotalResultOfTest.put("fail", intTotalNumberOfFail);
			joTotalResultOfTest.put("notExecute", intTotalNumberOfNotExecute);
			joTotalResultOfTest.put("totalTestcase", intTotalTestCases);
			HTMLResGenerator.createTableRowFinalResult(joTotalResultOfTest);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}
}
