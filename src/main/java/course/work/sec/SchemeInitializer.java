package course.work.sec;

import course.work.dao.UserRepository;
import course.work.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Заполняет бд необходимыми для работы данными
 */
@Configuration
public class SchemeInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SchemeInitializer(UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        User commonUser = new User();
        commonUser.setLogin("user");
        commonUser.setUserName("Дима");
        commonUser.setPassword(passwordEncoder.encode("user"));
        userRepository.save(commonUser);
    }
}
