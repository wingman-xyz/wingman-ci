package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class ReadExcel {
	// Init the parameter for write log
	private static Logger Log = Logger.getLogger(TestBatch.class.getName());

	/**********************************************************************************************************
	 * ' Function Name: getValue ' Purpose: This function will the value of
	 * specific column and row ' Inputs Parameters: String strFileName, String
	 * strSheetName, int intColumn, int intRow strFileName: Excel file store the
	 * value we want to get strSheetname: Excel sheet store the value intColumn:
	 * Specific column store the value intRow: Specific row store the value '
	 * Returns: strValue - Return the value that get from input parameters. '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	public String getValue(String strFileName, String strSheetName,
			int intColumn, int intRow) throws FileNotFoundException {
		FileInputStream fs = new FileInputStream(new File(strFileName));
		WorkbookSettings ws = null;
		Workbook workbook = null;
		Sheet s = null;

		String strValue = null;
		try {
			ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			workbook = Workbook.getWorkbook(fs, ws);

			// Getting Default Sheet i.e. 0
			s = workbook.getSheet(strSheetName);

			strValue = s.getCell(intColumn, intRow).getContents();
			workbook.close();
			fs.close();
		} catch (IOException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		} catch (BiffException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return strValue;
	}

	/**********************************************************************************************************
	 * ' Function Name: readInputData ' Purpose: This function will read and
	 * return all rows data exclude header in file ' Inputs Parameters: String
	 * strFileName, String strSheetName, int intColumn strFileName: Excel file
	 * store the value we want to get strSheetname: Excel sheet store the value
	 * intColumn: The total column in sheet name ' Returns: [][]tblData - Return
	 * the value that get from input parameters. ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public String[][] readInputData(String strFileName, String strSheetName,
			int intColumn) throws FileNotFoundException {
		String[][] tblData = null;
		FileInputStream fs = new FileInputStream(new File(strFileName));
		WorkbookSettings ws = null;
		Workbook workbook = null;
		Sheet sheet = null;
		Cell rowData[] = null;
		int intRowCount = '0';

		try {
			ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			workbook = Workbook.getWorkbook(fs, ws);

			// Getting Default Sheet i.e. 0
			sheet = workbook.getSheet(strSheetName);

			// Total Total No Of Rows in Sheet, will return you no of rows that
			// are occupied with some data
			intRowCount = sheet.getRows();
			tblData = new String[intRowCount - 1][intColumn];

			int intR = 0, intC = 0;
			// Reading Individual Row Content
			for (int i = 1; i < intRowCount; i++, intR++) {
				// Get Individual Row
				rowData = sheet.getRow(i);
				String strData = rowData[0].getContents();
				if (strData != null) {
					intC = 0;
					for (int j = 0; j < intColumn; j++, intC++) {
						// System.out.println(rowData[j].getContents());
						tblData[intR][intC] = rowData[j].getContents();
					}
				}
			}
			workbook.close();
		} catch (IOException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		} catch (BiffException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return (tblData);
	}

	/**********************************************************************************************************
	 * ' Function Name: readInputData ' Purpose: This function will read and
	 * return all rows data include header in file ' Inputs Parameters: String
	 * strFileName, String strSheetName, int intColumn strFileName: Excel file
	 * store the value we want to get strSheetname: Excel sheet store the value
	 * intColumn: The total column in sheet name ' Returns: [][]tblData - Return
	 * the value that get from input parameters. ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public String[][] readInputData(String strFileName, String strSheetName) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;

		String[][] tblData = null;

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSheetName);
			// Loop over column and lines

			int intRowCount = sheet.getRows();
			int intColumnCount = sheet.getColumns();

			tblData = new String[intRowCount][intColumnCount];

			for (int i = 0; i < intRowCount; i++) {
				String strData = sheet.getCell(0, i).getContents();
				if (strData != null) {
					for (int j = 0; j < intColumnCount; j++) {
						Cell cell = sheet.getCell(j, i);
						tblData[i][j] = cell.getContents();
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return (tblData);
	}

	/**********************************************************************************************************
	 * ' Function Name: getTestCasesToExecute ' Purpose: This function will
	 * return the list of TC will be execute (Y) in TestCases.xls '
	 * Inputs Parameters: String strFileName, String strSuiteSheet strFileName:
	 * Excel file store the value we want to get strSuiteSheet: Excel sheet
	 * store the value, this must be Suite sheet. ' Returns: JSONArray
	 * jaExecuteTestcases with structure as below [{ "execute": "<string>",
	 * "testcaseID": "<string>", "description": "<string>", "dataID":
	 * "<string>", }, ...., ] ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getTestCasesToExecute(String strFileName,
			String strSuiteSheet) {
		// ["testcaseID 001", "testcaseID 002" "testcaseID 003", ....]
		JSONArray jaExecuteTestcases = new JSONArray();

		// (0)Execute (1)TestCase (2)Description (3)DataID
		int intColumn = 4;
		try {
			String[][] tblData = readInputData(strFileName, strSuiteSheet,
					intColumn);

			int intRow = tblData.length;
			for (int intR = 0; intR < intRow; intR++) {
				String strExecute = tblData[intR][0];
				String strTestCaseID = tblData[intR][1];

				if (strExecute.contentEquals("Y"))
					jaExecuteTestcases.put(strTestCaseID);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaExecuteTestcases;
	}

	/**********************************************************************************************************
	 * ' Function Name: getListOfTestStepsExecuted ' Purpose: This function will
	 * read and return list of TestSteps that Executed (Just get TCID and TSID)
	 * ' Inputs Parameters: String strFileName, String strSuiteSheet, String
	 * strTestCaseSheet strFileName: Excel file store the value we want to get
	 * strSuiteSheet: Excel sheet store the value, this must be test suite
	 * details sheet. strTestCaseSheet: Excel sheet store the value, this must
	 * be test case details sheet. ' Returns: JSONArray jaTestCases -- JSONArray
	 * with structure as below [ { "testcaseID": "<string>", "teststepID":
	 * "<string>" }, ...., ] ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getListOfTestStepsExecuted(String strFileName,
			String strSuiteSheet, String strTestCaseSheet) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;

		// This will store the list TC that execute = Y
		/*
		 * [{ "testcaseID": "<string>", "teststepID": "<string>" }, ...., ]
		 */
		JSONArray jaTestSteps = new JSONArray();

		JSONArray jaTestCaseExecute = getTestCasesToExecute(strFileName,
				strSuiteSheet);
		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strTestCaseSheet);
			// Loop over column and lines

			int intRowCount = sheet.getRows();

			for (int i = 1; i < intRowCount; i++) {
				/*
				 * (0)TestCaseID (1)StepID (2)Description Here we just get
				 * TestcaseID and StepID
				 */
				Cell cellTestCaseID = sheet.getCell(0, i);
				String strTestCaseID = cellTestCaseID.getContents();

				for (int j = 0; j < jaTestCaseExecute.length(); j++) {
					String strTestCaseExecuted = jaTestCaseExecute.getString(j);
					if (strTestCaseID.contentEquals(strTestCaseExecuted)) {
						Cell cellTCID = sheet.getCell(0, i);
						Cell cellTSID = sheet.getCell(1, i);

						String strTCID = cellTCID.getContents();
						String strTSID = cellTSID.getContents();
						JSONObject joTestCase = new JSONObject();
						joTestCase.put("testcaseID", strTCID);
						joTestCase.put("teststepID", strTSID);
						jaTestSteps.put(joTestCase);
						break; // Break for loop
					}
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaTestSteps;
	}

	/**********************************************************************************************************
	 * ' Function Name: getListOfTestSteps ' Purpose: This function will read
	 * and return all TestSteps in excel sheet (Just get TCID and TSID) ' Inputs
	 * Parameters: String strFileName, String strTestCaseSheet strFileName:
	 * Excel file store the value we want to get strTestCaseSheet: Excel sheet
	 * store the value, this must be test case details sheet. ' Returns:
	 * JSONArray jaTestCases -- JSONArray with structure as below [ {
	 * "testcaseID": "<string>", "teststepID": "<string>" }, ...., ] ' Author :
	 * ' Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getListOfTestSteps(String strFileName,
			String strTestCaseSheet) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;

		// This will store the list TC that execute = Y
		/*
		 * [{ "testcaseID": "<string>", "teststepID": "<string>" }, ...., ]
		 */
		JSONArray jaTestSteps = new JSONArray();

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strTestCaseSheet);
			// Loop over column and lines

			int intRowCount = sheet.getRows();

			for (int i = 1; i < intRowCount; i++) {
				/*
				 * (0)TestCaseID (1)StepID (2)Description Here we just get
				 * TestcaseID and StepID
				 */
				Cell cellTCID = sheet.getCell(0, i);
				Cell cellTSID = sheet.getCell(1, i);

				String strTCID = cellTCID.getContents();
				String strTSID = cellTSID.getContents();
				JSONObject joTestCase = new JSONObject();
				joTestCase.put("testcaseID", strTCID);
				joTestCase.put("teststepID", strTSID);
				jaTestSteps.put(joTestCase);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaTestSteps;
	}

	/**********************************************************************************************************
	 * ' Function Name: getListOfTestCases ' Purpose: This function will read
	 * and return all TestCase in Suite sheet (Just get Execute and TestCaseID)
	 * ' Inputs Parameters: String strFileName, String strSuiteSheet
	 * strFileName: Excel file store the value we want to get strSuiteSheet:
	 * Excel sheet store the value, this must be test suite details sheet. '
	 * Returns: JSONArray jaTestCases -- JSONArray with structure as below [ {
	 * "execute": "<string>", "testcaseID": "<string>" }, ...., ] ' Author : '
	 * Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getListOfTestCases(String strFileName, String strSuiteSheet) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;

		// This will store the list TC that execute = Y
		/*
		 * [{ "execute": "<string>", "testcaseID": "<string>" }, ...., ]
		 */
		JSONArray jaTestCases = new JSONArray();

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSuiteSheet);
			// Loop over column and lines

			int intRowCount = sheet.getRows();
			for (int i = 1; i < intRowCount; i++) {
				/*
				 * (0)Execute (1)TestCaseID (2)Description Here we just get
				 * (0)Execute (1)TestCaseID
				 */
				Cell cellExecute = sheet.getCell(0, i);
				Cell cellTCID = sheet.getCell(1, i);

				String strExecute = cellExecute.getContents();
				String strTCID = cellTCID.getContents();
				JSONObject joTestCase = new JSONObject();
				joTestCase.put("execute", strExecute);
				joTestCase.put("testcaseID", strTCID);
				jaTestCases.put(joTestCase);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaTestCases;
	}

	/**********************************************************************************************************
	 * ' Function Name: getListOfTestSuites ' Purpose: This function will read
	 * and return all TestSuites in TestSuites sheet (Just get Execute and
	 * TestSuiteID) ' Inputs Parameters: String strFileName, String
	 * strSuiteSheet strFileName: Excel file store the value we want to get '
	 * Returns: JSONArray jaTestCases -- JSONArray with structure as below [ {
	 * "execute": "<string>", "testsuiteID": "<string>" }, ...., ] ' Author : '
	 * Creation Date: /
	 **********************************************************************************************************/
	public JSONArray getListOfTestSuites(String strFileName,
			String strSuiteSheet) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;

		// This will store the list TC that execute = Y
		/*
		 * [{ "execute": "<string>", "testsuiteID": "<string>" }, ...., ]
		 */
		JSONArray jaTestSuites = new JSONArray();

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSuiteSheet);
			// Loop over column and lines

			int intRowCount = sheet.getRows();
			for (int i = 1; i < intRowCount; i++) {
				/*
				 * (0)Execute (1)TestSuiteID (2)Description Here we just get
				 * (0)Execute (1)TestSuiteID
				 */
				Cell cellExecute = sheet.getCell(0, i);
				Cell cellTCID = sheet.getCell(1, i);

				String strExecute = cellExecute.getContents();
				String strTCID = cellTCID.getContents();
				JSONObject joTestSuite = new JSONObject();
				joTestSuite.put("execute", strExecute);
				joTestSuite.put("testsuiteID", strTCID);
				jaTestSuites.put(joTestSuite);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return jaTestSuites;
	}

	/**********************************************************************************************************
	 * ' Function Name: getNumberOfColumn ' Purpose: This function will count
	 * and return the number of columns in specific sheet ' Inputs Parameters:
	 * String strFileName, String strSheetName strFileName: Excel file store the
	 * value we want to get strSheetname: Excel sheet ' Returns: intColumns -
	 * Return the number of column ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public int getNumberOfColumn(String strFileName, String strSheetName) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;
		int intColumns = 0;

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSheetName);
			// Get Number of Column
			intColumns = sheet.getColumns();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intColumns;
	}

	/**********************************************************************************************************
	 * ' Function Name: getNumberOfRow ' Purpose: This function will count and
	 * return the number of Row in specific sheet ' Inputs Parameters: String
	 * strFileName, String strSheetName strFileName: Excel file store the value
	 * we want to get strSheetname: Excel sheet ' Returns: intRows - Return the
	 * number of row ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public int getNumberOfRow(String strFileName, String strSheetName) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;
		int intRows = 0;

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSheetName);
			// Get Number of Row
			intRows = sheet.getRows();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intRows;
	}

	/**********************************************************************************************************
	 * ' Function Name: getPositionOfRow ' Purpose: This function will get and
	 * return position of row with specific data. ' Inputs Parameters: String
	 * strFileName, String strSheetName, String strValue strFileName: Excel file
	 * store the value we want to get strSheetname: Excel sheet strValue: Value
	 * want to find out the row. ' Returns: intPositionOfRow - Return the
	 * position of row ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public int getPositionOfRow(String strFileName, String strSheetName,
			String strValue) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;
		int intRows = 0;
		int intColumns = 0;
		String strActualValue = "";
		int intPositionOfRow = 99999;

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSheetName);
			// Get Number of Row and Column
			intRows = sheet.getRows();
			intColumns = sheet.getColumns();

			for (int i = 0; i < intColumns; i++) {
				for (int j = 0; j < intRows; j++) {
					Cell cell = sheet.getCell(i, j);
					strActualValue = cell.getContents().trim();
					if (strActualValue.contentEquals(strValue)) {
						intPositionOfRow = j;
						break;
					}
				}
				if (intPositionOfRow != 99999) {
					break;
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intPositionOfRow;
	}

	/**********************************************************************************************************
	 * ' Function Name: getPositionOfColumn ' Purpose: This function will get
	 * and return position of column with specific data. ' Inputs Parameters:
	 * String strFileName, String strSheetName, String strValue strFileName:
	 * Excel file store the value we want to get strSheetname: Excel sheet
	 * strValue: Value want to find out the column ' Returns:
	 * intPositionOfColumn - Return the number of column ' Author : ' Creation
	 * Date: /
	 **********************************************************************************************************/
	public int getPositionOfColumn(String strFileName, String strSheetName,
			String strValue) {
		File fInputWorkbook = new File(strFileName);
		Workbook wb;
		int intRows = 0;
		int intColumns = 0;
		String strActualValue = "";
		int intPositionOfColumn = 99999;

		try {
			wb = Workbook.getWorkbook(fInputWorkbook);
			// Get the sheet
			Sheet sheet = wb.getSheet(strSheetName);
			// Get Number of Row and Column
			intRows = sheet.getRows();
			intColumns = sheet.getColumns();

			for (int i = 0; i < intRows; i++) {
				for (int j = 0; j < intColumns; j++) {
					Cell cell = sheet.getCell(j, i);
					strActualValue = cell.getContents().trim();
					if (strActualValue.contentEquals(strValue)) {
						intPositionOfColumn = j;
						break;
					}
				}
				if (intPositionOfColumn != 99999) {
					break;
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return intPositionOfColumn;
	}

}
