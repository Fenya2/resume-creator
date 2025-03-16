package course.work.controller;

import course.work.dao.UserRepository;
import course.work.dto.UserRegisterDto;
import course.work.model.User;
import course.work.service.RegistrationService;
import course.work.service.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final RegistrationService registrationService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(RegistrationService registrationService, UserRepository userRepository) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegisterDto userDto, Model model) {
        User user = new User();
        user.setLogin(userDto.login());
        user.setPassword(userDto.password());

        try {
            registrationService.registerUser(user);
        } catch (UserAlreadyExistException e) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "/register";
        }
        return "redirect:/login";
    }

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // TODO здесь нужно будет расширяться
        User user = userRepository.findByLogin(userDetails.getUsername()).orElseThrow();
        String username = user.getUserName();
        if (username != null) {
            model.addAttribute("username", username);
        } else {
            model.addAttribute("username", user.getLogin());
        }
        return "home";
    }
}
