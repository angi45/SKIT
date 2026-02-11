package mk.ukim.finki.wp.lab.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI функционални тестови со Selenium WebDriver за страниците поврзани со Dish.
 *
 * Тестовите се извршуваат над вклучен Spring Boot сервер,
 * при што се проверува реална интеракција со UI:
 *  - admin може да пристапи до формата за додавање на Dish
 *  - admin успешно креира нов Dish преку интерфејсот
 *
 * Овие тестови се извршуваат со дефиниран порт (9091)
 * и користат реален веб-браузер (Chromium/Chrome).
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=9091"
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminDishUITest {

    private WebDriver driver;

    /**
     * Подготовка на Selenium WebDriver и автоматско логирање како admin
     * пред извршување на секој тест.
     */
    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/google-chrome");
        driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        driver.get("http://localhost:9091/login");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("submit")).click();
    }

    /**
     * Тест кој проверува дека admin може да ја отвори формата
     * за додавање на ново јадење.
     */
    @Test
    @Order(1)
    void testAdminCanOpenAddForm() {
        driver.get("http://localhost:9091/dishes");
        driver.findElement(By.linkText("Add New Dish")).click();
        assertTrue(driver.getPageSource().contains("Add New Dish"));
    }

    /**
     * Тест кој проверува успешно внесување на ново јадење преку UI:
     *  - пополнување на формата
     *  - избор на Chef
     *  - проверка дека новото јадење е прикажано во листата
     */
    @Test
    @Order(2)
    void testAdminCreatesDish() {
        driver.get("http://localhost:9091/dishes/dish-form");

        driver.findElement(By.id("dishId")).sendKeys("UITEST1");
        driver.findElement(By.id("name")).sendKeys("Ui Test Dish");

        // Cuisine е ENUM → мора ТОЧНО ENUM име
        driver.findElement(By.id("cuisine")).sendKeys("ITALIAN");

        driver.findElement(By.id("preparationTime")).sendKeys("10");

        // изберете барем еден chef (checkbox)
        WebElement firstChefCheckbox =
                driver.findElements(By.name("chefsId[]")).get(0);
        firstChefCheckbox.click();

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Чекаме додека се појави новото јадење во листата
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.getPageSource().contains("UITEST1"));

        assertTrue(driver.getPageSource().contains("UITEST1"));
    }

    /**
     * Затворање на WebDriver по секој тест со цел да не остануваат активни процеси.
     */
    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
