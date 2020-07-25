package salesforcePackage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SFScenario1 
{
	@Test
	public void myTest() throws InterruptedException
	{
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		
		
		//5. If you get browser notifications, accept it.
		options.addArguments("--disable-notifications");
		//options.setExperimentalOption("debuggerAddress", "localhost:52539");
	
	
		ChromeDriver driver = new ChromeDriver(options);
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		Capabilities capabilities = driver.getCapabilities();
		for(String eachCap : capabilities.getCapabilityNames())
		{
			if(capabilities.getCapability(eachCap).toString().contains("debuggerAddress"))
				System.out.println(capabilities.getCapability(eachCap));
		}
		
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		//1. Load https://login.salesforce.com/
		driver.get("https://login.salesforce.com/");
		
		//2. Enter username as hari.radhakrishnan@testleaf.com
		driver.findElementById("username").sendKeys("hari.radhakrishnan@testleaf.com");
		
		//3. Enter password as India@123
		driver.findElementById("password").sendKeys("India@123");
		
		//4. Click Login
		driver.findElementById("Login").click();
		
		//6. Mouse on the image icon and confirm View profile text appears
		WebElement profileImage = driver.findElementByXPath("//button/div/span[1]/div/span");
		Actions builder = new Actions(driver);
		builder.moveToElement(profileImage).build().perform();
		WebElement profileToolTip = driver.findElementByXPath("//button/div/span[2]");
		String toolTipSearch = profileToolTip.getAttribute("textContent");
		System.out.println("---->"+toolTipSearch);
		Assert.assertEquals(toolTipSearch,"View profile");
		
		//7. Click on plus icon (+) and Click New Lead
		WebElement plusIcon = driver.findElementByXPath("(//ul[@class='slds-global-actions']/li)[4]");
		Thread.sleep(3000);
		plusIcon.click();
		
		WebElement newLeadOption = driver.findElementByXPath("//span[text()='New Lead']");
		newLeadOption.click();
		
		//8. Select appropriate Salutation and enter all the mandatory fields and click Save [Note: Use your unique last name so that your data is unique]
		WebElement salutation = driver.findElementByLinkText("--None--");
		salutation.click();
		WebElement mrOption = driver.findElementByXPath("//a[@title='Mr.']");
		mrOption.click();
		Random rand = new Random();  
        int randomNumber = rand.nextInt(100000);
        String lastName = "GTJ"+randomNumber; 
		driver.findElementByXPath("//label/span[text()='Last Name']/parent::label/following-sibling::input").sendKeys(lastName);
		driver.findElementByXPath("//label/span[text()='Company']/parent::label/following-sibling::input").sendKeys("GTJ Company");
		driver.findElementByXPath("(//span[text()='Save'])[2]").click();
		
		//9. Verify the message displayed that "Lead <name> was created"
		boolean successMessage = driver.findElementByXPath("//div[@title='"+lastName+"']").isDisplayed();
		Assert.assertEquals(successMessage,true);
		
		//10. Click on the App launcher menu and click on View All
		WebElement appLauncher =driver.findElementByXPath("//button[@class='bare slds-icon-waffle_container slds-context-bar__button slds-button uiButton forceHeaderButton salesforceIdentityAppLauncherHeader']");
		appLauncher.click();
		driver.findElementByXPath("//button[text()='View All']").click();
		
		//11. Click on Sales Link
		driver.findElementByXPath("//p[text()='Sales']").click();
		
		//12. Verify Sales tab is displayed and get the open USD value (just the number alone)
		String tabName = driver.findElementByXPath("//span[@class='appName slds-context-bar__label-action slds-context-bar__app-name']/span").getText();
		String openUSDValue = driver.findElementByXPath("(//span[@class='metricAmount uiOutputText'])[2]").getText();
		
		Assert.assertEquals(tabName,"Sales");
		System.out.println("The Open USD Value is: "+openUSDValue);
		
		//13. Clicks on the leads Tab
		WebElement leadsTab = driver.findElementByXPath("//a[@title='Leads']/span");	
		executor.executeScript("arguments[0].click();", leadsTab);
		
		//14. Sort by Name column and confirm the displayed names are sorted correctly
		driver.findElementByXPath("//th[@title='Name']/div").click();

		//15. Type and Enter your last name in Search box
		driver.findElementByXPath("//input[@name='Lead-search-input']").sendKeys(lastName);
		Thread.sleep(5000);
		driver.findElementByXPath("//span[@class='appName slds-context-bar__label-action slds-context-bar__app-name']/span").click();
		Thread.sleep(5000);
		
		//16. Wait for the loading bar to disappear
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='slds-spinner_container slds-grid']")));
		
		//17. Change the Lead Status as "Working - Contacted" and Click on Save button
		WebElement leadStatus = driver.findElementByXPath("(//span[text()='Open - Not Contacted'])[1]");
		builder.moveToElement(leadStatus).build().perform();
		driver.findElementByXPath("(//button[@class='slds-button trigger slds-button_icon-border'])[6]").click();
		driver.findElementByLinkText("Open - Not Contacted").click();
		driver.findElementByXPath("//a[@title='Working - Contacted']").click();
		driver.findElementByXPath("//span[text()='Save']").click();
		
		//18. Open a new tab programmatically
		((JavascriptExecutor)driver).executeScript("window.open()");
		
		//19. Get the Lead navigation URL and load in the new Tab
		String link = driver.findElementByLinkText(lastName).getAttribute("href");
		System.out.println(link);
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1));
		driver.get(link);
		 
		//20. Verify title of new Tab displays Last Name of your Lead and Click Create New Task
		Thread.sleep(5000);
		 Assert.assertEquals(driver.getTitle(),lastName+" | Salesforce");			
		 WebElement addButton = driver.findElementByXPath("//span[text()='Add']");
		 executor.executeScript("arguments[0].click();", addButton);
		 
		 //21. Select Subject as "Send Quote"
		 driver.findElementByXPath("(//label[text()='Subject']/following::input)[1]").sendKeys("Send Quote");
		 
		 //22. Select Due Date as Current Day+2 [Calculated Value and not hardcoded]
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");  
		 LocalDateTime now = LocalDateTime.now().plusDays(2); 
		 driver.findElementByXPath("(//span[text()='Due Date']/following::input)[1]").sendKeys(dtf.format(now));
		 
		 //23. Click Save
		 WebElement saveButton = driver.findElementByXPath("//span[text()='Save']");
		 executor.executeScript("arguments[0].click();", saveButton);
		 
		 //24. Click on the New Task and Click Mark Complete
		 WebElement tasksTab = driver.findElementByXPath("//a[@title='Tasks']/span");	
		 executor.executeScript("arguments[0].click();", tasksTab);
		 driver.findElementByXPath("//span[text()='Mark Complete']/parent::button").click();
		 
		 //25. Confirm the task is completed and displayed in blue color.
		 String CompletedDisplayed = driver.findElementByXPath("//span[text()='Completed']").getAttribute("class");
		 String blueColor = driver.findElementByXPath("//span[text()='Completed']").getCssValue("color");
		 Assert.assertEquals(CompletedDisplayed,"slds-text-selected");
		 Assert.assertEquals(blueColor,"rgba(0, 68, 135, 1)");	
		 
		 driver.quit();
		 
	}
}
