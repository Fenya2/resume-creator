package course.work.controller;

import course.work.dao.UserRepository;
import course.work.dto.UserRegisterDto;
import course.work.model.User;
import course.work.sec.CustomOAuth2UserService;
import course.work.sec.SecurityConfig;
import course.work.service.auth.RegistrationService;
import course.work.service.auth.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    @GetMapping(SecurityConfig.LOGIN_PAGE_ENDPOINT)
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
    public String homePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();
        String username = user.getUserName() != null ? user.getUserName() : user.getLogin();

        model.addAttribute("username", username);
        return "home";
    }

    private String extractLoginFromAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            String provider = oAuth2Token.getAuthorizedClientRegistrationId();
            String sub = oAuth2Token.getPrincipal().getAttribute("sub");
            return CustomOAuth2UserService.getOauth2UserLogin(provider, sub);
        }
        if(authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
        {
            return usernamePasswordAuthenticationToken.getName();
        }
        throw new IllegalArgumentException("Unknown authorization");
    }
}
