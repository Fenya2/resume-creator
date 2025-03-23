package course.work.controller;

import course.work.sec.CustomOAuth2UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

class AuthenticationUtils {

    private AuthenticationUtils() {

    }

    public static String extractLoginFromAuthentication(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
            String provider = oAuth2Token.getAuthorizedClientRegistrationId();
            String sub = oAuth2Token.getPrincipal().getAttribute("sub");
            return CustomOAuth2UserService.getOauth2UserLogin(provider, sub);
        }
        if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            return usernamePasswordAuthenticationToken.getName();
        }
        throw new IllegalArgumentException("Unknown authorization");
    }
}
