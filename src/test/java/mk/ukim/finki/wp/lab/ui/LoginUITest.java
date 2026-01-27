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
 * UI тестови за страната за најава на корисник.
 *
 * Тестовите проверуваат:
 *  - дали формата за логирање се вчитува успешно
 *  - дали при внесување погрешни креденцијали се прикажува порака за грешка
 *
 * Selenium WebDriver се користи за интеракција со реален фронт-енд
 * на дефиниран порт за Spring Boot апликацијата (9091).
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=9091"
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginUITest {

    private WebDriver driver;

    /**
     * Иницијализација на WebDriver пред секој тест.
     */
    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/google-chrome");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
    }

    /**
     * Проверува дали страницата за логирање се отвора и прикажува точен наслов.
     */
    @Test
    @Order(1)
    void testOpenLoginPage() {
        driver.get("http://localhost:9091/login");

        WebElement title = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));

        assertEquals("Sign in", title.getText());
    }

    /**
     * Проверува дали при внесување погрешни податоци се прикажува порака за грешка.
     */
    @Test
    @Order(2)
    void testInvalidLoginShowsError() {
        driver.get("http://localhost:9091/login");

        driver.findElement(By.id("username")).sendKeys("wrong");
        driver.findElement(By.id("password")).sendKeys("wrong");
        driver.findElement(By.id("submit")).click();

        WebElement error = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("text-danger")));

        assertTrue(error.isDisplayed());
    }

    /**
     * Затворање на WebDriver инстанцата по секој тест.
     */
    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
