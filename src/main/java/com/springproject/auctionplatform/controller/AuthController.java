package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.DTO.UserLoginDTO;
import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.springframework.validation.BindingResult.MODEL_KEY_PREFIX;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        if (!model.containsAttribute("loginDTO")) {
            model.addAttribute("loginDTO", new UserLoginDTO());
        }

        return "login";
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        if (!model.containsAttribute("registerDTO")) {
            model.addAttribute("registerDTO", new UserRegisterDTO());
        }

        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute("registerDTO") UserRegisterDTO registerDTO,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerDTO", registerDTO);
            redirectAttributes.addFlashAttribute(MODEL_KEY_PREFIX + "registerDTO", bindingResult);

            return "redirect:register";
        }

        userService.createUser(registerDTO);

        return "redirect:login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginDTO") UserLoginDTO loginDTO,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes, HttpSession session) {
        // TODO what happens if an error is thrown here(is redirect login called)?
        User user = userService.validateLogin(loginDTO);
        System.out.println(user);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        }

        if (bindingResult.hasErrors() || user == null) {
            System.out.println(bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
            redirectAttributes.addFlashAttribute(MODEL_KEY_PREFIX + "loginDTO", bindingResult);
            // TODO add redirectURL?
            // redirectAttributes.addAttribute("redirectURL", redirectURL);

            return "redirect:login";
        }

        session.setAttribute("user", user);
        System.out.println("Here");
        return "redirect:/home";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/home";
    }
}
