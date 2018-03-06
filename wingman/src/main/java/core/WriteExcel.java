package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class WriteExcel {

	private String inputFile;
	private String resultFile;
	private String backupResultFile;

	private String dataFile;
	private String backupDataFile;

	private ReadExcel readExcel = new ReadExcel();
	// Init the parameter for write log
	private static Logger Log = Logger.getLogger(TestBatch.class.getName());
	private static Properties prop = new Properties();

	/**********************************************************************************************************
	 * ' Function Name: getColumnOfResultTestCaseDetails ' Purpose: This
	 * function will get intColumnOfResultTestCaseDetails from re.properties and
	 * return the value This is a position of Result column in test case details
	 * of test case file. ' Inputs Parameters: ' Returns: intColumn -- The
	 * position of Result column ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public int getColumnOfResultTestCaseDetails() {
		int intColumn = 0;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("ref.properties"));
			intColumn = Integer.parseInt(prop
					.getProperty("intColumnOfResultTestCaseDetails"));
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intColumn;
	}

	/**********************************************************************************************************
	 * ' Function Name: getColumnOfResultTestSuiteDetails ' Purpose: This
	 * function will get intColumnOfResultTestSuiteDetails from ref.properties
	 * and return the value This is a position of Result column in test suite
	 * details of test case file. ' Inputs Parameters: ' Returns: intColumn --
	 * The position of Result column ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public int getColumnOfResultTestSuiteDetails() {
		int intColumn = 0;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("ref.properties"));
			intColumn = Integer.parseInt(prop
					.getProperty("intColumnOfResultTestSuiteDetails"));
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intColumn;
	}

	/**********************************************************************************************************
	 * ' Function Name: getColumnOfPass ' Purpose: This function will get
	 * intColumnOfPass from ref.properties and return the value This is a
	 * position of PASS column in test suites sheet of test case file ' Inputs
	 * Parameters: ' Returns: intColumn -- The position of PASS column ' Author
	 * : ' Creation Date: /
	 **********************************************************************************************************/
	public int getColumnOfPass() {
		int intColumn = 0;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("ref.properties"));
			intColumn = Integer.parseInt(prop.getProperty("intColumnOfPass"));
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intColumn;
	}

	/**********************************************************************************************************
	 * ' Function Name: setOutputFile ' Purpose: This function will initial the
	 * path of test case file. ' Inputs Parameters: The test case file. '
	 * Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void setOutputFile(String inputFile, String dataFile) {
		try {
			String strPath = "";
			prop.load(new FileInputStream("ref.properties"));
			strPath = prop.getProperty("strPathData");

			this.inputFile = strPath + inputFile;
			this.resultFile = strPath + "result" + inputFile;
			this.backupResultFile = strPath + "backupResult" + inputFile;

			this.dataFile = strPath + dataFile;
			this.backupDataFile = strPath + "backup" + dataFile;
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: copyFile ' Purpose: This function will copy test case
	 * file to other file with prefix "result" ' Inputs Parameters: ' Returns: '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void copyFile() throws IOException, WriteException, BiffException {
		try {
			Workbook wbInputFile = Workbook.getWorkbook(new File(inputFile));
			WritableWorkbook copyInputFile = Workbook.createWorkbook(new File(
					resultFile), wbInputFile);

			Workbook wbDataFile = Workbook.getWorkbook(new File(dataFile));
			WritableWorkbook copyDataFile = Workbook.createWorkbook(new File(
					backupDataFile), wbDataFile);

			copyInputFile.write();
			copyInputFile.close();
			wbInputFile.close();

			copyDataFile.write();
			copyDataFile.close();
			wbDataFile.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: backupResultFile ' Purpose: This function will backup
	 * the result test case file to other file with prefix "backup" ' Inputs
	 * Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void backupResultFile() throws IOException, WriteException,
			BiffException {
		try {
			Workbook wbInput = Workbook.getWorkbook(new File(resultFile));

			WritableWorkbook copy = Workbook.createWorkbook(new File(
					backupResultFile), wbInput);

			copy.write();
			copy.close();
			wbInput.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: backupDataFile ' Purpose: This function will backup the
	 * test data file to other file with prefix "backup" ' Inputs Parameters: '
	 * Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void backupDataFile() throws IOException, WriteException,
			BiffException {
		try {
			Workbook wbInput = Workbook.getWorkbook(new File(dataFile));

			WritableWorkbook copy = Workbook.createWorkbook(new File(
					backupDataFile), wbInput);

			copy.write();
			copy.close();
			wbInput.close();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: deleteFile ' Purpose: This function will Delete file
	 * with specific name ' Inputs Parameters: strFileName --- The full path of
	 * file that we want to delete. ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void deleteFile(String strFileName) {
		String strPathFileName = "";
		try {
			// Get the dir path
			File directory = new File(".");
			strPathFileName = directory.getCanonicalPath() + "\\" + strFileName;

			File fFileName = new File(strPathFileName);
			// Make sure the file or directory exists and isn't write protected
			if (!fFileName.exists()) {
				throw new IllegalArgumentException(
						"Delete: no such file or directory: " + strPathFileName);
			}
			if (!fFileName.canWrite()) {
				throw new IllegalArgumentException("Delete: write protected: "
						+ strPathFileName);
			}

			boolean bSuccess = fFileName.delete();
			if (!bSuccess) {
				System.out.println("Deletion failed: " + strPathFileName);
			} else {
				System.out.println("File deleted: " + strPathFileName);
			}
		} catch (IllegalArgumentException IllEx) {
			System.out.println(IllEx.getMessage());
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getCause().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: deleteBackupFile ' Purpose: This function will deelete
	 * backupFile and backupResultFile ' Inputs Parameters: ' Returns: ' Author
	 * : ' Creation Date: /
	 **********************************************************************************************************/
	public void deleteBackupFile() {
		try {
			deleteFile(backupDataFile);
			deleteFile(backupResultFile);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: writeResultTestCaseDetails ' Purpose: This function will
	 * write down the test result to Testcase Details sheet ' Inputs Parameters:
	 * String strSuiteSheet, String strTestCaseSheet, JSONObject
	 * joResultTestCaseDetails strSuiteSheet: Sheet name of test suite details
	 * sheet strTestCaseSheet: Sheet name of test case details sheet
	 * joResultTestCaseDetails: JSONObject with structure as below { "tcID": [ {
	 * "tsID": <string>, "result": "<boolean>" }, ......, ], ...., } ' Returns:
	 * ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void writeResultTestCaseDetails(String strSuiteSheet,
			String strTestCaseSheet, JSONObject joResultTestCaseDetails)
			throws FileNotFoundException, IOException, RowsExceededException,
			WriteException, BiffException {
		/*
		 * joResultTestSuiteDetails { "tcID": [ { "tsID": <string>, "result":
		 * "<boolean>" }, ......, ], ...., }
		 */
		// Initial wbInput
		// the excel sheet which contains data
		File fInput = new File(backupResultFile);
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		Workbook wbInput = Workbook.getWorkbook(fInput, ws);

		// the excel sheet where data is to copied
		File outputWorkbook = new File(resultFile);
		WritableWorkbook copyWB = Workbook.createWorkbook(outputWorkbook,
				wbInput);
		WritableSheet waInput = copyWB.getSheet(strTestCaseSheet);

		/*
		 * [{ "testcaseID": "<string>", "teststepID": "<string>" }, ...., ]
		 */
		JSONArray jaTestStepsExecuted = new JSONArray(); // This will store the
															// list TC that
															// execute = Y

		/*
		 * [{ "testcaseID": "<string>", "teststepID": "<string>" }, ...., ]
		 */
		JSONArray jaTestSteps = new JSONArray(); // This will store the list all
													// TCs in TEST CASE DETAILS
													// sheet

		int intColumnResult = getColumnOfResultTestCaseDetails(); // get the
																	// position
																	// of Column
																	// result in
																	// test case
																	// details
																	// sheet of
																	// test case
																	// file
		int intRow = readExcel.getNumberOfRow(inputFile, strTestCaseSheet); // Store
																			// the
																			// number
																			// of
																			// row
																			// in
																			// excel
																			// sheet
		try {
			jaTestSteps = readExcel.getListOfTestSteps(inputFile,
					strTestCaseSheet);
			jaTestStepsExecuted = readExcel.getListOfTestStepsExecuted(
					inputFile, strSuiteSheet, strTestCaseSheet);

			JSONArray jaTestCaseExecute = readExcel.getTestCasesToExecute(
					inputFile, strSuiteSheet);
			String strTestCaseExecute = jaTestCaseExecute.toString();

			// Go through all row of test details.
			for (int k = 0; k < intRow - 1; k++) {
				JSONObject joTestCase = jaTestSteps.getJSONObject(k);
				String strTCID = joTestCase.getString("testcaseID");
				String strTSID = joTestCase.getString("teststepID");
				if (strTestCaseExecute.contains(strTCID)) {
					int intLenght = jaTestStepsExecuted.length();
					// Go through all row of test details that had been
					// executed.
					for (int i = 0; i < intLenght; i++) {
						JSONObject joTestCaseExecuted = jaTestStepsExecuted
								.getJSONObject(i);
						String strTCIDExecuted = joTestCaseExecuted
								.getString("testcaseID");
						String strTSIDExecuted = joTestCaseExecuted
								.getString("teststepID");

						if (strTCID.contentEquals(strTCIDExecuted)
								&& strTSID.contentEquals(strTSIDExecuted)) {
							JSONArray jaTestStepsResult = new JSONArray();

							/*
							 * [ { "tsID": <string>, "result": "<boolean>" },
							 * ......, ]
							 */
							jaTestStepsResult = joResultTestCaseDetails
									.getJSONArray(strTCIDExecuted);

							int intLenghtOfTC = jaTestStepsResult.length();
							for (int j = 0; j < intLenghtOfTC; j++) {
								JSONObject joTestStepResult = jaTestStepsResult
										.getJSONObject(j);
								String strTSIDActual = joTestStepResult
										.getString("tsID");
								String strResult = joTestStepResult
										.getString("result");
								String strResultToWrite;

								// Test step match with result
								if (strTSIDExecuted
										.contentEquals(strTSIDActual)) {
									// Set Format for FONT
									WritableFont setColor = new WritableFont(
											WritableFont.ARIAL);
									setColor.setBoldStyle(WritableFont.BOLD);
									if (strResult.contentEquals("false")) {
										setColor.setColour(Colour.RED);
										strResultToWrite = "FAIL";
									} else {
										setColor.setColour(Colour.BLUE);
										strResultToWrite = "PASS";
									}
									WritableCellFormat cellFormat = new WritableCellFormat(
											setColor);

									// Set Border for CELL
									cellFormat.setBorder(Border.ALL,
											BorderLineStyle.THIN);

									// Set value for CELL
									Label lResult = new Label(intColumnResult,
											k + 1, strResultToWrite, cellFormat);
									waInput.addCell(lResult);
									break;
								} // End if
							} // Exit For
							break;
						} // End if
					} // Exit for walkthrought all test case that executed
				} // End if
			} // Exit for walkthrought all row in Test case details sheet.
			copyWB.write();
			copyWB.close();
			wbInput.close();
			backupResultFile();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: writeResultTestSuiteDetails ' Purpose: This function
	 * will write down the test result to Testcase Details sheet ' Inputs
	 * Parameters: String strSuiteSheet, JSONObject joResultTestSuiteDetails
	 * strSuiteSheet: Sheet name of test suite details sheet
	 * joResultTestSuiteDetails: JSONObject with structure as below { "tcID"
	 * <string>: "result" <boolean>, .... } ' Returns: ' Author : ' Creation
	 * Date: /
	 **********************************************************************************************************/
	public void writeResultTestSuiteDetails(String strSuiteSheet,
			JSONObject joResultTestSuiteDetails) throws FileNotFoundException,
			IOException, RowsExceededException, WriteException, BiffException {
		/*
		 * joResultTestSuiteDetails { "tcID" <string>: "result" <boolean>, ....
		 * }
		 */
		// Initial wbInput
		File fInput = new File(backupResultFile); // the excel sheet which
													// contains data
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		Workbook wbInput = Workbook.getWorkbook(fInput, ws);

		File outputWorkbook = new File(resultFile); // the excel sheet where
													// data is to copied
		WritableWorkbook copyWB = Workbook.createWorkbook(outputWorkbook,
				wbInput);
		WritableSheet waInput = copyWB.getSheet(strSuiteSheet);

		/*
		 * [ {
		 * 
		 * "execute": "<string>", "testcaseID": "<string>"` } , ...., ]
		 */
		JSONArray jaTestCases = new JSONArray(); // This will store the list all
													// TCs in TEST SUITE DETAILS
													// sheet

		int intColumnResult = getColumnOfResultTestSuiteDetails(); // get the
																	// position
																	// of Column
																	// result in
																	// test
																	// suite
																	// details
																	// sheet of
																	// test case
																	// file
		int intRow = readExcel.getNumberOfRow(inputFile, strSuiteSheet); // Store
																			// the
																			// number
																			// of
																			// row
																			// in
																			// excel
																			// sheet
		try {
			jaTestCases = readExcel
					.getListOfTestCases(inputFile, strSuiteSheet);

			// Go through all row of test suite details.
			for (int k = 0; k < intRow - 1; k++) {
				JSONObject joTestCase = jaTestCases.getJSONObject(k);
				String strExecute = joTestCase.getString("execute");
				String strTCID = joTestCase.getString("testcaseID");

				if (strExecute.contentEquals("Y")) {
					String strResultToWrite;
					boolean booleanResult = joResultTestSuiteDetails.getBoolean(strTCID);
					// Set Format for FONT
					WritableFont setColor = new WritableFont(WritableFont.ARIAL);
					setColor.setBoldStyle(WritableFont.BOLD);

					if (!booleanResult) {
						setColor.setColour(Colour.RED);
						strResultToWrite = "FAIL";
					} else {
						setColor.setColour(Colour.BLUE);
						strResultToWrite = "PASS";
					}
					WritableCellFormat cellFormat = new WritableCellFormat(
							setColor);

					// Set Border for CELL
					cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

					// Set value for CELL
					Label lResult = new Label(intColumnResult, k + 1,
							strResultToWrite, cellFormat);
					waInput.addCell(lResult);
				}
			} // Exit for walkthrought all row in Test case details sheet.
			copyWB.write();
			copyWB.close();
			wbInput.close();
			backupResultFile();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}	

	/**********************************************************************************************************
	 * ' Function Name: writeResultTestSuitesSheet ' Purpose: This function will
	 * write down the test result to Testsuites sheet ' Inputs Parameters:
	 * String strSuiteSheet, JSONObject joResultTestSuiteDetails strSuiteSheet:
	 * Sheet name of test suite details sheet joResultAllTestSuites: JSONObject
	 * with structure as below joResultAllTestSuites { "testsuiteID" : { "pass":
	 * <int>, "fail": "<int>, "notExecute": "<int> }, ... } ' Returns: ' Author
	 * : ' Creation Date: /
	 **********************************************************************************************************/
	public void writeResultTestSuitesSheet(String strTestSuitesSheet,
			JSONObject joResultAllTestSuites) throws FileNotFoundException,
			IOException, RowsExceededException, WriteException, BiffException {
		// Initial wbInput
		// the excel sheet which contains data
		File fInput = new File(backupResultFile);
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		Workbook wbInput = Workbook.getWorkbook(fInput, ws);

		// the excel sheet where data is to copied
		File outputWorkbook = new File(resultFile);
		WritableWorkbook copyWB = Workbook.createWorkbook(outputWorkbook,
				wbInput);
		WritableSheet waInput = copyWB.getSheet(strTestSuitesSheet);

		// This will store the list all test suites in TestSuites sheet
		/*
		 * [ { "execute": "<string>", "testsuiteID": "<string>"` } , ...., ]
		 */
		JSONArray jaTestSuites = new JSONArray();

		// get the position of Column Pass in testsuites sheet of test case file
		int intColumnResult = getColumnOfPass();

		// Store the number of row in excel sheet
		int intRow = readExcel.getNumberOfRow(inputFile, strTestSuitesSheet);
		try {
			jaTestSuites = readExcel.getListOfTestSuites(inputFile,
					strTestSuitesSheet);

			// Go through all row of testsuites sheet
			for (int k = 0; k < intRow - 1; k++) {
				JSONObject joTestCase = jaTestSuites.getJSONObject(k);
				String strExecute = joTestCase.getString("execute");
				String strTestSuiteID = joTestCase.getString("testsuiteID");

				if (strExecute.contentEquals("Y")) {
					JSONObject joTestSuiteResult = new JSONObject();
					/*
					 * { "pass": <int>, "fail": "<int>, "notExecute": "<int> },
					 * ...
					 */
					joTestSuiteResult = joResultAllTestSuites
							.getJSONObject(strTestSuiteID);
					int intNumberOfPass = joTestSuiteResult
							.getInt("pass");
					int intNumberOfFail = joTestSuiteResult
							.getInt("fail");
					int intNumberOfNotExecute = joTestSuiteResult
							.getInt("notExecute");

					WritableCellFormat cellFormat = new WritableCellFormat();

					// Set Border for CELL
					cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

					// Set value for CELL
					Label lPass = new Label(intColumnResult, k + 1,
							Integer.toString(intNumberOfPass), cellFormat);
					Label lFail = new Label(intColumnResult + 1, k + 1,
							Integer.toString(intNumberOfFail), cellFormat);
					Label lnotExecute = new Label(intColumnResult + 2, k + 1,
							Integer.toString(intNumberOfNotExecute), cellFormat);
					waInput.addCell(lPass);
					waInput.addCell(lFail);
					waInput.addCell(lnotExecute);

				}
			} // Exit for walkthrought all row in Test case details sheet.
			copyWB.write();
			copyWB.close();
			wbInput.close();
			backupResultFile();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: updateValue ' Purpose: This function will write down the
	 * test result to Testsuites sheet ' Inputs Parameters: String sheetTarget,
	 * String strValue, String strDTID, String strColumnName sheetTarget: Sheet
	 * name of sheet that you want to udpate value strValue: Value to be update
	 * to cell strColumnName: The name of column to update strDTID: Data ID of
	 * row to update ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void updateValue(String sheetTarget, String strValue,
			String strDTID, String strColumnName) throws FileNotFoundException,
			IOException, RowsExceededException, WriteException, BiffException {
		try {
			// Initial wbInput
			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));

			// the excel sheet which contains data
			File fInput = new File(backupDataFile);
			Workbook wbInput = Workbook.getWorkbook(fInput, ws);

			// the excel sheet where data is to copied
			File outputWorkbook = new File(dataFile);
			WritableWorkbook copyWB = Workbook.createWorkbook(outputWorkbook,
					wbInput);
			WritableSheet waInput = copyWB.getSheet(sheetTarget);

			// Get the postion of Data ID (strDTID)
			int intRow = readExcel.getPositionOfRow(backupDataFile,
					sheetTarget, strDTID);
			// Get the postion of Column Name (strColumnName)
			int intColumn = readExcel.getPositionOfColumn(backupDataFile,
					sheetTarget, strColumnName);

			// Set value for CELL
			Label lUpdateValue = new Label(intColumn, intRow, strValue);
			waInput.addCell(lUpdateValue);

			copyWB.write();
			copyWB.close();
			wbInput.close();
			backupDataFile();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
		}
	}
}
