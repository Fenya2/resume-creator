package course.work.model.resume;

import java.util.Date;

public class ResumeGeneralInfo {
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
