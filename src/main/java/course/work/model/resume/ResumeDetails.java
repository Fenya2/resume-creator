package course.work.model.resume;

import course.work.model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tbl_resume_details")
public class ResumeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User owner;

    private String name;

    private Instant dateCreated;

    /**
     * Хранится в yaml
     */
    @Lob
    private String resume;

    public long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }
}
