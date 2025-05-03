package course.work.service.exporting;

import com.lowagie.text.pdf.BaseFont;
import course.work.model.resume.Resume;
import course.work.s3.Photo;
import course.work.s3.PhotoStorage;
import course.work.s3.PhotoUUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class ExportingServiceImpl implements ExportingService {

    private final TemplateEngine templateEngine;
    private final PhotoStorage photoStorage;

    @Autowired
    public ExportingServiceImpl(TemplateEngine templateEngine, PhotoStorage photoStorage) {
        this.templateEngine = templateEngine;
        this.photoStorage = photoStorage;
    }

    @Override
    public PdfExportResult exportResume(Resume resume, ResumeTemplate resumeTemplate) {
        try {
            Context context = new Context();
            context.setVariable("resume", resume);

            if (resume.getGeneralInfo().getPhotoId() != null) {
                Photo photo = photoStorage.getPhoto(new PhotoUUID(resume.getGeneralInfo().getPhotoId()));
                String photoBase64 = Base64.getEncoder().encodeToString(photo.data());
                context.setVariable("photoBase64", photoBase64);
            }

            String htmlContent = templateEngine.process(
                    resumeTemplate.getTemplateName(),
                    context);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            addFonts(renderer);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);

            return new PdfExportResult(outputStream.toByteArray());
        } catch (Exception e) {
            throw new ExportingException("Can't generate pdf using %s".formatted(resumeTemplate.getTemplateName()), e);
        }
    }

    private void addFonts(ITextRenderer renderer) throws IOException {
        for (Font font : Font.values()) {
            renderer.getFontResolver().addFont(
                    getClass().getResource(font.getResourceName()).toExternalForm(),
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
        }
    }
}