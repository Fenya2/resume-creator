package course.work.service.auth;

import course.work.dao.UserRepository;
import course.work.model.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationServiceImpl(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void registerUser(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new UserAlreadyExistException(user);
        }
        String userPassword = user.getPassword();
        if (userPassword != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
        LOG.atInfo().log("User {} successfully registered", user.getLogin());
    }

    @Override
    public boolean isRegistered(String userLogin) {
        return userRepository.findByLogin(userLogin).isPresent();
    }
}
