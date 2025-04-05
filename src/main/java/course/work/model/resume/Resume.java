package course.work.model.resume;

import course.work.model.User;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    private User owner;

    /**
     * Хранится в yaml
     */
    @Lob
    private String userResume;
}
