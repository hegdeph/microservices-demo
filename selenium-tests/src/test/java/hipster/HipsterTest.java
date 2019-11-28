package hipster;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import ru.stqa.selenium.factory.WebDriverPool;

public class HipsterTest {
	private WebDriver driver;
	private static DesiredCapabilities capabillities;
	private static Wait<WebDriver> wait;

	@Test
	public void f() {
		driver.get("http://"+System.getProperty("TARGET_URL"));
		String title = driver.getTitle();
		System.out.println(title);
		List<WebElement> anchors = driver.findElements(By.tagName("a"));
		Iterator<WebElement> i = anchors.iterator();

		while (i.hasNext()) {
			WebElement anchor = i.next();
			System.out.println(anchor.getAttribute("href"));
			if (anchor.getAttribute("href").contains("66VCHSJNUP")) {

				anchor.click();

				break;
			}
		}

		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				List<WebElement> headers = d.findElements(By.tagName("h2"));
				Iterator<WebElement> i = headers.iterator();
				while (i.hasNext()) {
					WebElement anchor = i.next();
					System.out.println(anchor.getText());

					if (anchor.getText().contains("Vintage"))
						return true;

				}
				return false;
			}
		});

		Select qty = new Select(driver.findElement(By.name("quantity")));
		qty.selectByVisibleText("5");

		driver.findElement(By.xpath("/html/body/main/div/div/div[1]/div[2]/form/div/button")).click();

		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d1) {
				List<WebElement> headers = d1.findElements(By.tagName("h3"));
				Iterator<WebElement> i = headers.iterator();
				System.out.println(headers.size());
				while (i.hasNext()) {
					WebElement anchor = i.next();
					System.out.println(anchor.getText());

					if (anchor.getText().contains("Shopping"))
						return true;

				}
				return false;
			}
		});

		WebElement elem = driver.findElement(By.xpath("/html/body/main/div/div/div[3]/div/strong"));
		System.out.println(elem.getText());
		Assert.assertTrue(elem.getText().equals("USD 86.44"));

	}

	@BeforeTest
	public void beforeTest() throws MalformedURLException {

		capabillities = DesiredCapabilities.chrome();

		/** URL is the selenium hub URL here **/
		try{
		//driver = new RemoteWebDriver(new URL("http://34.87.56.178:4444/wd/hub"), capabillities);
		driver = WebDriverPool.DEFAULT.getDriver(new URL("http://"+System.getProperty("SELENIUM_GRID_URL") +":4444/wd/hub"), capabillities);
	
		//capabillities.setBrowserName("firefox");
		wait = new WebDriverWait(driver, 12000);

		}

		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
	}

}

