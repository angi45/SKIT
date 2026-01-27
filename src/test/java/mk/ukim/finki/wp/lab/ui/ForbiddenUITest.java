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
 * UI тест за проверка на правата на пристап за обичен корисник (ROLE_USER).
 *
 * Тестот проверува дека корисник без администраторска улога:
 *  - може да ја отвори страницата со јадења
 *  - не смее да има пристап до функционалностите за администрација:
 *      - додавање на јадење
 *      - уредување на јадење
 *      - бришење на јадење
 *
 * Овој тест користи Selenium WebDriver над реално стартуван Spring Boot сервер.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=9091"
)
public class ForbiddenUITest {

    private WebDriver driver;

    /**
     * Подготовка на WebDriver и логирање како обичен корисник пред секој тест.
     */
    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/google-chrome");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));

        driver.get("http://localhost:9091/login");
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user");
        driver.findElement(By.id("submit")).click();
    }

    /**
     * Проверка дека корисникот без ADMIN улога не ги гледа
     * контролите за управување со податоци (Edit/Delete/Add).
     */
    @Test
    void testUserCannotSeeAdminButtons() {
        driver.get("http://localhost:9091/dishes");

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));

        String page = driver.getPageSource();

        assertFalse(page.contains("Delete"), "USER should not see Delete button!");
        assertFalse(page.contains("Edit"), "USER should not see Edit button!");
        assertFalse(page.contains("Add New Dish"), "USER should not see Add New Dish button!");
    }

    /**
     * Гасење на WebDriver по секој тест за да нема заостанати процеси.
     */
    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
