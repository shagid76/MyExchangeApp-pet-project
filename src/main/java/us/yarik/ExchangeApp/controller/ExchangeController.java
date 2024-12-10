package us.yarik.ExchangeApp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import us.yarik.ExchangeApp.dto.ExchangeRequestDTO;
import us.yarik.ExchangeApp.dto.WalletAmountDTO;
import us.yarik.ExchangeApp.model.User;
import us.yarik.ExchangeApp.model.WalletAmount;
import us.yarik.ExchangeApp.service.ExchangeService;
import us.yarik.ExchangeApp.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ExchangeController {
    private final UserService userService;
    private final ExchangeService exchangeService;

    @GetMapping("/redirect_to_balance")
    public String redirectToBalance(Authentication authentication) {
        String userName = authentication.getName();
        Optional<User> user = userService.findByName(userName);
        return user.map(value -> "redirect:/balance/" + value.getId()).orElse("redirect:/login");
    }

    @GetMapping("/balance/{id}")
    public String balance(@PathVariable("id") Integer id, Model model) {
        Optional<User> user = Optional.ofNullable(userService.findById(id));
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("id", id);
            model.addAttribute("walletAmounts", exchangeService.findAllByUserId(id));

            return "balance";
        }
        return "redirect:/login";
    }

    @GetMapping("/replenishment/{id}")
    public String getReplenishment(Model model, User user) {
        WalletAmount walletAmount = new WalletAmount();
        model.addAttribute("id", user.getId());
        model.addAttribute("walletAmount", walletAmount);
        return "replenishment";
    }

    @PostMapping("/replenishment/{id}")
    public String postReplenishment(@PathVariable Integer id, @Valid @ModelAttribute("walletAmount") WalletAmountDTO walletAmountDTO,
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("walletAmount", walletAmountDTO);
            return "replenishment";
        }
        exchangeService.addReplenishment(id, walletAmountDTO.getCurrency(), walletAmountDTO.getAmount());
        return "redirect:/balance/" + id;
    }

    @GetMapping("/exchange/{id}")
    public String getExchange(Model model, @PathVariable("id") Integer id) {
        List<WalletAmount> walletContent = exchangeService.findAllByUserId(id);
        User user = userService.findById(id);
        model.addAttribute("id", user.getId());
        model.addAttribute("walletAmountList", walletContent);
        model.addAttribute("exchangeRequest", new ExchangeRequestDTO());
        return "exchange";
    }

    @PostMapping("/exchange/{id}")
    public String postExchange(@PathVariable("id") Integer id,
                               @ModelAttribute("exchangeRequest") @Valid ExchangeRequestDTO exchangeRequestDTO,
                               BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "exchange";
        }
        try {
            exchangeService.exchange(exchangeRequestDTO, id);
        }catch (IllegalArgumentException e){
            model.addAttribute("error", e);
            model.addAttribute("id", id);
            model.addAttribute("walletAmountList", exchangeService.findAllByUserId(id));
            return "exchange";
        }
            return "redirect:/balance/" + id;

    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
