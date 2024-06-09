package com.chinhbean.bookinghotel.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordDTO {
    @JsonProperty("old_password")
    private String oldPassword;
    @JsonProperty("new_password")
    private String newPassword;
    @JsonProperty("confirm_password")
    private String confirmPassword;
}
