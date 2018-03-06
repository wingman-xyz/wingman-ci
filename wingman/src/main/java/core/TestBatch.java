package core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.apache.log4j.Logger;

public class TestBatch extends Common {
	//private WebDriver driver;
	private AppiumDriver<WebElement>driver;
	// Ref parameter will be load from ref.properties file
	private String strDataFile = "";
	private String strAppDir = "";
	private String appName = "";
	private static Properties prop = new Properties();
	
	// parameter will be loaded from GENERAL sheet excel DATA file
	private String RUN_AUTOMATION_ON_REAL_DEVICE;
	private String DEVICE_NAME = "";
	private String UDID = "";
	private String SIMULATOR_NAME = "";
	private String VERSION = "";
	private String APP_BUNDLEID = "";
	private String APPIUM_SERVER_ADDRESS = ""; // this paramter will be loaded from System.getProperty if envSystem = true. If not, will loaded from excel file
	private String PLATFORM = "";
	
	//Init the parameter for capabilties testing
	private DesiredCapabilities capabilities = new DesiredCapabilities();
	
	
	// Init the parameter for write log
	private static Logger Log = Logger.getLogger(TestBatch.class.getName());

	protected ReadExcel readExcel = new ReadExcel();
	protected WriteExcel writeExcel = new WriteExcel();

	/*
	 * [{ "uc_name": "<string>", "number_test": <integer>, "pass": "<integer>",
	 * "fail": "<integer>" }, ...., ]
	 */
	protected JSONArray jaResult = new JSONArray();

	static HTMLResGenerator HTMLResGenerator = new HTMLResGenerator();
	// static Login login = new Login();
	static Controller controller = new Controller();
	static Keyword keyword = new Keyword();

	/**********************************************************************************************************
	 * ' Function Name: setUp ' Purpose: This function will initial some basic
	 * informations for test ' Inputs Parameters: ' Returns: ' Author : '
	 * Creation Date: /
	 **********************************************************************************************************/
	@Before
	public void setUp() throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.info("______________________________________________________________");
		Log.info("Initializing Selenium...");

		System.out.println("================================Setup the Test================================");
		loadRefParameter();
		initialDataFile();
		
		boolean envSystem = Boolean.valueOf(System.getProperty("envSystem"));
		
		if (envSystem) {
			capabilities.setCapability("sessionName", System.getProperty("envSessionName"));
			capabilities.setCapability("sessionDescription", System.getProperty("envSessionDescription")); 
			capabilities.setCapability("captureScreenshots", Boolean.valueOf(System.getProperty("envScreenShots")));
			capabilities.setCapability("deviceOrientation", System.getProperty("deviceOrientation"));  
			capabilities.setCapability("deviceGroup", System.getProperty("deviceGroup")); 
			capabilities.setCapability("deviceName", System.getProperty("deviceName"));
			capabilities.setCapability("platformVersion", System.getProperty("platformVersion"));
			capabilities.setCapability("platformName", System.getProperty("platformName"));
			capabilities.setCapability("app", System.getProperty("testingApp"));
			APPIUM_SERVER_ADDRESS = System.getProperty("kobitonServerUrl");
		} else {
			getGeneralInfo();
			capabilities.setCapability("platformName", PLATFORM);
			if (RUN_AUTOMATION_ON_REAL_DEVICE.equalsIgnoreCase("true")) {
				capabilities.setCapability("deviceName", DEVICE_NAME);
				capabilities.setCapability("udid", UDID);
				capabilities.setCapability("bundleId", APP_BUNDLEID);

			} else {
				capabilities.setCapability("deviceName", SIMULATOR_NAME);
				capabilities.setCapability("platformVersion", VERSION);
				capabilities.setCapability("app", strAppDir + appName);
				capabilities.setCapability("platformName", PLATFORM);
			}
			capabilities.setCapability("waitForAppScript", "true");
			
		}
		launchApp();
		HTMLResGenerator.initHTMLReport();
		Log.info("Selenium instance started");
	}

	/**********************************************************************************************************
	 * ' Function Name: tearDown ' Purpose: This function will close down the
	 * test ' Inputs Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	@After
	public void tearDown() {
		try {
			// Write log for stopping selenium
			Log.info("Stopping Selenium...");
			Log.info("______________________________________________________________");

			HTMLResGenerator.insertEndTime();

			System.out.println("tearDown");
			driver.quit();
			writeExcel.deleteBackupFile();
			
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
		}
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
			intTimeOut = Integer.parseInt(prop.getProperty("intTimeout"));
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: initialDataFile ' Purpose: This function will copy and
	 * backup testcasefile and datafile before the test run ' Inputs Parameters:
	 * ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void initialDataFile() throws Exception {
		prop.load(new FileInputStream("ref.properties"));
		String strTestCasesFileName = prop.getProperty("strTestCasesFile");
		String strDataFileName = prop.getProperty("strDataFile");
		writeExcel.setOutputFile(strTestCasesFileName, strDataFileName);
		writeExcel.copyFile();
		// writeExcel.backupDataFile();
		writeExcel.backupResultFile();
	}

	/**********************************************************************************************************
	 * ' Function Name: getGeneralInfo ' Purpose: This function will load some
	 * general parameter before the test run from Excel file ' Inputs
	 * Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void getGeneralInfo() throws Exception {
		try {
			RUN_AUTOMATION_ON_REAL_DEVICE = readExcel.getValue(strDataFile, "General", 0, 1);
			DEVICE_NAME = readExcel.getValue(strDataFile, "General", 1, 1);
			UDID = readExcel.getValue(strDataFile, "General", 2, 1);
			SIMULATOR_NAME = readExcel.getValue(strDataFile, "General", 3, 1);
			VERSION = readExcel.getValue(strDataFile, "General", 4, 1);
			APP_BUNDLEID = readExcel.getValue(strDataFile, "General", 5, 1);
			APPIUM_SERVER_ADDRESS = readExcel.getValue(strDataFile, "General", 6, 1);
			strAppDir = readExcel.getValue(strDataFile, "General", 7, 1);
			appName = readExcel.getValue(strDataFile, "General", 8, 1);
			PLATFORM = readExcel.getValue(strDataFile, "General", 9, 1);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: launchApp ' Purpose: This function will launch to the
	 * app ' Inputs Parameters: ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public void launchApp() {
		try {
			if(PLATFORM.equalsIgnoreCase("ios") || System.getProperty("platformName").trim().equalsIgnoreCase("ios")){
				driver = new IOSDriver<WebElement>(new URL(APPIUM_SERVER_ADDRESS), capabilities);
			} else {
				driver = new AndroidDriver<WebElement>(new URL(APPIUM_SERVER_ADDRESS), capabilities);
			}
			Thread.sleep(3000);
			Log.info("Launch to::: " + APPIUM_SERVER_ADDRESS);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}

	/**********************************************************************************************************
	 * ' Function Name: runTestBatch ' Purpose: This function will call to
	 * controler class for get the test suite and run it ' Inputs Parameters: '
	 * Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	@Test
	public void runTestBatch() throws Exception {
		controller.runTestSuite(driver);
	}

}
