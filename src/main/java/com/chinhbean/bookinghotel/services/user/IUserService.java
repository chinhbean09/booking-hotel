package com.chinhbean.bookinghotel.services.user;

import com.chinhbean.bookinghotel.dtos.ChangePasswordDTO;
import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.dtos.UserLoginDTO;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import com.chinhbean.bookinghotel.responses.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {

    User registerUser(UserDTO userDTO) throws Exception;

    String login(UserLoginDTO userLoginDTO) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUserAvatar(long id, MultipartFile avatar);

    void sendMailForRegisterSuccess(String email, String password, long userId);

    void changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws DataNotFoundException;

    void updatePassword(String email, String password) throws DataNotFoundException;

    void blockOrEnable(Long userId, Boolean active) throws Exception;

    Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest);

    User getUser(Long id) throws DataNotFoundException;

    void deleteUser(Long userId);

    void updateUser(UserDTO userDTO) throws Exception;

    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    List<UserResponse> getAllUsers(Long roleId);
}
