package mk.ukim.finki.wp.lab.web;

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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграциони тестови за DishController.
 *
 * ЛОГИКА:
 *  - USER: може само да гледа листа
 *  - ADMIN: може да гледа форми + CRUD операции
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChefRepository chefRepository;

    @Autowired
    private DishRepository dishRepository;

    private Long chefId;
    private Long dishId;

    @BeforeEach
    void setup() {
        Chef chef;
        if (chefRepository.count() == 0) {
            chef = chefRepository.save(new Chef("Test", "Chef", "Bio", Gender.MALE));
        } else {
            chef = chefRepository.findAll().get(0);
        }
        chefId = chef.getId();

        Dish dish = dishRepository.save(
                new Dish("D_TEST", "Test Dish", Cuisine.ITALIAN, 15, List.of(chef))
        );
        dishId = dish.getId();
    }

    /**
     * USER може да ја види листата на јадења
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetDishesPageAsUser() throws Exception {
        mockMvc.perform(get("/dishes"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("dishes"))
                .andExpect(model().attribute("bodyContent", "listDishes"));
    }

    /**
     * ADMIN може да ја отвори формата за додавање
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddDishFormAsAdmin() throws Exception {
        mockMvc.perform(get("/dishes/dish-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("dish-form"))
                .andExpect(model().attributeExists("chefs"))
                .andExpect(model().attributeExists("cuisines"));
    }

    /**
     * USER НЕ СМЕЕ да ја отвори формата за додавање
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddDishFormAsUserForbidden() throws Exception {
        mockMvc.perform(get("/dishes/dish-form"))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може успешно да креира јадење
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateDishAsAdmin() throws Exception {
        mockMvc.perform(post("/dishes/add")
                        .param("dishId", "D_NEW")
                        .param("name", "New Dish")
                        .param("cuisine", "ITALIAN")
                        .param("preparationTime", "10")
                        .param("chefsId", chefId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes"));
    }

    /**
     * USER НЕ СМЕЕ да креира јадење
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateDishAsUserForbidden() throws Exception {
        mockMvc.perform(post("/dishes/add")
                        .param("dishId", "FAIL")
                        .param("name", "Fail Dish")
                        .param("cuisine", "ITALIAN")
                        .param("preparationTime", "10")
                        .param("chefsId", chefId.toString()))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може да ја отвори формата за уредување
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testEditDishFormAsAdmin() throws Exception {
        mockMvc.perform(get("/dishes/dish-form/" + dishId))
                .andExpect(status().isOk())
                .andExpect(view().name("dish-form"))
                .andExpect(model().attributeExists("dish"))
                .andExpect(model().attributeExists("chefs"));
    }

    /**
     * USER НЕ СМЕЕ да ја отвори формата за уредување
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testEditDishFormAsUserForbidden() throws Exception {
        mockMvc.perform(get("/dishes/dish-form/" + dishId))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може успешно да избрише јадење
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteDishAsAdmin() throws Exception {
        mockMvc.perform(post("/dishes/delete/" + dishId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes"));
    }

    /**
     * USER НЕ СМЕЕ да брише јадење
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteDishAsUserForbidden() throws Exception {
        mockMvc.perform(post("/dishes/delete/" + dishId))
                .andExpect(status().isForbidden());
    }
}
