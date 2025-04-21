package course.work.service.resume;

import course.work.model.User;
import course.work.model.resume.Resume;
import course.work.model.resume.ResumeDetails;

import java.util.List;

public interface ResumeService {
    ResumeDetails createFor(User user);

    ResumeDetails getDetails(long id);

    Resume getResume(ResumeDetails details);

    List<ResumeDetails> getUserResumesDetails(User user);

    void updateUserResumeDetails(User user, String detailsName, long resumeDetailsId) throws ResumeServiceException;

    void checkResumeOwnsUser(ResumeDetails resumeDetails, User user);
}
