package org.upgrad.upstac.users.credentials;


import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.exception.ForbiddenException;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.UserRepository;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;


@Service
@Validated
public class ChangePasswordService {

    private static final Logger LOG = getLogger(ChangePasswordService.class);
    private AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;


    @Autowired
    public ChangePasswordService(AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    public void changePassword(User user, @Valid ChangePasswordRequest changePasswordRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUserName(),
                            changePasswordRequest.getOldPassword()
                    )
            );
            final String changedPassword = changePasswordRequest.getPassword();
            user.setPassword(bCryptPasswordEncoder.encode(changedPassword));
            userRepository.save(user);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }
    }
}