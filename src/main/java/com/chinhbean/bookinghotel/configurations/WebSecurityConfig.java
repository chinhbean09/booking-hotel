package com.chinhbean.bookinghotel.configurations;

import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.dtos.DataMailDTO;
import com.chinhbean.bookinghotel.entities.Role;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.filters.JwtTokenFilter;
import com.chinhbean.bookinghotel.repositories.IRoleRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
import com.chinhbean.bookinghotel.services.sendmails.MailService;
import com.chinhbean.bookinghotel.utils.MailTemplate;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtTokenUtils jwtTokenUtils;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
        return userRequest -> {
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            String email = oauth2User.getAttribute("email");
            if (email == null) {
                throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
            }
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty()) {
                String randomPassword = generateRandomPassword();
                String googleAccountId = oauth2User.getAttribute("sub");
                if (googleAccountId == null) {
                    throw new OAuth2AuthenticationException("Google account ID not found from OAuth2 provider");
                }

                User newUser = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(randomPassword))
                        .fullName(oauth2User.getAttribute("name"))
                        .active(true)
                        .googleAccountId(googleAccountId)
                        .role(roleRepository.findByRoleName(Role.CUSTOMER))
                        .build();

                userRepository.save(newUser);
                sendPasswordEmail(email, randomPassword);
            }
            return oauth2User;
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/oauth2/**", "/login", "/login-error").permitAll()
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/users/generate-secret-key", apiPrefix),
                                String.format("%s/users/block-or-enable/**", apiPrefix),
                                String.format("%s/hotels/get-hotels", apiPrefix),
                                String.format("%s/hotels/detail/**", apiPrefix),
                                String.format("%s/hotels/filter", apiPrefix),
                                String.format("%s/hotels/search", apiPrefix),
                                String.format("%s/room-types/filter/**", apiPrefix),
                                String.format("%s/room-types/get-room/**", apiPrefix),
                                String.format("%s/room-types/get-all-room-status/**", apiPrefix),
                                String.format("%s/bookings/create-booking", apiPrefix),
                                String.format("%s/payment/**", apiPrefix)
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService()))
                        .successHandler((request, response, authentication) -> {
                            // Tạo JWT token
                            User user = (User) authentication.getPrincipal();
                            String token = jwtTokenUtils.generateToken(user);

                            // Tạo URL redirect với token
                            String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + token;
                            response.sendRedirect(redirectUrl);
                        })
                        .failureHandler((request, response, exception) -> {
                            String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                            response.sendRedirect("http://localhost:3000/login?error=" + errorMessage);
                        })
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "content-disposition"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void sendPasswordEmail(String email, String password) {
        Map<String, Object> props = new HashMap<>();
        props.put("password", password);

        DataMailDTO mailData = new DataMailDTO(email, MailTemplate.SEND_MAIL_SUBJECT.NEW_PASSWORD, "", props);

        try {
            mailService.sendHtmlMail(mailData, MailTemplate.SEND_MAIL_TEMPLATE.NEW_PASSWORD);
        } catch (MessagingException e) {
            logger.error("Error sending your new password", e);
        }
    }
}