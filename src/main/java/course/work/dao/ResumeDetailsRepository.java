package course.work.dao;

import course.work.model.User;
import course.work.model.resume.ResumeDetails;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResumeDetailsRepository extends CrudRepository<ResumeDetails, Long> {
    List<ResumeDetails> findByOwnerOrderByDateCreatedDesc(User owner);
    boolean existsByOwnerAndName(User user, String detailsName);
}
