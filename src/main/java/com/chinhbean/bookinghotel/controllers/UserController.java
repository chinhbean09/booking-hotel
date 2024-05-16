package com.chinhbean.bookinghotel.controllers;


import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.RegisterResponse;
import com.chinhbean.bookinghotel.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor

public class UserController {
    private final IUserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ) throws DataNotFoundException {

        RegisterResponse registerResponse = new RegisterResponse();

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            registerResponse.setMessage(errorMessages.toString());
            return ResponseEntity.badRequest().body(registerResponse.getMessage());
        }
        try {
            User user = userService.registerUser(userDTO);
            registerResponse.setMessage("Đăng ký tài khoản thành công");
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse.getMessage());
        } catch (Exception e) {
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
