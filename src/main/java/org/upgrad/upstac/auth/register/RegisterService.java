package org.upgrad.upstac.auth.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.UserService;
import org.upgrad.upstac.users.models.AccountStatus;
import org.upgrad.upstac.users.roles.UserRole;

import java.time.LocalDateTime;

import static org.upgrad.upstac.shared.DateParser.getDateFromString;
import static org.upgrad.upstac.users.models.AccountStatus.APPROVED;
import static org.upgrad.upstac.users.models.AccountStatus.INITIATED;
import static org.upgrad.upstac.users.roles.UserRole.*;


@Service
public class RegisterService {

    @Autowired
    private UserService userService;


    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);


    public User addUser(RegisterRequest user) {


        if((null != userService.findByUserName(user.getUserName())))
            throw new AppException("Username already exists " + user.getUserName());

        if((null != userService.findByEmail(user.getEmail())))
            throw new AppException("User with Same email already exists " + user.getEmail());


        if((null != userService.findByPhoneNumber(user.getPhoneNumber())))
            throw new AppException("User with Same Phone number already exists " + user.getPhoneNumber());

        return updateUserDetails(user,USER, APPROVED);
    }

    public User addDoctor(RegisterRequest user) {

        if((null != userService.findByUserName(user.getUserName())))
            throw new AppException("Username already exists " + user.getUserName());

        if((null != userService.findByEmail(user.getEmail())))
            throw new AppException("User with Same email already exists " + user.getEmail());


        if((null != userService.findByPhoneNumber(user.getPhoneNumber())))
            throw new AppException("User with Same Phone number already exists " + user.getPhoneNumber());

        return updateUserDetails(user,DOCTOR, INITIATED);
    }

    private User updateUserDetails(RegisterRequest user,UserRole userRole , AccountStatus accountStatus){
        final User newUser = prepareNewUser(user, userRole,accountStatus);
        return userService.saveInDatabase(newUser);
    }

    private User prepareNewUser(RegisterRequest user,UserRole userRole , AccountStatus accountStatus) {
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setPassword(userService.toEncrypted(user.getPassword()));
        newUser.setRoles(userService.getRoleFor(userRole));
        newUser.setCreated(LocalDateTime.now());
        newUser.setUpdated(LocalDateTime.now());
        newUser.setAddress(user.getAddress());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setPinCode(user.getPinCode());
        newUser.setGender(user.getGender());
        newUser.setAddress(user.getAddress());
        newUser.setDateOfBirth(getDateFromString(user.getDateOfBirth()));
        newUser.setStatus(accountStatus);
        return newUser;
    }

    public User addGovernmentAuthority(RegisterRequest user) {

        if((null != userService.findByUserName(user.getUserName())))
            throw new AppException("Username already exists " + user.getUserName());

        if((null != userService.findByEmail(user.getEmail())))
            throw new AppException("User with Same email already exists " + user.getEmail());


        if((null != userService.findByPhoneNumber(user.getPhoneNumber())))
            throw new AppException("User with Same Phone number already exists " + user.getPhoneNumber());

        return updateUserDetails(user,GOVERNMENT_AUTHORITY, APPROVED);
    }

    public User addTester(RegisterRequest user) {

        if((null != userService.findByUserName(user.getUserName())))
            throw new AppException("Username already exists " + user.getUserName());

        if((null != userService.findByEmail(user.getEmail())))
            throw new AppException("User with Same email already exists " + user.getEmail());


        if((null != userService.findByPhoneNumber(user.getPhoneNumber())))
            throw new AppException("User with Same Phone number already exists " + user.getPhoneNumber());

        return updateUserDetails(user,TESTER, INITIATED);
    }

}