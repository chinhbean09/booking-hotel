package com.chinhbean.bookinghotel.services.user;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.dtos.ChangePasswordDTO;
import com.chinhbean.bookinghotel.dtos.DataMailDTO;
import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.dtos.UserLoginDTO;
import com.chinhbean.bookinghotel.entities.Hotel;
import com.chinhbean.bookinghotel.entities.Role;
import com.chinhbean.bookinghotel.entities.Token;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.enums.HotelStatus;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.exceptions.InvalidParamException;
import com.chinhbean.bookinghotel.exceptions.PermissionDenyException;
import com.chinhbean.bookinghotel.repositories.IHotelRepository;
import com.chinhbean.bookinghotel.repositories.IRoleRepository;
import com.chinhbean.bookinghotel.repositories.ITokenRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
import com.chinhbean.bookinghotel.responses.user.UserResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor

public class UserService implements IUserService {
    private final IUserRepository IUserRepository;
    private final IRoleRepository IRoleRepository;
    private final LocalizationUtils localizationUtils;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final IHotelRepository hotelRepository;
    private final AmazonS3 amazonS3;
    private final MailService mailService;
    private final ITokenRepository ITokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Override
    @Transactional
    public User registerUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
        if (IUserRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(localizationUtils.getLocalizedMessage(MessageKeys.PHONE_NUMBER_ALREADY_EXISTS));
        }

        // Sử dụng roleId mặc định là 2 nếu không được truyền vào
        Long roleId = userDTO.getRoleId() != null ? userDTO.getRoleId() : 3L;
        Role role = IRoleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

        // Check if the current user has permission to register users with the specified role
        if (role.getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new PermissionDenyException("Not allowed to register for an Admin account");
        }

        User newUser = User.builder()
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .fullName(userDTO.getFullName())
                .active(true)
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        newUser.setRole(role);

        // Kiểm tra nếu có accountId, không yêu cầu password
        if (userDTO.getFacebookAccountId() == null) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        User user = IUserRepository.save(newUser);
        //send mail
        sendMailForRegisterSuccess(userDTO.getEmail(), userDTO.getPassword(), user.getId());
        return user;
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser;
//        String subject = null;
        String subject = userLoginDTO.getLoginIdentifier();

        if (subject.contains("@")) {
            optionalUser = IUserRepository.findByEmail(subject);
        } else {
            optionalUser = IUserRepository.findByPhoneNumber(subject);
        }

        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND));
        }
        User existingUser = optionalUser.get();

        if (existingUser.getFacebookAccountId() == null && existingUser.getGoogleAccountId() == null) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }
        if (!existingUser.isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginDTO.getPassword(),
                existingUser.getAuthorities()
        );

        // authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);
    }

    @Override
    public Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest) {
        Page<User> usersPage;
        usersPage = IUserRepository.searchUsers(keyword, pageRequest);
        return usersPage.map(UserResponse::fromUser);
    }

    @Override
    public User getUser(Long id) throws DataNotFoundException {
        return IUserRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new DataNotFoundException("Token is expired");
        }
        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
        Optional<User> user = IUserRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = IUserRepository.findById(userId);
        List<Token> tokens = ITokenRepository.findByUserId(userId);
        ITokenRepository.deleteAll(tokens);
        optionalUser.ifPresent(IUserRepository::delete);
    }

    @Override
    public User updateUserAvatar(long id, MultipartFile avatar) {
        User user = IUserRepository.findById(id).orElse(null);
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
                String objectKey = "user_avatar/" + id + "/" + originalFileName;
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
                IUserRepository.save(user);
                return user;
            } catch (IOException e) {
                logger.error("Failed to upload avatar for user with ID {}", id, e);
            }
        }
        return null;
    }


    @Override
    public void sendMailForRegisterSuccess(String email, String password, long userId) {
        try {
            DataMailDTO dataMail = new DataMailDTO();
            dataMail.setTo(email);
            dataMail.setSubject(MailTemplate.SEND_MAIL_SUBJECT.USER_REGISTER);

            Map<String, Object> props = new HashMap<>();
            props.put("email", email);
            props.put("password", password);
            props.put("userId", userId); // Add userId to props

            dataMail.setProps(props);

            mailService.sendHtmlMail(dataMail, MailTemplate.SEND_MAIL_TEMPLATE.USER_REGISTER);
        } catch (MessagingException exp) {
            logger.error("Failed to send registration success email", exp);
        }
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws DataNotFoundException {
        User exsistingUser = IUserRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), exsistingUser.getPassword())) {
            throw new DataNotFoundException(MessageKeys.OLD_PASSWORD_WRONG);
        }
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new DataNotFoundException(MessageKeys.CONFIRM_PASSWORD_NOT_MATCH);
        }
        exsistingUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        IUserRepository.save(exsistingUser);
    }

    @Override
    public void updatePassword(String email, String password) throws DataNotFoundException {
        User user = IUserRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        user.setPassword(passwordEncoder.encode(password));
        IUserRepository.save(user);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = IUserRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(MessageKeys.USER_NOT_FOUND));
        existingUser.setActive(active);
        IUserRepository.save(existingUser);
        if (!active && existingUser.getRole().getRoleName().equals(Role.PARTNER)) {
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
            List<Hotel> hotels = hotelRepository.findHotelsByPartnerId(userId, pageable).getContent();
            for (Hotel hotel : hotels) {
                hotel.setStatus(HotelStatus.CLOSED);
                hotelRepository.save(hotel);
            }
        }
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        currentUser.setEmail(userDTO.getEmail());
        currentUser.setFullName(userDTO.getFullName());
        currentUser.setPhoneNumber(userDTO.getPhoneNumber());
        currentUser.setAddress(userDTO.getAddress());
        currentUser.setDateOfBirth(userDTO.getDateOfBirth());
        currentUser.setGender(userDTO.getGender());
        IUserRepository.save(currentUser);
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = ITokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public List<UserResponse> getAllUsers(Long roleId) {
        List<User> users = IUserRepository.findByRoleId(roleId);
        return users.stream().map(UserResponse::fromUser).toList();
    }

}
