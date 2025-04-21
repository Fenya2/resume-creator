package course.work.service.resume;

import course.work.dao.ResumeDetailsRepository;
import course.work.model.User;
import course.work.model.resume.Resume;
import course.work.model.resume.ResumeDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import java.time.Instant;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {
    private static final Logger LOG = LoggerFactory.getLogger(ResumeServiceImpl.class);

    private final ResumeDetailsRepository resumeDetailsRepository;

    @Autowired
    public ResumeServiceImpl(ResumeDetailsRepository resumeDetailsRepository) {
        this.resumeDetailsRepository = resumeDetailsRepository;
    }

    @Override
    public ResumeDetails createFor(User user) {
        ResumeDetails resumeDetails = new ResumeDetails();
        resumeDetails.setDateCreated(Instant.now());
        resumeDetails.setName(user.getUserName() + "_" + resumeDetails.getDateCreated());
        resumeDetails.setOwner(user);
        Resume emptyResume = createUserResumeFromTemplate();
        resumeDetails.setResume(marshalUserResume(emptyResume));
        resumeDetailsRepository.save(resumeDetails);
        LOG.atInfo().log("Created resume {} for user {}", resumeDetails.getId(), user.getLogin());
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
    public Resume getResume(ResumeDetails details) {
        return unmarshallUserResume(details.getResume());
    }

    private static Resume unmarshallUserResume(String resume) {
        Yaml yaml = new Yaml(new Constructor(Resume.class, new LoaderOptions()));
        return yaml.load(resume);
    }
}
