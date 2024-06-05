package com.chinhbean.bookinghotel;

import com.chinhbean.bookinghotel.entities.Role;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.repositories.RoleRepository;
import com.chinhbean.bookinghotel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
@Component
public class BookingHotelApplicationRunner implements ApplicationRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${bookinghotel.admin.email}")
    private String email;

    @Value("${bookinghotel.admin.fullName}")
    private String fullName;

    @Value("${bookinghotel.admin.address}")
    private String address;

    @Value("${bookinghotel.admin.phoneNumber}")
    private String phoneNumber;

    @Value("${bookinghotel.admin.gender}")
    private String gender;

    @Value("${bookinghotel.admin.password}")
    private String password;

    @Value("${bookinghotel.admin.active}")
    private Boolean active;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<User> findAccountResult = userRepository.findByPhoneNumber(phoneNumber);
        Optional<Role> existRolePermission = roleRepository.findById((long) 1);


        Role AdminRole = Role.builder()
                .id(1L)
                .roleName("ADMIN")
                .build();
        Role ParterRole = Role.builder()
                .id(2L)
                .roleName("PARTNER")
                .build();
        Role CustomerRole = Role.builder()
                .id(3L)
                .roleName("CUSTOMER")
                .build();

        if (existRolePermission.isEmpty()) {
            System.out.println("There is no role Initialing...!");

        }

        roleRepository.save(AdminRole);
        roleRepository.save(ParterRole);
        roleRepository.save(CustomerRole);

        if (findAccountResult.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setEmail(email);
            user.setGender(gender);
            user.setAddress(address);
            user.setPassword(encodedPassword);
            user.setActive(active);
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setRole(AdminRole);
            user.setActive(true);
            user.setDateOfBirth(new Date());
            userRepository.save(user);
            System.out.println("Admin initialized!");
        }

        System.out.println("Hello There I'm System Manager!");
    }
}
