package course.work.s3;

public interface PhotoStorage {

    PhotoUUID uploadPhoto(Photo photo);

    Photo getPhoto(PhotoUUID photoUUID);

    void deletePhoto(PhotoUUID photoUUID);
}
