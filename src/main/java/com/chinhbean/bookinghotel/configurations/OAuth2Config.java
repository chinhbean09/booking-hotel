package com.chinhbean.bookinghotel.configurations;

import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.services.oauth2.IOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {
    private final IOAuth2Service oAuth2Service;

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return new CustomOAuth2UserService(oAuth2Service);
    }

    public static class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        private final IOAuth2Service oAuth2Service;

        public CustomOAuth2UserService(IOAuth2Service oAuth2Service) {
            this.oAuth2Service = oAuth2Service;
        }

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String googleId = oauth2User.getAttribute("sub");
            User user = oAuth2Service.processGoogleUser(email, name, googleId);
            return new OAuth2UserDetails(user, oauth2User.getAttributes());
        }
    }
}