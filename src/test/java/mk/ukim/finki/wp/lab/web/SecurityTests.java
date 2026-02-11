package mk.ukim.finki.wp.lab.web;

import mk.ukim.finki.wp.lab.config.CustomUsernamePasswordAuthenticationProvider;
import mk.ukim.finki.wp.lab.web.controller.ChefController;
import mk.ukim.finki.wp.lab.web.controller.DishController;
import mk.ukim.finki.wp.lab.web.controller.LoginController;
import mk.ukim.finki.wp.lab.service.AuthService;
import mk.ukim.finki.wp.lab.service.ChefService;
import mk.ukim.finki.wp.lab.service.DishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Проверка на Spring Security конфигурација преку MockMvc.
 */
//Web MVC Test (@WebMvcTest) е интеграционен тест на Spring MVC контролерите.
//Тестираме Controller без вистински сервис слој
@WebMvcTest({DishController.class, ChefController.class, LoginController.class})
@Import(mk.ukim.finki.wp.lab.config.WebSecurityConfig.class)
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishService dishService;

    @MockitoBean
    private ChefService chefService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUsernamePasswordAuthenticationProvider authProvider;

    /**
     * Анонимен корисник при обид за пристап до /dishes се пренасочува кон login
     */
    @Test
    void anonymousAccessToProtectedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dishes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Корисник со улога USER не смее да пристапи до admin-only страница
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userCannotAccessAdminPages() throws Exception {
        mockMvc.perform(get("/chefs/chef-form"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access_denied"));
    }

    /**
     * Login страницата е јавна и достапна без автентикација
     */
    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("bodyContent"));
    }
}
