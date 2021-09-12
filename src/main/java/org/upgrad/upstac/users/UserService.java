package org.upgrad.upstac.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.auth.register.RegisterRequest;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.users.models.AccountStatus;
import org.upgrad.upstac.users.models.UpdateUserDetailRequest;
import org.upgrad.upstac.users.roles.Role;
import org.upgrad.upstac.users.roles.RoleService;
import org.upgrad.upstac.users.roles.UserRole;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

import static org.upgrad.upstac.shared.Constant.*;
import static org.upgrad.upstac.shared.DateParser.getDateFromString;
import static org.upgrad.upstac.shared.StringValidator.isNotEmptyOrNull;
import static org.upgrad.upstac.users.models.AccountStatus.APPROVED;
import static org.upgrad.upstac.users.models.AccountStatus.INITIATED;


@Service
@Validated
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    RoleService roleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Cacheable("user")
    public User findByUserName(String userName) {

        return userRepository.findByUserName(userName);

    }

    public List<User> findPendingApprovals() {
        return userRepository.findByStatus(INITIATED);
    }

    public boolean isApprovedUser(String userName) {
        return userRepository.findByUserName(userName).getStatus() == APPROVED;
    }

    public void validateUserWithSameDataExists(RegisterRequest user) {

        if ((null != findByUserName(user.getUserName())))
            throw new AppException(USERNAME_ALREADY_EXISTS + user.getUserName());

        userRepository.findByEmail(user.getEmail()).ifPresent(user1 -> {
            throw new AppException(USER_WITH_SAME_EMAIL_ALREADY_EXISTS + user.getEmail());
        });

        userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(user1 -> {
            throw new AppException(USER_WITH_SAME_PHONE_NUMBER_ALREADY_EXISTS + user.getPhoneNumber());
        });
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User addUser(RegisterRequest user) {
        return addUserWithRole(user, roleService.getForUser(), APPROVED);
    }

    public User addDoctor(RegisterRequest user) {
        return addUserWithRole(user, roleService.getForDoctor(), INITIATED);
    }

    public User addGovernmentAuthority(RegisterRequest user) {
        return addUserWithRole(user, roleService.getForGovernmentAuthority(), APPROVED);
    }

    public User addTester(RegisterRequest user) {
        return addUserWithRole(user, roleService.getForTester(), INITIATED);
    }

    public User addUserWithRole(@Valid RegisterRequest registerRequest, Role role, AccountStatus status) {
        validateUserWithSameDataExists(registerRequest);
        User newUser = new User();
        newUser.setUserName(registerRequest.getUserName());
        newUser.setPassword(toEncrypted(registerRequest.getPassword()));
        newUser.setRoles(getRolesForUser(role));
        newUser.setCreated(LocalDateTime.now());
        newUser.setUpdated(LocalDateTime.now());
        newUser.setAddress(registerRequest.getAddress());
        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber());
        newUser.setPinCode(registerRequest.getPinCode());
        newUser.setGender(registerRequest.getGender());
        newUser.setAddress(registerRequest.getAddress());
        newUser.setDateOfBirth(getDateFromString(registerRequest.getDateOfBirth()));
        newUser.setStatus(status);
        User updatedUser = saveInDatabase(newUser);
        return updatedUser;
    }

    @CachePut(value = "user")
    public User updateApprovalStatus(Long userId, AccountStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException("Invalid User ID"));
        return updateStatusAndSave(user, status);
    }

    public User updateStatusAndSave(User user, @NotNull AccountStatus status) {
        user.setStatus(status);
        return saveInDatabase(user);
    }

    @CachePut(value = "user")
    public User saveInDatabase(User newUser) {
        try {
            return userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new AppException("User with same data Already exists, Email/Phone should be unique");
        }
    }

    public User updateUserDetails(User user, UpdateUserDetailRequest updateUserDetailRequest) {
        if (isNotEmptyOrNull(updateUserDetailRequest.getFirstName()))
            user.setFirstName(updateUserDetailRequest.getFirstName());

        if (isNotEmptyOrNull(updateUserDetailRequest.getLastName()))
            user.setLastName(updateUserDetailRequest.getLastName());

        if (isNotEmptyOrNull(updateUserDetailRequest.getEmail()))
            user.setEmail(updateUserDetailRequest.getEmail());

        if (isNotEmptyOrNull(updateUserDetailRequest.getPhoneNumber()))
            user.setPhoneNumber(updateUserDetailRequest.getPhoneNumber());
        final User savedUser = saveInDatabase(user);
        log.info("updateUserDetails" + savedUser.toString());
        return savedUser;
    }

    public Set<Role> getRoleFor(UserRole userRole) {
        return getRolesForUser(roleService.findByRole(userRole));
    }

    private Set<Role> getRolesForUser(Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        return roles;
    }

    public String toEncrypted(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByPhoneNumber(String email) {
        return userRepository.findByPhoneNumber(email).orElse(null);
    }
}