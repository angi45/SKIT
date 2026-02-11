package mk.ukim.finki.wp.lab.repository;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JPA тестови за DishRepository.
 *
 * Овие тестови ја проверуваат комуникацијата со реална PostgreSQL база
 * користејќи Testcontainers и Spring Data JPA.
 *
 * Се тестира:
 *  - зачувување и пронаоѓање на Dish по dishId
 *  - бришење на Dish по ID
 *  - листање на сите Dish поврзани со даден Chef
 */
//testiraat samo repository sloj i povrzuvanje so baza t,e testcontainers startuva vistinska baza vo Docker, koja e aktivna samo za vreme na tstot
//i ja gasi posle testot
//komunikacija megju Spring Data JPA и realna PostgreSQL
@DataJpaTest
@Testcontainers
class DishRepositoryTest {

    /**
     * Testcontainer за PostgreSQL база.
     * Се стартува пред тестовите и се гаси автоматски по нивното завршување.
     */
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("dishes_test")
                    .withUsername("postgres")
                    .withPassword("1234");

    /**
     * Динамичко поврзување на Spring Boot со Testcontainers базата.
     * Овде се конфигурира datasource и Hibernate ddl-auto.
     */
    @DynamicPropertySource
    static void setupDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    /**
     * DishRepository кој се тестира.
     */
    @Autowired
    DishRepository dishRepository;

    /**
     * ChefRepository – потребен за да се зачуваат Chef ентитети
     * кои ќе бидат поврзани со Dish (ManyToMany).
     */
    @Autowired
    ChefRepository chefRepository;

    /**
     * Chef кој ќе се користи во сите тестови.
     */
    private Chef savedChef;

    /**
     * Подготовка на тестни податоци пред секој тест.
     * Се зачувува еден Chef во базата.
     */
    @BeforeEach
    void setupTestData() {
        savedChef = chefRepository.save(new Chef("Test", "Chef", "Bio", Gender.MALE));
    }

    /**
     * Тест за:
     *  - зачувување на Dish
     *  - пронаоѓање на Dish по dishId
     */
    @Test
    void testSaveAndFindByDishId() {

        Dish dish = new Dish("TEST01", "TestDish", Cuisine.ITALIAN, 20, List.of(savedChef));

        dishRepository.save(dish);

        Dish result = dishRepository.findByDishId("TEST01");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("TestDish");
        assertThat(result.getChefs()).hasSize(1);
    }

    /**
     * Тест за успешно бришење на Dish по ID.
     */
    @Test
    void testDeleteDish() {

        Dish dish = dishRepository.save(
                new Dish("DEL01", "DeleteDish", Cuisine.FRENCH, 10, List.of(savedChef))
        );

        dishRepository.deleteById(dish.getId());

        assertThat(dishRepository.findById(dish.getId())).isEmpty();
    }

    /**
     * Тест за листање на сите Dish поврзани со даден Chef ID.
     * Се користи derived query методот:
     * findAllByChefs_Id
     */
    @Test
    void testFindAllByChefId() {

        dishRepository.save(
                new Dish("CH1", "Dish1", Cuisine.ITALIAN, 5, List.of(savedChef))
        );
        dishRepository.save(
                new Dish("CH2", "Dish2", Cuisine.MEXICAN, 10, List.of(savedChef))
        );

        List<Dish> dishes =
                dishRepository.findAllByChefs_Id(savedChef.getId());

        assertThat(dishes).hasSize(2);
    }
}
