package mk.ukim.finki.wp.lab.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import mk.ukim.finki.wp.lab.model.User;
import mk.ukim.finki.wp.lab.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public String getLoginPage(Model model) {
        model.addAttribute("bodyContent", "login");
        return "master-template";
    }

    @PostMapping
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpServletRequest req) {
        User user;

        try {
            user = this.authService.login(username, password);
            req.getSession().setAttribute("user", user);
            return "redirect:/dishes";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bodyContent", "login");
            return "master-template";
        }
    }
}


