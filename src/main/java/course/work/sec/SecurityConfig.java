package course.work.sec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final String LOGIN_PAGE_ENDPOINT = "/login";
    public static final String ROOT_ENDPOINT = "/";

    public static final String LOGIN_ERROR_ENDPOINT = "/login?error=true";

    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    public SecurityConfig(
            UserDetailsService userDetailsService,
            CustomOAuth2UserService customOAuth2UserService
    ) {
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login", // Страница логина
                                "/register", // Страница регистрации
                                "/oauth2/**", // Пути для OAuth2
                                "/login/oauth2/code/google", // Google callback URL
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE_ENDPOINT)
                        .defaultSuccessUrl(ROOT_ENDPOINT)
                        .failureUrl(LOGIN_ERROR_ENDPOINT)
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth  // Добавляем OAuth2 конфигурацию
                        .loginPage(LOGIN_PAGE_ENDPOINT)
                        .defaultSuccessUrl(ROOT_ENDPOINT)  // URL после успешной авторизации
                        .failureUrl(LOGIN_ERROR_ENDPOINT)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .userDetailsService(userDetailsService)
                .csrf(Customizer.withDefaults()); // Включаем CSRF защиту
        return http.build();
    }
}
