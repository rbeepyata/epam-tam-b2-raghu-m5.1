package com.ryanair.automation;

import org.openqa.selenium.By;

public class RyanairPage {
		
	
	public static String url = "https://www.ryanair.com/gb/en/";
	
	public static By fromElement = By.xpath("(//*[@name='departureAirportName'])[1]");
	
	public static By fromButton = //By.xpath("//label[text()='From:']");
								By.xpath("(//*[@name='departureAirportName'])[1]");
	
	public static By belgiumLink = By.xpath("//*[text()=' Belgium']");
	public static By brusselsCharleroiLink = By.xpath("//*[text()='Brussels Charleroi']");
	
	public static By toElement = By.xpath("(//*[@name='destinationAirportName'])[1]");
	public static By germanyLink = By.xpath("//*[text()=' Germany']");
	public static By unitedKindgomLink = By.xpath("//*[text()=' United Kingdom']");
	public static By manchesterLink = By.xpath("//*[text()='Manchester']");
	public static By passenderTextContent = By.xpath("//div[@class='dropdown-handle']/preceding-sibling::div");
	public static By passenderDropdown = By.xpath("//div[@class='dropdown-handle']");
	public static By teenPlusOption = By.xpath("(//*[@icon-id='glyphs.plus-circle'])[2]/..");
	public static By letsGoButton = By.xpath("//button/span[text()=\"Let's go! \"]");
	public static By flightsTableHeader = By.xpath("((//flights-table)[1]//flights-table-header)[1]");
	public static By toDateHighlighted = By.xpath("(//div[@class='slide active']/div/div[@class='date'])[1]");
	public static By returnDateHighlighted = By.xpath("(//div[@class='slide active']/div/div[@class='date'])[2]");
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
