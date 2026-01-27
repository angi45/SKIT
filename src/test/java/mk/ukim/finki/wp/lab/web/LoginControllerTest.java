package mk.ukim.finki.wp.lab.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграциони тестови за Login функционалноста преку LoginController.
 *
 * Се тестира:
 *  - вчитување на login страната како анонимен корисник
 *  - успешно логирање со валидни креденцијали и пренасочување кон /dishes
 *  - неуспешно логирање и враќање назад на /login со параметар за грешка
 */
public class LoginControllerTest extends BaseWebTest {

    /**
     * Проверува дека login страницата е достапна за анонимен корисник.
     */
    @Test
    @WithAnonymousUser
    void testLoginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("bodyContent"))
                .andExpect(model().attribute("bodyContent", "login"));
    }

    /**
     * Проверува дека успешното логирање резултира со redirect кон /dishes.
     */
    @Test
    void testSuccessfulLoginRedirectsToDishes() throws Exception {
        ResultActions result = mockMvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "admin"));

        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dishes"));
    }

    /**
     * Проверува дека неуспешно логирање предизвикува пренасочување
     * назад кон login страницата со параметар error=BadCredentials.
     */
    @Test
    void testFailedLoginRedirectsBackToLoginPage() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "WRONG"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=BadCredentials"));
    }
}
