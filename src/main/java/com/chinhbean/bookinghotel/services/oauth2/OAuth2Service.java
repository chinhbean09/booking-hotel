package com.chinhbean.bookinghotel.services.oauth2;

import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.dtos.DataMailDTO;
import com.chinhbean.bookinghotel.entities.Role;
import com.chinhbean.bookinghotel.entities.Token;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.repositories.IRoleRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
import com.chinhbean.bookinghotel.responses.user.LoginResponse;
import com.chinhbean.bookinghotel.services.sendmails.MailService;
import com.chinhbean.bookinghotel.services.token.ITokenService;
import com.chinhbean.bookinghotel.utils.MailTemplate;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2Service implements IOAuth2Service {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtTokenUtils jwtTokenUtils;
    private final ITokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(OAuth2Service.class);

    @Transactional
    @Override
    public LoginResponse handleFacebookLogin(String accessToken, HttpServletRequest request) throws Exception {
        String facebookGraphApiUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                facebookGraphApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> userAttributes = response.getBody();

        if (userAttributes != null && userAttributes.containsKey("id")) {
            String facebookId = (String) userAttributes.get("id");
            String email = (String) userAttributes.get("email");
            String name = (String) userAttributes.get("name");

            User user = processFacebookUser(email, name, facebookId);

            return generateLoginResponse(user, request, "Facebook login successful");
        } else {
            throw new Exception("Invalid Facebook access token");
        }
    }

    @Transactional
    @Override
    public LoginResponse handleGoogleLogin(String accessToken, HttpServletRequest request) throws Exception {
        try {
            final String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    googleUserInfoEndpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            Map<String, Object> userAttributes = response.getBody();

            if (userAttributes != null && userAttributes.containsKey("sub")) { // "sub" is the user ID in Google's response
                String googleId = (String) userAttributes.get("sub");
                String email = (String) userAttributes.get("email");
                String name = (String) userAttributes.get("name");

                User user = processGoogleUser(email, name, googleId);

                return generateLoginResponse(user, request, "Google login successful");
            } else {
                throw new Exception("Invalid Google access token");
            }
        } catch (Exception e) {
            logger.error("Google login error", e);
            throw new Exception("Google login failed: " + e.getMessage());
        }
    }

    public User processFacebookUser(String email, String name, String facebookId) {
        return userRepository.findByEmail(email)
                .map(this::updateFacebookUser)
                .orElseGet(() -> createOAuth2User(email, name, facebookId, false));
    }

    public User processGoogleUser(String email, String name, String googleId) {
        return userRepository.findByEmail(email)
                .map(this::updateGoogleUser)
                .orElseGet(() -> createOAuth2User(email, name, googleId, true));
    }

    private User updateFacebookUser(User user) {
        if (user.getFacebookAccountId() == null) {
            user.setFacebookAccountId(user.getEmail());
            return userRepository.save(user);
        }
        return user;
    }

    private User updateGoogleUser(User user) {
        if (user.getGoogleAccountId() == null) {
            user.setGoogleAccountId(user.getEmail());
            return userRepository.save(user);
        }
        return user;
    }

    private User createOAuth2User(String email, String name, String accountId, boolean isGoogle) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        User newUser = User.builder()
                .email(email)
                .fullName(name)
                .password(passwordEncoder.encode(randomPassword))
                .active(true)
                .role(roleRepository.findByRoleName(Role.CUSTOMER))
                .build();

        if (isGoogle) {
            newUser.setGoogleAccountId(accountId);
        } else {
            newUser.setFacebookAccountId(accountId);
        }

        User savedUser = userRepository.save(newUser);
        sendPasswordEmail(email, randomPassword, name);
        return savedUser;
    }

    private LoginResponse generateLoginResponse(User user, HttpServletRequest request, String message) {
        String userAgent = request.getHeader("User-Agent");
        String jwtToken = jwtTokenUtils.generateToken(user);
        Token savedToken = tokenService.addToken(user, jwtToken, isMobileDevice(userAgent));

        return LoginResponse.builder()
                .message(message)
                .token(savedToken.getToken())
                .tokenType(savedToken.getTokenType())
                .refreshToken(savedToken.getRefreshToken())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(user.getId())
                .build();
    }

    private boolean isMobileDevice(String userAgent) {
        return userAgent != null && userAgent.toLowerCase().contains("mobile");
    }

    private void sendPasswordEmail(String email, String password, String name) {
        Map<String, Object> props = new HashMap<>();
        props.put("email", email);
        props.put("password", password);
        props.put("name", name);

        DataMailDTO mailData = new DataMailDTO(email, MailTemplate.SEND_MAIL_SUBJECT.NEW_PASSWORD, "", props);

        try {
            mailService.sendHtmlMail(mailData, MailTemplate.SEND_MAIL_TEMPLATE.NEW_PASSWORD);
        } catch (MessagingException e) {
            logger.error("Error sending new password", e);
        }
    }
}
