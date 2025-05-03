package course.work.service.exporting;

public enum Font {
    ROBOTO_REGULAR("Roboto-Regular.ttf");

    private static final String FONTS_PATH = "/fonts/";

    private final String resourceName;

    Font(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return FONTS_PATH + resourceName;
    }
}
