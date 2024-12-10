package us.yarik.ExchangeApp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import us.yarik.ExchangeApp.model.User;
import us.yarik.ExchangeApp.service.RegistrationService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final RegistrationService registrationService;

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String postRegistration(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            registrationService.registerUser(user);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
}
