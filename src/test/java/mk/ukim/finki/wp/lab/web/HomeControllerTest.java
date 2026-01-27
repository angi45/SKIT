package mk.ukim.finki.wp.lab.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграциони тестови за HomeController.
 *
 * Се проверува:
 *  - достапност на почетната страница за секој корисник (вкл. анонимен)
 *  - пренасочување кон login при пристап до /access_denied без логирање
 *  - вчитување на страна за забранет пристап кога корисник е логираниот
 */
public class HomeControllerTest extends BaseWebTest {

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
