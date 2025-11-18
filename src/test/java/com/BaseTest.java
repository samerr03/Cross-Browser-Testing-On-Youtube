package com;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

public class BaseTest {
    protected WebDriver driver;

    @Parameters("browser")
    @BeforeTest
    public void setup(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();

        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();

        } else if (browser.equalsIgnoreCase("edge")) {
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();

        } else {
            throw new IllegalArgumentException("❌ Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();
    }

    @AfterTest
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (org.openqa.selenium.NoSuchSessionException e) {
            System.out.println("⚠ Browser session already closed. Skipping driver.quit()");
        } catch (Exception e) {
            System.out.println("⚠ Error during teardown: " + e.getMessage());
        }
    }
}