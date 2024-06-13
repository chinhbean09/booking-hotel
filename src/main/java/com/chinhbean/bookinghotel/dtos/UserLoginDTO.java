package com.chinhbean.bookinghotel.dtos;

import com.chinhbean.bookinghotel.validators.EmailOrPhone;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
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
    @JsonProperty("email_or_phone")
    @NotBlank(message = "Email or phone number is required")
    @EmailOrPhone(message = "If an email is provided, it must be valid")
    private String emailOrPhone;

    @NotBlank(message = "Password cannot be blank")
    private String password;

}
