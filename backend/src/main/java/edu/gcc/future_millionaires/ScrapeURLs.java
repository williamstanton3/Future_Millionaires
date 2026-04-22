package edu.gcc.future_millionaires;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.time.Duration;
import java.util.*;

public class ScrapeURLs {

    private static final String BASE_URL = "https://www.gcc.edu/Home/Academics/Faculty-Directory/PgrID/2052/PageID/";
    private static final int TOTAL_PAGES = 33;

    public static void main(String[] args) throws Exception {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        WebDriver driver = new ChromeDriver(options);
        Map<String, String> result = new LinkedHashMap<>();

        try {
            for (int page = 1; page <= TOTAL_PAGES; page++) {
                System.out.println("Scraping page " + page + " of " + TOTAL_PAGES);
                driver.get(BASE_URL + page);

                // Wait for faculty cards to load
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".card__detail--directory__name")));

                List<WebElement> cards = driver.findElements(By.cssSelector("a.card"));
                for (WebElement card : cards) {
                    try {
                        String name = card.findElement(
                                By.cssSelector(".card__detail--directory__name")).getText().trim();
                        String imageUrl = "";
                        try {
                            WebElement img = card.findElement(By.cssSelector(".card__img img"));
                            imageUrl = img.getAttribute("src");
                            // skip placeholder images
                            if (imageUrl.contains("placeholder")) imageUrl = "";
                        } catch (org.openqa.selenium.NoSuchElementException e) {
                            // no image
                        }
                        if (!name.isEmpty()) {
                            result.put(name, imageUrl);
                            System.out.println("  " + name + " -> " + imageUrl);
                        }
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        // skip malformed card
                    }
                }

                Thread.sleep(500); // be polite
            }
        } finally {
            driver.quit();
        }

        new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValue(new File("C:/Users/STANTONWJ23/IdeaProjects/Software Engineering Code/Future_Millionaires/backend/src/main/resources/professor_images.json"), result);

        System.out.println("Writing to: " + new File("src/main/resources/professor_images.json").getAbsolutePath());
        System.out.println("Done! Scraped " + result.size() + " professors.");

        System.out.println("Done! Scraped " + result.size() + " professors.");
    }
}