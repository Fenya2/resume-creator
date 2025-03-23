package course.work.sec;

import course.work.model.User;
import course.work.service.auth.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final RegistrationService registrationService;


    @Autowired
    public CustomOAuth2UserService(RegistrationService registrationService,
                                   UserDetailsService userDetailsService) {
        this.registrationService = registrationService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2User oauthUser = super.loadUser(userRequest);

        String userLogin = getOauth2UserLogin(provider, oauthUser.getAttribute("sub"));
        if (!registrationService.isRegistered(userLogin)) {
            User user = extractUser(userLogin, oauthUser);
            registrationService.registerUser(user);
        }

        return oauthUser;
    }

    public static String getOauth2UserLogin(String provider, String userSub) {
        return provider + ":" + userSub;
    }

    private static User extractUser(String userLogin, OAuth2User oAuth2User) {
        User user = new User();
        user.setLogin(userLogin);
        user.setUserName(oAuth2User.getAttribute("given_name"));
        return user;
    }
}
