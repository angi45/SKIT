package mk.ukim.finki.wp.lab.web;

import mk.ukim.finki.wp.lab.config.WebSecurityConfig;
import mk.ukim.finki.wp.lab.config.CustomUsernamePasswordAuthenticationProvider;
import mk.ukim.finki.wp.lab.web.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//Web MVC Test (@WebMvcTest) е интеграционен тест на Spring MVC контролерите.
//Тестираме Controller без вистински сервис слој
@WebMvcTest(HomeController.class)
@Import(WebSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUsernamePasswordAuthenticationProvider authProvider;

    /**
     * Проверува дека почетната страница е достапна и ги прикажува потребните view елементи.
     */
    @Test
    void testHomePageLoads() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("bodyContent"))
                .andExpect(model().attribute("bodyContent", "home"));
    }

    /**
     * Проверува дека анонимен корисник при пристап до /access_denied
     * се пренасочува кон login страната.
     */
    @Test
    void anonymousAccessDeniedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/access_denied"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Проверува дека логираниот корисник може да ја види страницата за забранет пристап.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void loggedInAccessDeniedPageLoads() throws Exception {
        mockMvc.perform(get("/access_denied"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("bodyContent"))
                .andExpect(model().attribute("bodyContent", "access-denied"));
    }
}
