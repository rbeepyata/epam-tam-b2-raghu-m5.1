package com.ryanair.tests;

import java.time.LocalDate;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import com.google.common.base.Function;
import com.ryanair.automation.RyanairPage;

public class FlightTests {

	WebDriver driver;
	
	@BeforeTest
	public void beforeTest() {
		System.setProperty("webdriver.chrome.driver", "lib//driver//chromedriver.exe");
		driver = new ChromeDriver();
		driver.get("https://www.ryanair.com/gb/en/");
		driver.manage().window().maximize();
	}

	@Test
	public void testFindFlights() throws InterruptedException {
		//2.	Verify correct page is opened
		Assert.assertEquals(driver.getTitle(),
				"Official Ryanair website | Book direct for the lowest fares | Ryanair.com");

		
		WebElement fromElement = driver.findElement(RyanairPage.fromElement);
		fromElement.click();

		((JavascriptExecutor) driver).executeScript("window.scrollBy(0,250)");

		WebElement fromCountry = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver d) {
				return d.findElement(RyanairPage.belgiumLink);
			}
		});
		fromCountry.click();

		WebElement fromAirport = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver d) {
				return d.findElement(RyanairPage.brusselsCharleroiLink);
			}
		});
		fromAirport.click();
		//4.	Verify this airport appeared in “From” field.
		Assert.assertEquals(fromElement.getAttribute("value"), "Brussels Charleroi");

		WebElement toElement = driver.findElement(RyanairPage.toElement);

		WebElement toCountry = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver d) {
				return d.findElement(RyanairPage.germanyLink);
			}
		});
		//5.	For “To” field verify that “Germany” country is unavailable for selection
		Assert.assertTrue(toCountry.getAttribute("class").contains("unavailable"));

		((JavascriptExecutor) driver).executeScript("window.scrollBy(0,250)");
		toCountry = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver d) {
				return d.findElement(RyanairPage.unitedKindgomLink);
			}
		});
		toCountry.click();

		WebElement ToAirport = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver d) {
				return d.findElement(RyanairPage.manchesterLink);
			}
		});
		ToAirport.click();

		new WebDriverWait(driver, 30).until(ExpectedConditions.textToBePresentInElementValue(toElement, "Manchester"));
		//Verify this airport appeared in “To” field.
		Assert.assertEquals(toElement.getAttribute("value"), "Manchester");

		//•	Add a mechanism to choose random dates at p. 8 from available ones, so that each test new couple of dates Fly out -> Fly back were selected
		LocalDate startDate = LocalDate.of(java.time.LocalDateTime.now().getYear(),
				java.time.LocalDateTime.now().getMonthValue(), java.time.LocalDateTime.now().getDayOfMonth());
																												
		long start = startDate.toEpochDay();

		LocalDate endDate = LocalDate.of(java.time.LocalDateTime.now().getYear(),
				java.time.LocalDateTime.now().getMonthValue() + 7, java.time.LocalDateTime.now().getDayOfMonth());
																													
		long end = endDate.toEpochDay();

		long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
		long randomEpochDay2 = ThreadLocalRandom.current().longs(randomEpochDay, end).findAny().getAsLong();

		String StartDate = LocalDate.ofEpochDay(randomEpochDay).getDayOfMonth() + "";
		String ToDate = LocalDate.ofEpochDay(randomEpochDay2).getDayOfMonth() + "";
		String StartMonth = LocalDate.ofEpochDay(randomEpochDay).getMonth().name();
		String ToMonth = LocalDate.ofEpochDay(randomEpochDay2).getMonth().name();

		selectDateFromCalendar(StartDate, StartMonth);
		selectDateFromCalendar(ToDate, ToMonth);
		driver.findElement(RyanairPage.passenderDropdown).click();
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(WebDriverException.class);
		
		WebElement AddTeen = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(RyanairPage.teenPlusOption);
			}
		});
		AddTeen.click();
		
		String passenderTextContent = driver.findElement(RyanairPage.passenderTextContent).getText();
		//10.	Verify that “Passengers” field has value “2 Passengers”
		Assert.assertTrue(
				passenderTextContent.toLowerCase().contains("adult") &&
				passenderTextContent.toLowerCase().contains("1 others"));


		driver.findElement(RyanairPage.letsGoButton).click();
		
		
		boolean flightDisplayed = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return d.findElement(RyanairPage.flightsTableHeader).isDisplayed();
			}
		});
		//13.	Verify that search results page is opened.
		Assert.assertTrue(flightDisplayed);
		
		//•	On search results page, verify that selected dates equals to those that were selected on p. 8: 
		WebElement StartResultDateElement = (new WebDriverWait(driver, 30)).until(new ExpectedCondition<WebElement>() {
			public WebElement apply(WebDriver d) {
				return d.findElement(RyanairPage.toDateHighlighted);
			}
		});
		String StartResultDate = StartResultDateElement.getText();
		Assert.assertTrue(
				StartResultDate.toLowerCase().contains((StartDate + " " + StartMonth.substring(0, 3)).toLowerCase()));
		WebElement EndResultDateElement = (new WebDriverWait(driver, 30)).until(ExpectedConditions
				.presenceOfElementLocated(RyanairPage.returnDateHighlighted));
		String EndResultDate = EndResultDateElement.getText();
		Assert.assertTrue(EndResultDate.toLowerCase().contains((ToDate + " " + ToMonth.substring(0, 3)).toLowerCase()));

		//•	On search results page, verify that at least one flight is available for each destination. If so, print out to console info for each flight: flight number, departure time, arrival time, flight time
		printToFlightsDetails();

		printReturnFlightsDetails();
		
	}

	public void printToFlightsDetails() {
		List<WebElement> Flights = driver.findElements(By.xpath("(//flights-table)[1]//flights-table-header"));
		if (Flights.size() >= 1) {
			List<WebElement> StartTime = driver
					.findElements(By.xpath("(//flights-table)[1]//flights-table-header//div[@class='start-time']"));
			List<WebElement> EndTime = driver
					.findElements(By.xpath("(//flights-table)[1]//flights-table-header//div[@class='end-time']"));
			List<WebElement> FlightNumber = driver.findElements(By.xpath(
					"(//flights-table)[1]//flights-table-header//div[@class='meta-row flight-number-wrapper']//span[@class='flight-number']"));
			List<WebElement> FlightTime = driver
					.findElements(By.xpath("(//flights-table)[1]//flights-table-header//strong"));

			ListIterator<WebElement> itr1 = StartTime.listIterator();
			ListIterator<WebElement> itr2 = EndTime.listIterator();
			ListIterator<WebElement> itr3 = FlightNumber.listIterator();
			ListIterator<WebElement> itr4 = FlightTime.listIterator();

			System.out.println("\nTo Flight");
			while (itr1.hasNext()) {
				System.out.println("\nFlight Number:" + itr3.next().getText());
				System.out.print("\tStart Time:" + itr1.next().getText());
				System.out.print("\tEnd Time:" + itr2.next().getText());
				System.out.print("\tFlightTime:" + itr4.next().getText());

			}

		} else {
			System.out.println("No To Flights Available");
		}
	}

	public void printReturnFlightsDetails() {
		
		List<WebElement> ReturnFlights = driver.findElements(By.xpath("(//flights-table)[2]//flights-table-header"));
		if (ReturnFlights.size() >= 1) {
			List<WebElement> StartTime = driver
					.findElements(By.xpath("(//flights-table)[2]//flights-table-header//div[@class='start-time']"));
			List<WebElement> EndTime = driver
					.findElements(By.xpath("(//flights-table)[2]//flights-table-header//div[@class='end-time']"));
			List<WebElement> FlightNumber = driver.findElements(By.xpath(
					"(//flights-table)[2]//flights-table-header//div[@class='meta-row flight-number-wrapper']//span[@class='flight-number']"));
			List<WebElement> FlightTime = driver
					.findElements(By.xpath("(//flights-table)[2]//flights-table-header//strong"));

			ListIterator<WebElement> itr1 = StartTime.listIterator();
			ListIterator<WebElement> itr2 = EndTime.listIterator();
			ListIterator<WebElement> itr3 = FlightNumber.listIterator();
			ListIterator<WebElement> itr4 = FlightTime.listIterator();

			System.out.println("\nReturn Flight");
			while (itr1.hasNext()) {
				System.out.println("\nFlight Number:" + itr3.next().getText());
				System.out.print("\tStart Time:" + itr1.next().getText());
				System.out.print("\tEnd Time:" + itr2.next().getText());
				System.out.print("\tFlightTime:" + itr4.next().getText());

			}

		} else {
			System.out.println("No Return Flights Available");
		}
	}

	public WebDriver selectDateFromCalendar(String startDate, String startMonth) {

		int i = 1;

		while (i < 11) {
			WebElement Month = driver.findElement(By.xpath("(//h1[@class='month-name'])[" + i + "]"));
			if (Month.getText().toLowerCase().contains(startMonth.toLowerCase())) {
				WebElement Calendar = driver.findElement(By.xpath("(//core-datepicker//ul[@class='days'])[" + i + "]"));
				List<WebElement> Days = Calendar
						.findElements(By.xpath("(//core-datepicker//ul[@class='days'])[" + i + "]/li"));
				for (WebElement day : Days) {
					String str = day.getAttribute("data-id").substring(0, 2);
					if ((str.substring(0, 1).equals("0") ? str.substring(1) : str).equals(startDate)) {
						day.click();
						return driver;
					}
				}
			} else {
				driver.findElement(By.xpath("//button[@class='arrow right']")).click();
				i++;
			}
		}

		return driver;
	}

	@AfterTest
	public void afterTest() {
		driver.quit();
	}

}
