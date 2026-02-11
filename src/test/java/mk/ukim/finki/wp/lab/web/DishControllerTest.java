package mk.ukim.finki.wp.lab.web;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.Dish;
import mk.ukim.finki.wp.lab.model.enums.Cuisine;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.service.ChefService;
import mk.ukim.finki.wp.lab.service.DishService;
import mk.ukim.finki.wp.lab.web.controller.DishController;
import mk.ukim.finki.wp.lab.config.CustomUsernamePasswordAuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Web MVC Test (@WebMvcTest) е интеграционен тест на Spring MVC контролерите.
//Тестираме Controller без вистински сервис слој

@WebMvcTest(DishController.class)
@Import(mk.ukim.finki.wp.lab.config.WebSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishService dishService;

    @MockitoBean
    private ChefService chefService;

    @MockitoBean
    private CustomUsernamePasswordAuthenticationProvider authProvider;

    /**
     * USER може да ја види листата на јадења
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testGetDishesPageAsUser() throws Exception {
        Chef chef = new Chef("Test", "Chef", "Bio", Gender.MALE);
        Dish dish = new Dish("D_TEST", "Test Dish", Cuisine.ITALIAN, 15, List.of(chef));

        Mockito.when(dishService.listDishes())
                .thenReturn(List.of(dish));

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
    @WithMockUser(roles = {"ADMIN"})
    void testAddDishFormAsAdmin() throws Exception {
        Chef chef = new Chef("Test", "Chef", "Bio", Gender.MALE);

        Mockito.when(chefService.listChefs())
                .thenReturn(List.of(chef));

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
    @WithMockUser(roles = {"USER"})
    void testAddDishFormAsUserForbidden() throws Exception {
        mockMvc.perform(get("/dishes/dish-form"))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може успешно да креира јадење
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateDishAsAdmin() throws Exception {
        mockMvc.perform(post("/dishes/add")
                        .param("dishId", "D_NEW")
                        .param("name", "New Dish")
                        .param("cuisine", "ITALIAN")
                        .param("preparationTime", "10")
                        .param("chefsId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes"));
    }

    /**
     * USER НЕ СМЕЕ да креира јадење
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateDishAsUserForbidden() throws Exception {
        mockMvc.perform(post("/dishes/add")
                        .param("dishId", "FAIL")
                        .param("name", "Fail Dish")
                        .param("cuisine", "ITALIAN")
                        .param("preparationTime", "10")
                        .param("chefsId", "1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може успешно да избрише јадење
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteDishAsAdmin() throws Exception {
        Mockito.when(dishService.findById(1L))
                .thenReturn(new Dish("D_TEST", "Test Dish", Cuisine.ITALIAN, 15, List.of()));

        mockMvc.perform(post("/dishes/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes"));
    }

    /**
     * USER НЕ СМЕЕ да брише јадење
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteDishAsUserForbidden() throws Exception {
        mockMvc.perform(post("/dishes/delete/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
