package com;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class YouTubeTest {
    WebDriver driver;

    @Parameters("browser")
    @BeforeTest
    public void setup(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            default:
                throw new IllegalArgumentException("Invalid browser: " + browser);
        }
        driver.manage().window().maximize();
    }

    @Test
    public void searchAndPlayVideo() throws InterruptedException {
        driver.get("https://www.youtube.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Accept cookies if any
        try {
            WebElement agreeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(., 'Accept all') or contains(., 'I agree')]")));
            agreeButton.click();
        } catch (Exception e) {
            System.out.println("No cookie popup found.");
        }

        // Search for 'Laptop'
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("search_query")));
        searchBox.sendKeys("Apple Laptop ");
        searchBox.sendKeys(Keys.ENTER);

        // Wait for search results
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@id='video-title']")));

        // Scroll down to load more videos (10-12)
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 4; i++) {
            js.executeScript("window.scrollBy(0,1000);");
            Thread.sleep(1000);
        }

        // Find all video links
        List<WebElement> videos = driver.findElements(By.xpath("//a[@id='video-title']"));
        System.out.println("Videos found: " + videos.size());

        if (videos.size() > 10) {
            System.out.println("Greater than 10 videos found, selecting first available.");
        }

        // Select 10th video if available
        WebElement videoToPlay = videos.size() >= 10 ? videos.get(9) : videos.get(0);
        js.executeScript("arguments[0].scrollIntoView(true);", videoToPlay);
        js.executeScript("arguments[0].click();", videoToPlay);

        // Wait for player to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("video")));

        // Try to click play button if autoplay is blocked
        try {
            WebElement playButton = driver.findElement(By.cssSelector("button.ytp-play-button"));
            js.executeScript("arguments[0].click();", playButton);
            System.out.println("Play button clicked manually.");
        } catch (Exception e) {
            System.out.println("Autoplay already running or play button not found.");
        }

        // Let the video play for 5 seconds
        Thread.sleep(15000);
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}