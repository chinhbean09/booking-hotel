package com.chinhbean.bookinghotel.services;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.dtos.DataMailDTO;
import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.entities.Role;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.InvalidParamException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.repositories.RoleRepository;
import com.chinhbean.bookinghotel.repositories.UserRepository;
import com.chinhbean.bookinghotel.services.sendmails.MailService;
import com.chinhbean.bookinghotel.utils.MailTemplate;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LocalizationUtils localizationUtils;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final AmazonS3 amazonS3;
    private final MailService mailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Override
    @Transactional
    public User registerUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.PHONE_NUMBER_ALREADY_EXISTS));
        }

        // Sử dụng roleId mặc định là 2 nếu không được truyền vào
        Long roleId = userDTO.getRoleId() != null ? userDTO.getRoleId() : 2L;
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

        // Check if the current user has permission to register users with the specified role
        if (role.getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("Not allowed to register for an Admin account");
        }

        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .gender(userDTO.getGender())
                .active(true)
                .city(userDTO.getCity())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);

        // Kiểm tra nếu có accountId, không yêu cầu password
        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        //send mail
        sendMailForRegisterSuccess(userDTO.getFullName(), userDTO.getEmail(), userDTO.getPassword());
        return userRepository.save(newUser);
    }


    @Override
    public String login(
            String phoneNumber,
            String password,
            Long roleId
    ) throws Exception {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }
        User existingUser = optionalUser.get();
        if (existingUser.getFacebookAccountId() == 0
                && existingUser.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }
        if (!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,
                existingUser.getAuthorities()
        );

        // authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);
    }


    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new DataNotFoundException("Token is expired");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public User updateUserAvatar(long id, MultipartFile avatar) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && avatar != null && !avatar.isEmpty()) {
            try {
                // Check if the uploaded file is an image
                MediaType mediaType = MediaType.parseMediaType(Objects.requireNonNull(avatar.getContentType()));
                if (!mediaType.isCompatibleWith(MediaType.IMAGE_JPEG) &&
                        !mediaType.isCompatibleWith(MediaType.IMAGE_PNG)) {
                    throw new InvalidParamException(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // Get the original filename of the avatar
                String originalFileName = avatar.getOriginalFilename();
                // Construct the object key with the folder path and original filename
                String objectKey = "user_avatar/" + originalFileName;
                // Get the size of the file
                long contentLength = avatar.getSize();
                // Create object metadata and set the content length and content type
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(contentLength);
                metadata.setContentType(avatar.getContentType()); // Set the content type here
                // Upload the avatar to AWS S3 bucket
                amazonS3.putObject(bucketName, objectKey, avatar.getInputStream(), metadata);
                // Set the avatar URL in the user entity
                String avatarUrl = amazonS3.getUrl(bucketName, objectKey).toString();
                user.setAvatar(avatarUrl);
                // Save the updated user entity
                userRepository.save(user);
                return user;
            } catch (IOException e) {
                logger.error("Failed to upload avatar for user with ID " + id, e);
            }
        }
        return null;
    }


    @Override
    public void sendMailForRegisterSuccess(String name, String email, String password) {
        try {
            DataMailDTO dataMail = new DataMailDTO();
            dataMail.setTo(email);
            dataMail.setSubject(MailTemplate.SEND_MAIL_SUBJECT.USER_REGISTER);
            Map<String, Object> props = new HashMap<>();
            props.put("name", name);
            props.put("email", email);
            props.put("password", password);
            dataMail.setProps(props);

            mailService.sendHtmlMail(dataMail, MailTemplate.SEND_MAIL_TEMPLATE.USER_REGISTER);
        } catch (MessagingException exp) {
            exp.printStackTrace();
        }
    }
}
