package mk.ukim.finki.wp.lab.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграциони тестови за проверка на Spring Security конфигурацијата.
 *
 * Тестирано е:
 *  - автоматско пренасочување кон login страница при обид за пристап до заштитени страници
 *    без автентикација
 *  - забрана на пристап кон admin-only ресурси за корисници со улога USER
 *  - јавна достапност на login страницата
 */
public class SecurityTests extends BaseWebTest {

    /**
     * Проверува дека анонимен корисник при обид за пристап
     * до заштитена страница се пренасочува кон login.
     */
    @Test
    void anonymousAccessToProtectedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dishes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    /**
     * Проверува дека корисник со улога USER не смее да пристапи
     * до admin-only страница за додавање Chef.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userCannotAccessAdminPages() throws Exception {
        mockMvc.perform(get("/chefs/chef-form"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access_denied"));
    }

    /**
     * Проверува дека страницата за најава е јавна и достапна
     * без претходна автентикација.
     */
    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("bodyContent"));
    }
}
