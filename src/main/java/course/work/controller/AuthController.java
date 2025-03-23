package course.work.controller;

import course.work.dao.UserRepository;
import course.work.dto.UserRegisterDto;
import course.work.model.User;
import course.work.model.resume.ResumeDetails;
import course.work.sec.SecurityConfig;
import course.work.service.auth.RegistrationService;
import course.work.service.auth.UserAlreadyExistException;
import course.work.service.resume.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static course.work.controller.AuthenticationUtils.extractLoginFromAuthentication;

@Controller
public class AuthController {
    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private final ResumeService resumeService;

    @Autowired
    public AuthController(RegistrationService registrationService, UserRepository userRepository, ResumeService resumeService) {
        this.registrationService = registrationService;
        this.userRepository = userRepository;
        this.resumeService = resumeService;
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
    @Transactional
    public String registerUser(@ModelAttribute UserRegisterDto userDto, Model model) {
        User user = new User();
        user.setLogin(userDto.login());
        user.setPassword(userDto.password());

        try {
            registrationService.registerUser(user);
        } catch (UserAlreadyExistException e) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "register";
        }
        return "redirect:/login";
    }

    @GetMapping("/")
    @Transactional
    public String homePage(Authentication authentication, Model model) {
        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();
        String username = user.getUserName() != null ? user.getUserName() : user.getLogin();

        List<ResumeDetails> userResumeDetails = resumeService.getUserResumesDetails(user);

        model.addAttribute("username", username);
        model.addAttribute("resumes", userResumeDetails);
        return "home";
    }
}
