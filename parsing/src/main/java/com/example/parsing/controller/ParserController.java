package com.example.parsing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.parsing.pojo.ChoiseCityPojo;
import com.example.parsing.pojo.ParseInfoPojo;

@RestController
@RequestMapping("/api")
public class ParserController
{
	private static final Logger LOG = LoggerFactory.getLogger(ParserController.class);
	private static final String DRIVER_URL = "C:\\Users\\one-w\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
	
	@GetMapping("/parse")
	public List<ParseInfoPojo> getEmails(@RequestParam("site") String siteUrl) throws InterruptedException
	{
		WebDriver driver = new ChromeDriver();
		List<ParseInfoPojo> result = new ArrayList<ParseInfoPojo>();
		
		try
		{
			// укажем настройки
			System.setProperty("webdriver.chrome.driver", DRIVER_URL);
			
			// зайдём на сайт
			driver.get(siteUrl);
			
			// нажмём на кнопку выбора города
			driver.findElement(By.className("city-select")).click();
			Thread.sleep(2500);
			
			// соберём список адресов афиши по городам
			WebElement cityUl = driver.findElement(By.id("locations-dropdown"));
			List<ChoiseCityPojo> cityPojoList = new ArrayList<ChoiseCityPojo>();
			
			cityUl.findElements(By.className("cities-menu-item"))
				.forEach(cityLi ->
				{
					WebElement el = cityLi.findElement(By.tagName("a"));
					
					cityPojoList.add(new ChoiseCityPojo(el.getAttribute("href"), el.getAttribute("data-value")));
				});
			
			// пробежимся по всем страницам всех городов
			for (ChoiseCityPojo cityPojo : cityPojoList)
			{
				if (!cityPojo.getUrl().equals(siteUrl))
				{
					// получим страницу
					/*driver.get(cityPojo.getUrl());
					
					Thread.sleep(3000);*/
				}				
				else
				{
					// начнём заполнять объект
					ParseInfoPojo resultPojo = new ParseInfoPojo();
					
					// укажем значение города
					resultPojo.setCityValue(cityPojo.getCityValue());
					
					// получим кол-во страниц
					WebElement pagination = driver.findElement(By.className("feed-pagination__buttons"));
					
					List<WebElement> pages = pagination.findElements(By.tagName("div"));
					List<String> cardLinks = new ArrayList<>();		
					
					// соберём все ссылки на карточки по каждой странице
					for (int i = 0; i < 3/*pages.size()*/; i++)
					{
						WebElement page = pages.get(i);
						
						page.click();

						Thread.sleep(2000);

						List<WebElement> cardsUrl = driver.findElements(By.className("post-title-link"));

						cardsUrl.forEach(cardUrl ->
						{
							cardLinks.add(cardUrl.getAttribute("href"));
						});
					}
					
					// откроем карточки и получим ссылку на источник
					List<String> sourceLinkList = new ArrayList<String>();
					
					for(String cardLink : cardLinks)
					{						
						try
						{
							driver.get(cardLink);
							
							Thread.sleep(3000);
							
							sourceLinkList.add(driver.findElement(By.className("navbar-item")).findElement(By.tagName("a")).getAttribute("href"));
						}
						catch (Exception e)
						{
							LOG.error(e.toString());
						}
					}
					
					// откроем ссылку источник и попробуем вытянуть email
					for (String sourceLink : sourceLinkList)
					{
						driver.get(sourceLink);
						
						resultPojo.setName(driver.getTitle());

						List<String> emails = new ArrayList<>();
						
						try
						{
							List<WebElement> allElems = driver.findElements(By.cssSelector("*"));
							
							for (WebElement el : allElems)
							{
								String text = el.getText();
								
								if (text.contains("@"))
								{
									emails.add(text);	
								}
							}
						}
						catch (Exception e)
						{
							LOG.error(e.toString());
							
							try
							{
								// если не удалось найти что-то с символом @, пробуем найти на странице contacts
								WebElement contactPage = driver.findElement(By.cssSelector("a[href*=\"contact\"]"));
								
								// открываем страницу
								contactPage.click();
								
								Thread.sleep(3000);
								
								// находим элементы
								List<WebElement> contactPageElems = driver.findElements(By.cssSelector("*"));
								
								for (WebElement contactPageElem : contactPageElems)
								{
									String text = contactPageElem.getText();
									
									if (text.contains("@"))
									{
										emails.add(text);
									}
								}							
							}
							catch (Exception ex)
							{
								LOG.error(ex.toString());
							}						
						}
						
						if (emails.isEmpty())
						{
							try
							{
								// если не удалось найти что-то с символом @, пробуем найти на странице contacts
								WebElement contactPage = driver.findElement(By.cssSelector("a[href*=\"contact\"]"));
								
								// открываем страницу
								contactPage.click();
								
								Thread.sleep(3000);
								
								// находим элементы
								List<WebElement> contactPageElems = driver.findElements(By.cssSelector("*"));
								
								for (WebElement contactPageElem : contactPageElems)
								{
									String text = contactPageElem.getText();
									
									if (text.contains("@"))
									{
										emails.add(text);
									}
								}							
							}
							catch (Exception ex)
							{
								LOG.error(ex.toString());
							}
						}
						
						if (!emails.isEmpty())
						{
							resultPojo.setEmail(emails);
						}
					}
					
					result.add(resultPojo);
				}
			}
		}
		catch (Exception e)
		{
			LOG.error(e.toString());
			
			throw e;
		}
		finally
		{
			driver.close();			
		}

		return result;
	}
	
}
