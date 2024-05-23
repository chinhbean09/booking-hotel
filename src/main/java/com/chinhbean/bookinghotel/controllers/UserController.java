package com.chinhbean.bookinghotel.controllers;


import com.chinhbean.bookinghotel.components.JwtTokenUtils;
import com.chinhbean.bookinghotel.components.LocalizationUtils;
import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.dtos.UserLoginDTO;
import com.chinhbean.bookinghotel.entities.Token;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.responses.LoginResponse;
import com.chinhbean.bookinghotel.responses.ResponseObject;
import com.chinhbean.bookinghotel.responses.UserResponse;
import com.chinhbean.bookinghotel.services.ITokenService;
import com.chinhbean.bookinghotel.services.IUserService;
import com.chinhbean.bookinghotel.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")

public class UserController {
    private final IUserService userService;
    private final ITokenService tokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final LocalizationUtils localizationUtils;

    @GetMapping("/generate-secret-key")
    public ResponseEntity<?> generateSecretKey() {
        return ResponseEntity.ok(jwtTokenUtils.generateSecretKey());
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(errorMessages.toString())
                    .build());
        }
        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build());
        }
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(UserResponse.fromUser(user))
                .message(MessageKeys.REGISTER_SUCCESSFULLY)
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        String token = userService.login(
                userLoginDTO.getPhoneNumber(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 1 : userLoginDTO.getRoleId()
        );
        String userAgent = request.getHeader("User-Agent");

        User userDetail = userService.getUserDetailsFromToken(token);

        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .message(MessageKeys.LOGIN_SUCCESSFULLY)
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(MessageKeys.LOGIN_SUCCESSFULLY)
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }

    private boolean isMobileDevice(String userAgent) {
        return userAgent.toLowerCase().contains("mobile");
    }


}
