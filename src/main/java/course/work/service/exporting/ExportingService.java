package course.work.service.exporting;

import course.work.model.resume.Resume;

public interface ExportingService {
    PdfExportResult exportResume(Resume resume, ResumeTemplate resumeTemplate) throws ExportingException;
}
