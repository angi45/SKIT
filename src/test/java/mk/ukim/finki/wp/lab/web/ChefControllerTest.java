package mk.ukim.finki.wp.lab.web;

import mk.ukim.finki.wp.lab.model.Chef;
import mk.ukim.finki.wp.lab.model.enums.Gender;
import mk.ukim.finki.wp.lab.repository.jpa.ChefRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграциони тестови за ChefController со MockMvc.
 *
 * Се тестираат:
 *  - пристапни права според улога (USER vs ADMIN)
 *  - вчитување на листа со chefs
 *  - отварање на форма за додавање (само за ADMIN)
 *  - креирање и бришење Chef од страна на ADMIN
 *
 * Целта е да се осигури дека контролерот правилно ги користи
 * правилата за пристап дефинирани во Spring Security.
 */
public class ChefControllerTest extends BaseWebTest {

    @Autowired
    private ChefRepository chefRepository;

    /**
     * USER може да пристапи кон листата на chefs.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetChefsPageAsUser() throws Exception {
        mockMvc.perform(get("/chefs"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("chefs"))
                .andExpect(model().attributeExists("bodyContent"))
                .andExpect(model().attribute("bodyContent", "chefsList"));
    }

    /**
     * ADMIN може да ја отвори формата за додавање Chef.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddChefFormForbiddenAsUser() throws Exception {
        mockMvc.perform(get("/chefs/chef-form"))
                .andExpect(status().isForbidden());
    }

    /**
     * ADMIN може успешно да креира нов Chef.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateChefAsAdmin() throws Exception {
        mockMvc.perform(post("/chefs/add")
                        .param("firstName", "Test")
                        .param("lastName", "Chef")
                        .param("bio", "Test Bio")
                        .param("gender", "MALE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefs"));
    }

    /**
     * USER не може да креира Chef.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteChefAsAdmin() throws Exception {
        Chef chef = chefRepository.save(new Chef("Delete", "Target", "Bio", Gender.MALE));

        mockMvc.perform(post("/chefs/delete/" + chef.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chefs"));
    }

    /**
     * USER не може да брише Chef.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteChefForbiddenAsUser() throws Exception {
        mockMvc.perform(post("/chefs/delete/1"))
                .andExpect(status().isForbidden());
    }
}
