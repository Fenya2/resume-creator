package course.work.service.resume;

import course.work.dao.ResumeDetailsRepository;
import course.work.model.User;
import course.work.model.resume.Resume;
import course.work.model.resume.ResumeDetails;
import course.work.s3.Photo;
import course.work.s3.PhotoStorage;
import course.work.s3.PhotoUUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeServiceImpl implements ResumeService {
    private static final Logger LOG = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final ResumeDetailsRepository resumeDetailsRepository;
    private final PhotoStorage photoStorage;

    @Autowired
    public ResumeServiceImpl(ResumeDetailsRepository resumeDetailsRepository, PhotoStorage photoStorage) {
        this.resumeDetailsRepository = resumeDetailsRepository;
        this.photoStorage = photoStorage;
    }

    @Override
    public ResumeDetails createFor(User user) {
        ResumeDetails resumeDetails = new ResumeDetails();
        resumeDetails.setDateCreated(Instant.now());
        resumeDetails.setName(UUID.randomUUID().toString());
        resumeDetails.setOwner(user);
        Resume emptyResume = createUserResumeFromTemplate();
        resumeDetails.setResume(marshalUserResume(emptyResume));
        resumeDetailsRepository.save(resumeDetails);
        LOG.atInfo().log("Create resume {} for {}", resumeDetails.getId(), user.getLogin());
        return resumeDetails;
    }

    private static String marshalUserResume(Resume resume) {
        DumperOptions options = new DumperOptions();
        options.setExplicitStart(false);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        Yaml yaml = new Yaml(new Constructor(Resume.class, new LoaderOptions()));
        return yaml.dumpAs(resume, Tag.MAP, null);
    }

    private static Resume createUserResumeFromTemplate() {
        Resume.ContactInfo contactInfo = new Resume.ContactInfo();
        Resume.AdditionalInfo additionalInfo = new Resume.AdditionalInfo();
        Resume.GeneralInfo generalInfo = new Resume.GeneralInfo();

        Resume resume = new Resume();
        resume.setGeneralInfo(generalInfo);
        resume.setAdditionalInfo(additionalInfo);
        resume.setContactInfo(contactInfo);

        return resume;
    }

    @Override
    public void updateUserResumeDetails(User user, String detailsName, long detailsId) {
        if (resumeDetailsRepository.existsByOwnerAndName(user, detailsName)) {
            throw new ResumeDetailsExistsException(detailsName);
        }
        ResumeDetails updatingDetails = getDetails(detailsId);
        checkResumeOwnsUser(updatingDetails, user);
        updatingDetails.setName(detailsName);
        LOG.atInfo().log("resume details %d update name to %s".formatted(detailsId, detailsName));
        resumeDetailsRepository.save(updatingDetails);
    }

    @Override
    public void checkResumeOwnsUser(ResumeDetails resumeDetails, User user) throws InvalidOwnerException {
        if (resumeDetails.getOwner().getId() != user.getId()) {
            throw new InvalidOwnerException();
        }
    }

    @Override
    public ResumeDetails getDetails(long id) {
        return resumeDetailsRepository.findById(id).orElseThrow();
    }

    @Override
    public List<ResumeDetails> getUserResumesDetails(User user) {
        return resumeDetailsRepository.findByOwnerOrderByDateCreatedDesc(user);
    }

    @Override
    public void updateResume(ResumeDetails resumeDetails, Resume resume, MultipartFile photo) {
        ResumeDetails oldDetails = getDetails(resumeDetails.getId());
        Resume oldResume = unmarshallUserResume(oldDetails.getResume());

        String oldPhotoUuid = oldResume.getGeneralInfo().getPhotoId();
        if (!photo.isEmpty()) {
            if (oldPhotoUuid != null) {
                photoStorage.deletePhoto(new PhotoUUID(oldPhotoUuid));
            }
            byte[] photoData;
            try {
                photoData = photo.getBytes();
            } catch (IOException e) {
                throw new CantUpdatePhotoException("Can't extract photo", e);
            }
            PhotoUUID newPhotoUuid = photoStorage.uploadPhoto(new Photo(photoData));
            resume.getGeneralInfo().setPhotoId(newPhotoUuid.photoId().toString());
        } else {
            resume.getGeneralInfo().setPhotoId(oldPhotoUuid);
        }
        resumeDetails.setResume(marshalUserResume(resume));
        LOG.atInfo().log("resume {} updated", resumeDetails.getId());
    }

    @Override
    public Resume getResume(ResumeDetails details) {
        return unmarshallUserResume(details.getResume());
    }

    private static Resume unmarshallUserResume(String resume) {
        Yaml yaml = new Yaml(new Constructor(Resume.class, new LoaderOptions()));
        return yaml.load(resume);
    }

    @Override
    public void deleteResume(long id) {
        resumeDetailsRepository.deleteById(id);
    }
}
