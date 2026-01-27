package mk.ukim.finki.wp.lab.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI тестови за страницата за листање на Dish објекти.
 *
 * Тестирано е:
 *  - нормален корисник успешно се логира
 *  - се прикажува листата на јадења преку HTML табела
 *
 * Овие тестови се извршуваат на вистински подигнат Spring Boot сервер
 * на дефинираниот порт 9091 и користат Selenium WebDriver.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=9091"
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DishesUITest {

    private WebDriver driver;

    /**
     * Иницијализација на WebDriver и автоматско логирање како обичен корисник пред секој тест.
     */
    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/google-chrome");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        driver.get("http://localhost:9091/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user");
        driver.findElement(By.id("submit")).click();
    }

    /**
     * Проверува дали страницата со листата на јадења:
     *  - успешно се вчитува
     *  - прикажува наслов "All Dishes"
     *  - содржи HTML табела со податоци
     */
    @Test
    @Order(1)
    void testDishesListLoads() {
        driver.get("http://localhost:9091/dishes");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        WebElement title = driver.findElement(By.tagName("h1"));
        assertTrue(title.getText().contains("All Dishes"));

        WebElement table = driver.findElement(By.tagName("table"));
        assertTrue(table.isDisplayed());
    }

    /**
     * Затворање на WebDriver инстанцата по секој тест.
     */
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
