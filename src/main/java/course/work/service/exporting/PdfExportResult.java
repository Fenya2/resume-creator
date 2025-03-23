package course.work.service.exporting;

/**
 * @author dsyromyatnikov
 * @since 02.05.2025
 */
public record PdfExportResult(byte[] data) {
    public int getSize()
    {
        return data.length;
    }
}
