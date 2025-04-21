package course.work.model.resume;

import java.util.Date;
import java.util.List;

public class Resume {
    private GeneralInfo generalInfo;

    private AdditionalInfo additionalInfo;

    private ContactInfo contactInfo;

    private String about;

    public GeneralInfo getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(GeneralInfo generalInfo) {
        this.generalInfo = generalInfo;
    }

    public AdditionalInfo getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(AdditionalInfo additionalInfo) {
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

    public static class GeneralInfo {
        private String firstName;

        private String lastName;

        private Date birthday;

        private String city;

        private int requiredSalary;

        private String photoId;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getRequiredSalary() {
            return requiredSalary;
        }

        public void setRequiredSalary(int requiredSalary) {
            this.requiredSalary = requiredSalary;
        }

        public String getPhotoId() {
            return photoId;
        }

        public void setPhotoId(String photoId) {
            this.photoId = photoId;
        }
    }

    public static class AdditionalInfo {
        private boolean hasDriverLicence;

        private boolean hasCar;

        private boolean hasChildren;

        public boolean isHasDriverLicence() {
            return hasDriverLicence;
        }

        public void setHasDriverLicence(boolean hasDriverLicence) {
            this.hasDriverLicence = hasDriverLicence;
        }

        public boolean isHasCar() {
            return hasCar;
        }

        public void setHasCar(boolean hasCar) {
            this.hasCar = hasCar;
        }

        public boolean isHasChildren() {
            return hasChildren;
        }

        public void setHasChildren(boolean hasChildren) {
            this.hasChildren = hasChildren;
        }
    }

    public static class ContactInfo {
        private String phone;
        private String email;
        private List<SocialNetwork> socialNetworks;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<SocialNetwork> getSocialNetworks() {
            return socialNetworks;
        }

        public void setSocialNetworks(List<SocialNetwork> socialNetworks) {
            this.socialNetworks = socialNetworks;
        }
    }
}
