package course.work.sec;

import course.work.dao.UserRepository;
import course.work.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final String USER_NOT_FOUND = "user with login <%s> not found";

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND.formatted(username)));
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                Objects.requireNonNullElseGet(user.getPassword(), () -> "NO_PASSWORD"),
                // TODO роли пользователя можно добавить позже
                List.of()
        );
    }
}
