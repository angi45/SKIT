package mk.ukim.finki.wp.lab.integration;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import mk.ukim.finki.wp.lab.repository.jpa.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционен тест за ажурирање (update) на Dish.
 *
 * Овој тест ја проверува целата функционална низа:
 * Controller → Service → Repository → PostgreSQL база
 *
 * НЕ се тестира LAZY loading на релации (ManyToMany),
 * бидејќи тоа е надвор од одговорноста на integration тест.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DishUpdateIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("lab_update_integration")
                    .withUsername("postgres")
                    .withPassword("1234");

    @DynamicPropertySource
    static void setupDb(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ChefRepository chefRepository;

    private Dish dish;

    /**
     * Подготовка на тестни податоци:
     * - се креира Chef
     * - се креира Dish поврзан со Chef
     */
    @BeforeEach
    void init() {

        Chef chef = chefRepository.save(
                new Chef("Update", "Chef", "Bio", Gender.FEMALE)
        );

        dish = dishRepository.save(new Dish("UPD1", "OldName", Cuisine.ITALIAN, 30, List.of(chef)));
    }

    /**
     * Тест за update flow:
     * - POST /dishes/edit/{id}
     * - redirect кон /dishes
     * - проверка дека основните полиња се ажурирани
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateDishFlow() throws Exception {

        Long chefId = dish.getChefs().get(0).getId();

        mockMvc.perform(post("/dishes/edit/" + dish.getId())
                        .param("dishId", "UPD1")
                        .param("name", "Updated Name")
                        .param("cuisine", Cuisine.MEXICAN.name())
                        .param("preparationTime", "15")
                        .param("chefsId", chefId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes"));

        // Повторно вчитување на Dish од базата
        Dish updated = dishRepository.findById(dish.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getCuisine()).isEqualTo(Cuisine.MEXICAN);
        assertThat(updated.getPreparationTime()).isEqualTo(15);

    }
}
