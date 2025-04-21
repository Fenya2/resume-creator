package course.work.sec;

import course.work.model.User;
import course.work.service.auth.RegistrationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Заполняет бд необходимыми для работы данными
 */
@Configuration
public class SchemeInitializer {
    private final RegistrationService registrationService;

    @Autowired
    public SchemeInitializer(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostConstruct
    public void init() {
        User user = new User();
        user.setLogin("user");
        user.setPassword("user");
        user.setUserName("Дима");
        registrationService.registerUser(user);
    }
}
