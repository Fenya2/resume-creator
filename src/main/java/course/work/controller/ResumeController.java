package course.work.controller;

import course.work.dao.UserRepository;
import course.work.model.User;
import course.work.model.resume.Resume;
import course.work.model.resume.ResumeDetails;
import course.work.s3.Photo;
import course.work.s3.PhotoStorage;
import course.work.s3.PhotoUUID;
import course.work.service.resume.CantUpdateResumeException;
import course.work.service.resume.InvalidOwnerException;
import course.work.service.resume.ResumeDetailsExistsException;
import course.work.service.resume.ResumeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;

import static course.work.controller.AuthenticationUtils.extractLoginFromAuthentication;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Controller
@RequestMapping("/resume")
public class ResumeController {
    private static final String REDIRECT_RESUME = "redirect:/resume/";
    private final UserRepository userRepository;
    private final ResumeService resumeService;
    private final PhotoStorage photoStorage;

    public ResumeController(UserRepository userRepository, ResumeService resumeService, PhotoStorage photoStorage) {
        this.userRepository = userRepository;
        this.resumeService = resumeService;
        this.photoStorage = photoStorage;
    }

    @GetMapping("/{id}")
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
            response.setStatus(FORBIDDEN.value());
            return null;
        }

        Resume resume = resumeService.getResume(resumeDetails);
        model.addAttribute("resumeDetails", resumeDetails);
        model.addAttribute("resume", resume);

        String photoId = resume.getGeneralInfo().getPhotoId();
        if (photoId != null) {
            Photo photo = photoStorage.getPhoto(new PhotoUUID(photoId));
            String base64Photo = Base64.getEncoder().encodeToString(photo.data());
            model.addAttribute("photoBase64", base64Photo);
            model.addAttribute("photoContentType", MediaType.IMAGE_JPEG_VALUE);
        }

        return "resume";
    }

    @PostMapping("/create")
    @Transactional
    public String createResume(Authentication authentication, Model model) {
        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();
        ResumeDetails resumeDetails = resumeService.createFor(user);
        return REDIRECT_RESUME + resumeDetails.getId();
    }

    @PatchMapping("/{id}/meta")
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
            response.setStatus(FORBIDDEN.value());
            return null;
        }
        return REDIRECT_RESUME + id;
    }

    @PutMapping("/{id}")
    @Transactional
    public String updateResume(
            Authentication authentication,
            @PathVariable long id,
            @ModelAttribute("resume") Resume resume,
            @RequestParam("photo") MultipartFile photo,
            HttpServletResponse response) {
        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();

        ResumeDetails resumeDetails = resumeService.getDetails(id);
        try {
            resumeService.checkResumeOwnsUser(resumeDetails, user);
        } catch (InvalidOwnerException e) {
            response.setStatus(FORBIDDEN.value());
            return null;
        }

        try {
            resumeService.updateResume(resumeDetails, resume, photo);
        } catch (CantUpdateResumeException e) {
            response.setStatus(BAD_REQUEST.value());
            return null;
        }
        return REDIRECT_RESUME + id;
    }
}
