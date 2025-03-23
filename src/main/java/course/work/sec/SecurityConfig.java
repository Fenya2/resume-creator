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
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                LOGIN_PAGE_ENDPOINT,
                                "/register",
                                "/oauth2/**",
                                "/login/oauth2/code/google",
                                "/favicon.ico")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE_ENDPOINT)
                        .defaultSuccessUrl(ROOT_ENDPOINT, true)
                        .failureUrl(LOGIN_ERROR_ENDPOINT)
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage(LOGIN_PAGE_ENDPOINT)
                        .defaultSuccessUrl(ROOT_ENDPOINT, true)
                        .failureUrl(LOGIN_ERROR_ENDPOINT)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())
                .userDetailsService(userDetailsService)
                .csrf(Customizer.withDefaults())
                .build();
    }
}
