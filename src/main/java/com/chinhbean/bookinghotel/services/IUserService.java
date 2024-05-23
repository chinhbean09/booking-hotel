package com.chinhbean.bookinghotel.services;

import com.chinhbean.bookinghotel.dtos.UserDTO;
import com.chinhbean.bookinghotel.entities.User;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    User registerUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUserAvatar(long id, MultipartFile avatar);

    Boolean sendMailForRegisterSuccess(String name, String email, String password);

}
