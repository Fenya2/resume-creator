package course.work.service.auth;

import course.work.model.User;

public interface RegistrationService {
    void registerUser(User user);
    boolean isRegistered(String userLogin);
}
