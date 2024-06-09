package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.ChangePasswordDTO;
import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.exceptions.DataNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    User registerUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUserAvatar(long id, MultipartFile avatar);

    void sendMailForRegisterSuccess(String name, String email, String password);

    User changePassword(Long id, ChangePasswordDTO changePasswordDTO) throws DataNotFoundException;

    void updatePassword(String email, String password) throws DataNotFoundException;

}
