package course.work.service;

import course.work.model.User;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(User user) {
        super(user.getLogin());
    }
}
