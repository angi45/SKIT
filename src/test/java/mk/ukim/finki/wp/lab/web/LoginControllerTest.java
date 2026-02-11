package mk.ukim.finki.wp.lab.web;

import mk.ukim.finki.wp.lab.config.WebSecurityConfig;
import mk.ukim.finki.wp.lab.config.CustomUsernamePasswordAuthenticationProvider;
import mk.ukim.finki.wp.lab.service.AuthService;
import mk.ukim.finki.wp.lab.web.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//Web MVC Test (@WebMvcTest) е интеграционен тест на Spring MVC контролерите.
//Тестираме Controller без вистински сервис слој
@WebMvcTest(LoginController.class)
@Import(WebSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CustomUsernamePasswordAuthenticationProvider authProvider;

    @Test
    @WithAnonymousUser
    void testLoginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attributeExists("bodyContent"))
                .andExpect(model().attribute("bodyContent", "login"));
    }

    @Test
    void testFailedLoginRedirectsBackToLoginPage() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "WRONG"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=BadCredentials"));
    }
}

