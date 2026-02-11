package mk.ukim.finki.wp.lab.web;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.service.ChefService;
import mk.ukim.finki.wp.lab.service.DishService;
import mk.ukim.finki.wp.lab.web.controller.ChefController;
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
@WebMvcTest(ChefController.class)
@Import(mk.ukim.finki.wp.lab.config.WebSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class ChefControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChefService chefService;

    @MockitoBean
    private DishService dishService;

    // Mock на auth provider за WebSecurityConfig
    @MockitoBean
    private CustomUsernamePasswordAuthenticationProvider authProvider;

    /**
     * USER може да пристапи кон листата на chefs.
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testGetChefsPageAsUser() throws Exception {
        List<Chef> chefs = List.of(
                new Chef("Test", "Chef", "Bio", Gender.MALE)
        );

        Mockito.when(chefService.listChefs())
                .thenReturn(chefs);

        mockMvc.perform(get("/chefs"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("chefs"))
                .andExpect(model().attribute("bodyContent", "chefsList"));
    }

    /**
     * ADMIN може да ја отвори формата за додавање Chef.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddChefFormAsAdmin() throws Exception {
        mockMvc.perform(get("/chefs/chef-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("chef-form"))
                .andExpect(model().attributeExists("genders"));
    }

    /**
     * USER нема пристап до формата за додавање.
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testAddChefFormForbiddenAsUser() throws Exception {
        mockMvc.perform(get("/chefs/chef-form"))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може успешно да креира нов Chef.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateChefAsAdmin() throws Exception {
        mockMvc.perform(post("/chefs/add")
                        .param("firstName", "Test")
                        .param("lastName", "Chef")
                        .param("bio", "Test Bio")
                        .param("gender", "MALE")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefs"));
    }

    /**
     * USER не може да креира Chef.
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testCreateChefForbiddenAsUser() throws Exception {
        mockMvc.perform(post("/chefs/add")
                        .param("firstName", "Fail")
                        .param("lastName", "Chef")
                        .param("bio", "Fail Bio")
                        .param("gender", "MALE"))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може да избрише Chef.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteChefAsAdmin() throws Exception {
        Mockito.when(chefService.findById(1L))
                .thenReturn(new Chef("Delete", "Target", "Bio", Gender.MALE));

        mockMvc.perform(post("/chefs/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefs"));
    }

    /**
     * USER не може да брише Chef.
     */
    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteChefForbiddenAsUser() throws Exception {
        mockMvc.perform(post("/chefs/delete/1"))
                .andExpect(status().isForbidden());
    }
}
