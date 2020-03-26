package testcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.LogStatus;

//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.MediaEntityBuilder;
//import com.aventstack.extentreports.Status;
//import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import basetestcases.BaseTest;

public class TestNG_Demo extends BaseTest {


	@Test(priority=1)
	public void Test01_launchPage() throws IOException 
	{

		test = rep.startTest("Test01_launchPage");

		test.log(LogStatus.INFO, "Starting the test - Test01_launchPage");

		openBrowser("browsername");

		test.log(LogStatus.INFO, "Opened the browser");
		navigate("appurl");
		test.log(LogStatus.INFO, "Navigated to the URL: "+ prop.getProperty("appurl"));

		String actualTitle = driver.getTitle();
		System.out.println(actualTitle);

		String expectedTitle = prop.getProperty("homepagetitle");
		Assert.assertEquals(expectedTitle, actualTitle);


		reportPass("Test01_launchPage is Passed");
	}

	@Test(priority=2, dependsOnMethods= {"Test01_launchPage"})
	public void Test02_ApplyFilterFromYear() throws IOException
	{

		test = rep.startTest("Test02_ApplyFilterFromYear");
		
		test.log(LogStatus.INFO, "Starting the test Test02_ApplyFilterFromYear");

		click("regdatefrom_xpath");
		
		test.log(LogStatus.INFO, "Registration date from is clicked");

		selectValueFromDropdown("selectyearrange_xpath", "year");

		reportPass("Test02_ApplyFilterFromYear is Passed");
	}

	@Test(priority=3, dependsOnMethods= {"Test01_launchPage", "Test02_ApplyFilterFromYear"})
	public void Test03_VerifyAppliedFilter() throws IOException {

		test = rep.startTest("Test03_VerifyAppliedFilter");
		test.log(LogStatus.INFO, "Starting the test Test03_VerifyAppliedFilter");

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(prop.getProperty("activefilter_css"))));
		
		String url = driver.getCurrentUrl();
		if(url.equals(prop.getProperty("filteredurl")))
		{
			reportPass("Filter applied correctly");
		}else {
			reportFailure("Filtere is not applied");
		}

		reportPass("Test03_VerifyAppliedFilter is Passed");

	}

	@Test(priority=4, dependsOnMethods= {"Test01_launchPage", "Test02_ApplyFilterFromYear"})
	public void Test04_PriceDescendingOrder() throws IOException {

		test = rep.startTest("Test04_PriceDescendingOrder");
		test.log(LogStatus.INFO, "Starting the test Test04_PriceDescendingOrder");

		selectValueFromDropdown("sortprice_xpath", "sortby");

		String url = driver.getCurrentUrl();
		if(url.equals(prop.getProperty("filterpricedescendingurl")))
		{
			reportPass("Car Price sorted by descending order");
		}else {
			reportFailure("Car Price not sorted by descending order");
		}

		reportPass("Test04_PriceDescendingOrder is Passed");

	}

	@Test(priority=5, dependsOnMethods= {"Test01_launchPage", "Test02_ApplyFilterFromYear", "Test04_PriceDescendingOrder"})
	public void Test05_VerifyCarFilteredByFirstRegistration() throws InterruptedException, IOException
	{

		Thread.sleep(5000);
		test = rep.startTest("Test05_VerifyCarFilteredByFirstRegistration");
		test.log(LogStatus.INFO, "Starting the test - Test05_VerifyCarFilteredByFirstRegistration");

		List<WebElement> links = driver.findElements(By.xpath(prop.getProperty("registrationdate_xpath")));
		System.out.println("The total number of cars are filtered: "+links.size());
		int count =0;
		for(int i=0;i<links.size();i++) 
		{

			String val1[] = links.get(i).getText().toString().replace("•", "").trim().split("/");

			int i1=Integer.parseInt(val1[1]);

			if(i1 >= 2015) {
				count = count+1;
			}else {
				reportFailure("cars are not filtered by first registration 2015");
			}

		}

		test.log(LogStatus.INFO, "The number of cars are filtered by registration greater and equal to 2015: "+count);

		reportPass("Test05_VerifyCarFilteredByFirstRegistration is Passed");

	}

	@Test(priority=6, dependsOnMethods= {"Test01_launchPage", "Test02_ApplyFilterFromYear", "Test04_PriceDescendingOrder", "Test05_VerifyCarFilteredByFirstRegistration"})
	public void Test06_VerifyCarSortedByPriceDescending() throws IOException
	{

		test = rep.startTest("Test06_VerifyCarSortedByPriceDescending");
		test.log(LogStatus.INFO, "Starting the test - Test06_VerifyCarSortedByPriceDescending");

		List<WebElement> price = driver.findElements(By.cssSelector(prop.getProperty("price_css")));

		ArrayList<Float> priceList = new ArrayList<Float>();
		for (int i = 0; i<price.size(); i=i+1) {
			String val[]  = price.get(i).getText().toString().split(" ");
			priceList.add(Float.parseFloat(val[0])); 
		}  

		if(descendingCheck(priceList))
		{ 
			reportPass("Price displayed in descending order");

		}else
		{
			reportFailure("Price not displayed in descending order");
		}

		reportPass("Test06_VerifyCarSortedByPriceDescending is Passed");

	}
	
	@AfterMethod
	public void quit()
	{
		rep.endTest(test);
		rep.flush();
	}

}
