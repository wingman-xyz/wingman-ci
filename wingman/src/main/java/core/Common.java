package core;

import io.appium.java_client.AppiumDriver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class Common {

	// Ref parameter will be load from ref.properties file
	public int intTimeOut = 0;
	public String strTestCasesFile = "";
	public String strDataFile = "";
	public String strXmlPath = "";

	// Init the parameter for write log
	private static Logger Log = Logger.getLogger(TestBatch.class.getName());

	public Common() {
		loadRefParameter();
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
	 * ' Function Name: findElement ' Purpose: This function find and return the
	 * element we want to get ' Inputs Parameters: WebDriver webDriver, By by,
	 * int intTimeout webDriver: WebDriver to run the test by:
	 * By.xpath(oObjectXpath) intTimeout: Time set for time out when find an
	 * object. ' Returns: ' Author : ' Creation Date: /
	 **********************************************************************************************************/
	public WebElement findElement(AppiumDriver<WebElement> webDriver, By by, int iTimeOut) {
		int iSleepTime = 5000;
		for (int i = 0; i < iTimeOut; i += iSleepTime) {
			List<WebElement> oWebElements = webDriver.findElements(by);
			if (oWebElements.size() > 0) {
				return oWebElements.get(0);
			} else {
				try {
					Thread.sleep(iSleepTime);
					System.out.println(String.format(
							"Waited for %d milliseconds.[%s]", i + iSleepTime,
							by));
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		// Can't find 'by' element. Therefore throw an exception.
		String sException = String.format(
				"Can't find %s after %d milliseconds.", by, iTimeOut);
		throw new RuntimeException(sException);
	}
	
	public String updateXapth(String strXpath, String attValue) {
		String strUpdatedXath = null;
		try {
			Pattern ptn = Pattern.compile("\'\\d\'");
	        Matcher mtch = ptn.matcher(strXpath);
	        strUpdatedXath = mtch.replaceAll("\'" + attValue + "\'");
	        
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return strUpdatedXath;
	}	
	
	public String getPasswordFromRestAPI (String strLink) {
		String strPassword = null;
		try {
			String strResponse = getResponseFromRestAPI (strLink);
			JSONObject json = convertJsonStringJavaObject (strResponse);
			strPassword = json.getJSONObject("body").getString("code").toString().trim();
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return strPassword;
	}
	
	public String getResponseFromRestAPI (String strRequest) {
		String line = null;
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(strRequest);
			HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            line = rd.readLine();
            
		} catch (ClientProtocolException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		} catch (UnsupportedOperationException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		} catch (IOException ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return line;
	}
	
	public JSONObject convertJsonStringJavaObject (String strJson) {
		JSONObject json = null;
		try {
			json = new JSONObject(strJson);
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
		return json;
	}
	
	public void inputMultiNumbers (AppiumDriver<WebElement> webDriver, String oObjectXpath, String strData) {
		try {
			for (int i = 0; i < strData.length(); i++) {
				oObjectXpath = updateXapth(oObjectXpath, Character.toString(strData.charAt(i)));
				WebElement oNumber = findElement(webDriver, By.xpath(oObjectXpath),intTimeOut);			
				oNumber.click();
				if(i == strData.length()-1) {
					Thread.sleep(3000);
				}
			}
		} catch (Exception ex) {
			System.err.println("Got an exception! ");
			ex.printStackTrace();
			Log.info(ex.getMessage().toString());
		}
	}
	
	public boolean isElementExisted(AppiumDriver<WebElement> webDriver, String strLocator){
		boolean isVisible = true;
		try{
			webDriver.findElement(By.xpath(strLocator));
		}catch(NoSuchElementException Ex){
			isVisible = false;
		}
		return isVisible;
	}
}
