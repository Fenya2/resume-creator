package course.work.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResumeController {
    @GetMapping("/create")
    public String getCreationTemplatesPage() {
        // TODO
        return null;
    }
}
