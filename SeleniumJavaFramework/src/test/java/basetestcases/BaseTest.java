package basetestcases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.apache.commons.io.FileUtils;

//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.ExtentTest;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Utility.ExtentManager;

public class BaseTest {
	
	public WebDriver driver;
	String projectPath = System.getProperty("user.dir");
	public Properties prop;
	
	public ExtentReports rep = ExtentManager.getInstance();
	
	public ExtentTest test;

	public void openBrowser(String browser) {
		
		if (prop==null) {
			prop = new Properties();
			try {
				FileInputStream fs = new FileInputStream(projectPath+"//src//test/resources//projectconfig.properties");
				prop.load(fs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println(prop.getProperty("appurl"));
		
		
		if(prop.getProperty(browser).equals("Mozilla")) {
			System.setProperty("webdriver.gecko.driver", projectPath+"\\drivers\\geckoDriver\\geckodriver.exe");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "null");
			driver = new FirefoxDriver();
		}else if(prop.getProperty(browser).equals("Chrome")) {
			System.setProperty("webdriver.chrome.driver", projectPath+"\\drivers\\chromedriver\\chromedriver.exe");
			System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "null");
			driver = new ChromeDriver();
		}else if(prop.getProperty(browser).equals("IE")) {
			System.setProperty("webdriver.ie.driver", projectPath+"\\drivers\\iedriver\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();

		}else if(prop.getProperty(browser).equals("Edge")) {
			driver = new EdgeDriver();
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		
	}
	
	
	public void navigate(String urlKey) {
		driver.get(prop.getProperty(urlKey));
	}
	
	public void click(String locatorKey) {
		getElement(locatorKey).click();
		//driver.findElement(By.xpath(prop.getProperty(xpathElementKey))).click();
	}
	
	//finding element and returning it
	public WebElement getElement(String locatorKey) 
	{
		WebElement e = null;
		try {
			if(locatorKey.endsWith("_id"))
				e= driver.findElement(By.id(prop.getProperty(locatorKey)));
			else if(locatorKey.endsWith("_xpath"))
				e= driver.findElement(By.xpath(prop.getProperty(locatorKey)));
			else if(locatorKey.endsWith("_name"))
				e= driver.findElement(By.name(prop.getProperty(locatorKey)));
			else {
				reportFailure("Locator not correct  - " +locatorKey);
				AssertJUnit.fail("Locator not correct  - " +locatorKey);
			}


		}
		catch(Exception ex) {
			reportFailure(ex.getMessage());
			ex.printStackTrace();
			AssertJUnit.fail("Failed Test"+ex.getMessage());
		}
		return e;
	}
	
	public void selectValueFromDropdown(String locatorKey, String valuekey) {
		Select dropdown = new Select(getElement(locatorKey));  
		dropdown.selectByVisibleText(prop.getProperty(valuekey));
		WebElement selectedValue=dropdown.getFirstSelectedOption();

		String actualValue=selectedValue.getText();
		String expectedValue = prop.getProperty(valuekey);
		Assert.assertEquals(expectedValue, actualValue);
		if(expectedValue.equals(actualValue)) {
			reportPass(prop.getProperty(valuekey)+ " is selected in dropdwon");
		}else
		{
			reportFailure(prop.getProperty(valuekey)+ " is not selected in dropdwon");
		}
	}
	
	public Boolean descendingCheck(ArrayList<Float> data){         
		for (int i = 0; i < data.size()-1; i++) {
			if (data.get(i) > data.get(i+1)) {
				return true;
			}       
		}
		return false;
	}

//********************************Reportig**********************************
	public void reportPass(String msg){
		test.log(LogStatus.PASS, msg);
	}
	
	public void reportFailure(String msg){
		test.log(LogStatus.FAIL, msg);
		takeScreenshot();
		Assert.fail(msg);
	}
	
	public void takeScreenshot() {
		// TODO Auto-generated method stub
		
		// fileName of the screenshot
				Date d=new Date();
				String screenshotFile=d.toString().replace(":", "_").replace(" ", "_")+".png";
				
				// store screenshot in that file
				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				try {
					FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//put screenshot file in reports
				test.log(LogStatus.INFO,"Screenshot-> "+ test.addScreenCapture(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
				
		
	}
}
