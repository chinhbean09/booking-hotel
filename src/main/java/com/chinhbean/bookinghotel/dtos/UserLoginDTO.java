package com.chinhbean.bookinghotel.dtos;

import com.chinhbean.bookinghotel.validators.EmailOrPhone;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @JsonProperty("login_identifier")
    @NotBlank(message = "Login identifier cannot be blank")
    @EmailOrPhone(message = "Login identifier must be a valid email or phone number")
    private String loginIdentifier;

    @NotBlank(message = "Password cannot be blank")
    private String password;

}
