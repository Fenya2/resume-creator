package course.work.service.exporting;

/**
 * @author dsyromyatnikov
 * @since 02.05.2025
 */
public enum ResumeTemplate {
    BASIC_TEMPLATE("basic");

    private String templateName;

    public static final String TEMPLATE_PATH = "pdf-templates/";

    ResumeTemplate(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return TEMPLATE_PATH + templateName;
    }
}
