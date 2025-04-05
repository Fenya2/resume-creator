package course.work.model.resume;

public class UserResume {
    private ResumeGeneralInfo generalInfo;

    private ResumeAdditionalInfo additionalInfo;

    private ContactInfo contactInfo;

    private String about;

    public ResumeGeneralInfo getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(ResumeGeneralInfo generalInfo) {
        this.generalInfo = generalInfo;
    }

    public ResumeAdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(ResumeAdditionalInfo additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
