package com.chinhbean.bookinghotel.responses.user;

import com.chinhbean.bookinghotel.responses.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class UserListResponse {
    private List<UserResponse> users;
    private int totalPages;
    private String message;

}
