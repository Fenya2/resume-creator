package course.work.controller;

import course.work.dao.UserRepository;
import course.work.model.User;
import course.work.model.resume.Resume;
import course.work.model.resume.ResumeDetails;
import course.work.service.resume.InvalidOwnerException;
import course.work.service.resume.ResumeDetailsExistsException;
import course.work.service.resume.ResumeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static course.work.controller.AuthenticationUtils.extractLoginFromAuthentication;

@Controller
public class ResumeController {
    private static final String REDIRECT_RESUME = "redirect:/resume/";
    private final UserRepository userRepository;
    private final ResumeService resumeService;

    public ResumeController(UserRepository userRepository, ResumeService resumeService) {
        this.userRepository = userRepository;
        this.resumeService = resumeService;
    }

    @GetMapping("/resume/{id}")
    @Transactional
    public String getResume(Authentication authentication,
                            @PathVariable long id, Model model,
                            HttpServletResponse response) {
        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();
        ResumeDetails resumeDetails = resumeService.getDetails(id);
        try {
            resumeService.checkResumeOwnsUser(resumeDetails, user);
        } catch (InvalidOwnerException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        model.addAttribute("resumeDetails", resumeDetails);
        model.addAttribute("resume", resumeService.getResume(resumeDetails));

        return "resume";
    }

    @PostMapping("/resume/create")
    @Transactional
    public String createResume(Authentication authentication, Model model) {
        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();
        ResumeDetails resumeDetails = resumeService.createFor(user);
        return REDIRECT_RESUME + resumeDetails.getId();
    }

    @PatchMapping("/resume/{id}/meta")
    @Transactional
    public String saveResumeDetails(
            @PathVariable long id,
            Authentication authentication,
            @RequestParam String detailsName,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response) {
        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();

        try {
            resumeService.updateUserResumeDetails(user, detailsName, id);
        } catch (ResumeDetailsExistsException e) {
            redirectAttributes.addFlashAttribute("error", 1);
            redirectAttributes.addFlashAttribute("preservedName", detailsName);
            return REDIRECT_RESUME + id;
        } catch (InvalidOwnerException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        return REDIRECT_RESUME + id;
    }

    @PostMapping("/resume/{id}")
    @Transactional
    public String saveResume(
            Authentication authentication,
            @PathVariable long id,
            @ModelAttribute("resume") Resume resume,
            @RequestParam("photo") MultipartFile photo) {
        return REDIRECT_RESUME + id;
    }
}
