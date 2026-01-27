package mk.ukim.finki.wp.lab.integration;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционен тест кој проверува целосен CRUD flow за Chef:
 *
 * ADMIN креира нов Chef преку HTTP POST барање до контролерот
 * Проверуваме во PostgreSQL база дека Chef е зачуван
 * ADMIN го брише истиот Chef преку контролерот
 * Повторно проверуваме дека Chef е целосно отстранет од базата
 *
 * Тестот ја проверува вистинската функционална низа:
 * Controller → Service → Repository → Реална PostgreSQL база
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FullFlowIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17")
                    .withDatabaseName("lab_full_integration")
                    .withUsername("postgres")
                    .withPassword("1234");

    /**
     * Поставување на Testcontainers база како извор на податоци за Spring Boot.
     */
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
    private ChefRepository chefRepository;

    /**
     * Тестира:
     *  Креирање на Chef
     *  Проверка во база
     *  Бришење на истиот Chef
     *  Проверка дека е избришан
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void fullChefCreateAndDeleteFlow() throws Exception {
        long countBefore = chefRepository.count();

        // ADMIN креира нов Chef преку контролер
        mockMvc.perform(post("/chefs/add")
                        .param("firstName", "Int")
                        .param("lastName", "Chef")
                        .param("bio", "Integration Test Chef")
                        .param("gender", Gender.MALE.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefs"));

        // Проверка дека бројот на зачувани chefs е зголемен
        long countAfterCreate = chefRepository.count();
        assertThat(countAfterCreate).isEqualTo(countBefore + 1);

        // Го наоѓаме токму креираниот Chef според прво и презиме
        Optional<Chef> createdOpt = chefRepository.findAll()
                .stream()
                .filter(c -> "Int".equals(c.getFirstName()) && "Chef".equals(c.getLastName()))
                .findFirst();

        assertThat(createdOpt).isPresent();
        Chef created = createdOpt.get();

        // ADMIN го брише истиот Chef
        mockMvc.perform(post("/chefs/delete/" + created.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefs"));

        // Проверуваме дека повеќе не постои во базата
        assertThat(chefRepository.existsById(created.getId())).isFalse();
    }
}
