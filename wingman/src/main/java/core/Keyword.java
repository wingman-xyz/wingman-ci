package core;

import io.appium.java_client.AppiumDriver;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Keyword extends Common {
	// Variables for get data from ref.properties
	private String strDataFileName = "";
	private String strTestCasesFileName = "";

	// Global variables of class
	private int intTimeOut = 0;



	private static Logger Log = Logger.getLogger(TestBatch.class.getName());
	protected ReadExcel readExcel = new ReadExcel();
	protected WriteExcel writeExcel = new WriteExcel();
	
	public Keyword() {

		try {
			loadRefParameter();
			writeExcel.setOutputFile(strTestCasesFileName, strDataFileName);

		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}
	

	/**********************************************************************************************************
	 * ' Function Name: input ' Purpose: This function will input data to
	 * textbox object ' Inputs Parameters: (WebDriver webDriver, String
	 * oObjectXpath, String strObjectName, String strData) webDriver: WebDriver
	 * to run the test oObjectXpath: The xpath of specific object strObjectName:
	 * Name of object in testcase file strData: Data to fill in Object '
	 * Returns: true or false true if PASS false if FAIL ' Author : ' Creation
	 * Date: /
	 **********************************************************************************************************/
	boolean input(AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName, String strData) {
		String strStepDetails;
		boolean bResult = false;
		try {
		
			WebElement oInput = findElement(webDriver, By.xpath(oObjectXpath),intTimeOut);			
			oInput.clear();
			oInput.sendKeys(strData);
			strStepDetails = "Enter value to " + strObjectName + ": " + strData;
			System.out.println(strStepDetails);
			
			Log.info(strStepDetails);
			bResult = true;
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}

	/**********************************************************************************************************
	 * ' Function Name: loadRefParameter ' Purpose: This function will load some
	 * ref parameter before the test run ' Inputs Parameters: ' Returns: '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	private void loadRefParameter() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("ref.properties"));
			intTimeOut = Integer.parseInt(prop.getProperty("intTimeout"));
			strTestCasesFileName = prop.getProperty("strTestCasesFile");
			strDataFileName = prop.getProperty("strDataFile");
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: tap ' Purpose: This function will tap on a
	 * Link/Button ' Inputs Parameters: (WebDriver webDriver, String
	 * oObjectXpath, String strObjectName) webDriver: WebDriver to run the test
	 * oObjectXpath: The xpath of specific object strObjectName: Name of object
	 * in testcase file ' Returns: true or false true if PASS false if FAIL '
	 * Author : ' Creation Date: /
	 **********************************************************************************************************/
	boolean tap(AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			WebElement oElement= findElement(webDriver, By.xpath(oObjectXpath), intTimeOut);
			// Click on Object
			oElement.click();
			strStepDetails = "Click on " + strObjectName;
			System.out.println(strStepDetails);
			Log.info(strStepDetails);
			Thread.sleep(2000);
			bResult = true;
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	/**********************************************************************************************************
	 * ' Function Name: verifyValue ' Purpose: This function will verify the
	 * actual result of value get from a textbox with expected value ' Inputs
	 * Parameters: (WebDriver webDriver, String oObjectXpath, String String
	 * strData) webDriver: WebDriver to run the test oObjectXpath: The xpath of
	 * specific object strData: Data to fill in Object ' Returns: true or false
	 * true if the actual result equal with expected result false if the actual
	 * result is not equal with expected result ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	boolean verifyValue(AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName, String strData) {
		String strStepDetails;
		boolean bResult = false;
		try {
			WebElement oElement = findElement(webDriver, By.xpath(oObjectXpath),intTimeOut);
			String strActualResult = oElement.getAttribute("value").trim();
			if (strActualResult.equalsIgnoreCase(strData.trim())) {
				bResult = true;

				strStepDetails = "Verify Text of " + strObjectName
						+ ": PASS, Expected result: "
						+ strData.trim().toUpperCase() + "\nActual result: "
						+ strActualResult.trim().toUpperCase();
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Verify Text of " + strObjectName 
						+ ": FAIL, Expected result: "
						+ strData.trim().toUpperCase() + "\nActual result: "
						+ strActualResult.trim().toUpperCase();
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}

		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}

	/**********************************************************************************************************
	 * ' Function Name: tapOnPhoneNumber ' Purpose: This function will input data to
	 * textbox object ' Inputs Parameters: (WebDriver webDriver, String
	 * oObjectXpath, String strObjectName, String strData) webDriver: WebDriver
	 * to run the test oObjectXpath: The xpath of specific object strObjectName:
	 * Name of object in testcase file strData: Data to fill in Object '
	 * Returns: true or false true if PASS false if FAIL ' Author : ' Creation
	 * Date: /
	 **********************************************************************************************************/
	boolean tapOnPhoneNumber(AppiumDriver<WebElement> webDriver, String oObjectXpath, String strData) {
		String strStepDetails;
		boolean bResult = false;
		try {
			inputMultiNumbers(webDriver, oObjectXpath, strData);
			strStepDetails = "Phone number: " + strData + " has been entered";
			System.out.println(strStepDetails);
			Log.info(strStepDetails);
			bResult = true;

		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean inputPassword (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strData) {
		String strStepDetails;
		boolean bResult = false;
		try {
			String strPassword = getPasswordFromRestAPI(strData);
			inputMultiNumbers(webDriver, oObjectXpath, strPassword);
			strStepDetails = "Password number: " + strPassword + " has been entered";
			System.out.println(strStepDetails);
			Log.info(strStepDetails);
			bResult = true;
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}

	boolean verifyElememtIsExisted (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			bResult = isElementExisted(webDriver, oObjectXpath);
			if(bResult) {
				strStepDetails = "Elment: " + strObjectName + " is existing";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Elment: " + strObjectName + " is NOT existing";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean verifyElememtIsDisplayedOnScreen (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			WebElement oElement= findElement(webDriver, By.xpath(oObjectXpath), intTimeOut);
			bResult = oElement.isDisplayed();
			if(bResult) {
				strStepDetails = "Elment: " + strObjectName + " is displayed";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Elment: " + strObjectName + " is NOT displayed";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean verifyElememtIsEnabledOnScreen (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			WebElement oElement= findElement(webDriver, By.xpath(oObjectXpath), intTimeOut);
			bResult = oElement.isEnabled();
			if(bResult) {
				strStepDetails = "Elment: " + strObjectName + " is enabled";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Elment: " + strObjectName + " is NOT enabled";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean allowPermissionPopUp(AppiumDriver<WebElement> webDriver) {
		String strStepDetails;
		boolean bResult = false;
		try {
			Alert popup = webDriver.switchTo().alert();
			String strText = popup.getText();
			popup.accept();
			strStepDetails = strText + ": ACCEPTED";
			System.out.println(strStepDetails);
			Log.info(strStepDetails);
			Thread.sleep(1000);
			bResult = true;
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean getResourcePage(AppiumDriver<WebElement> webDriver) {
		String strStepDetails;
		boolean bResult = true;
		try {
			strStepDetails = webDriver.getPageSource();
			System.out.println(strStepDetails);
			Thread.sleep(1000);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean verifyElememtIsNotExisted (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			boolean check = isElementExisted(webDriver, oObjectXpath);
			if(!check) {
				bResult = true;
				strStepDetails = "Elment: " + strObjectName + " is Not existing as expectation";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Elment: " + strObjectName + " is existing and is not same with expectation";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean verifyElememtIsNotDisplayedOnScreen (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			WebElement oElement= findElement(webDriver, By.xpath(oObjectXpath), intTimeOut);
			boolean check = oElement.isDisplayed();
			if(!check) {
				bResult = true;
				strStepDetails = "Elment: " + strObjectName + " is NOT displayed as expectation";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Elment: " + strObjectName + " is displayed and is not same with expectation";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
	boolean verifyElememtIsNotEnabledOnScreen (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strObjectName) {
		String strStepDetails;
		boolean bResult = false;
		try {
			WebElement oElement= findElement(webDriver, By.xpath(oObjectXpath), intTimeOut);
			boolean check = oElement.isEnabled();
			if(!check) {
				bResult = true;
				strStepDetails = "Elment: " + strObjectName + " is Not enabled as expectation";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			} else {
				strStepDetails = "Elment: " + strObjectName + " is enabled and is not same with expectation";
				System.out.println(strStepDetails);
				Log.info(strStepDetails);
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return bResult;
	}
	
}
