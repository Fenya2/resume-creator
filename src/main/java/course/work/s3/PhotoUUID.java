package course.work.s3;

import java.util.UUID;

/**
 * @author dsyromyatnikov
 * @since 01.05.2025
 */
public record PhotoUUID(UUID photoId) {
    public PhotoUUID(String uuid) {
        this(UUID.fromString(uuid));
    }
}
