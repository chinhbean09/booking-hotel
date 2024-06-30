package com.chinhbean.bookinghotel;

import com.chinhbean.bookinghotel.entities.Role;
import com.chinhbean.bookinghotel.entities.User;
import com.chinhbean.bookinghotel.repositories.IRoleRepository;
import com.chinhbean.bookinghotel.repositories.IUserRepository;
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
    private IUserRepository IUserRepository;

    @Autowired
    private IRoleRepository IRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${bookinghotel.admin.email}")
    private String email;

    @Value("${bookinghotel.guest.email}")
    private String guestEmail;

    @Value("${bookinghotel.admin.fullName}")
    private String fullName;

    @Value("${bookinghotel.guest.fullName}")
    private String guestFullName;

    @Value("${bookinghotel.admin.address}")
    private String address;

    @Value("${bookinghotel.guest.address}")
    private String guestAddress;

    @Value("${bookinghotel.admin.phoneNumber}")
    private String phoneNumber;

    @Value("${bookinghotel.guest.phoneNumber}")
    private String guestPhoneNumber;


    @Value("${bookinghotel.admin.gender}")
    private String gender;

    @Value("${bookinghotel.admin.password}")
    private String password;

    @Value("${bookinghotel.admin.active}")
    private Boolean active;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<User> findAccountResult = IUserRepository.findByPhoneNumber(phoneNumber);
        Optional<Role> existRolePermission = IRoleRepository.findById((long) 1);
        Optional<User> findAccountGuest = IUserRepository.findByPhoneNumber(guestPhoneNumber);


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

        IRoleRepository.save(AdminRole);
        IRoleRepository.save(ParterRole);
        IRoleRepository.save(CustomerRole);

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
            IUserRepository.save(user);
            System.out.println("Admin initialized!");
        }

        if (findAccountGuest.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);

            User user = new User();
            user.setEmail(guestEmail);
            user.setGender(gender);
            user.setAddress(guestAddress);
            user.setPassword(encodedPassword);
            user.setActive(active);
            user.setFullName(guestFullName);
            user.setPhoneNumber(guestPhoneNumber);
            user.setRole(CustomerRole);
            user.setActive(true);
            user.setDateOfBirth(new Date());
            IUserRepository.save(user);
            System.out.println("Guest initialized!");
        }

        System.out.println("Hello, I'm System Manager!");
    }
}
