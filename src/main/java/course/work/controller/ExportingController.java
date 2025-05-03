package course.work.controller;

import course.work.dao.UserRepository;
import course.work.model.User;
import course.work.model.resume.Resume;
import course.work.model.resume.ResumeDetails;
import course.work.service.exporting.ExportingService;
import course.work.service.exporting.PdfExportResult;
import course.work.service.resume.InvalidOwnerException;
import course.work.service.resume.ResumeService;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static course.work.controller.AuthenticationUtils.extractLoginFromAuthentication;
import static course.work.service.exporting.ResumeTemplate.BASIC_TEMPLATE;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

/**
 * @author dsyromyatnikov
 * @since 02.05.2025
 */
@Controller
public class ExportingController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;
    private final ExportingService exportingService;

    public ExportingController(ResumeService resumeService, UserRepository userRepository, ExportingService exportingService) {
        this.resumeService = resumeService;
        this.userRepository = userRepository;
        this.exportingService = exportingService;
    }

    @Transactional
    @GetMapping(value = "/resume/{id}/export", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> exportResume(
            @PathVariable long id,
            Authentication authentication) {

        String login = extractLoginFromAuthentication(authentication);
        User user = userRepository.findByLogin(login).orElseThrow();

        ResumeDetails details = resumeService.getDetails(id);
        try {
            resumeService.checkResumeOwnsUser(details, user);
        } catch (InvalidOwnerException e) {
            return ResponseEntity.status(FORBIDDEN).build();
        }

        Resume resume = resumeService.getResume(details);
        PdfExportResult result = exportingService.exportResume(resume, BASIC_TEMPLATE);

        ByteArrayResource resource = new ByteArrayResource(result.data());

        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=%s.pdf".formatted(details.getName()))
                .contentLength(result.getSize())
                .contentType(APPLICATION_PDF)
                .body(resource);
    }
}
