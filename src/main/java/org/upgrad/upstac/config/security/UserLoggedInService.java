package org.upgrad.upstac.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.UserService;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;


@Component
public class UserLoggedInService {

    private final UserService userService;

    @Autowired
    public UserLoggedInService(UserService userService) {
        this.userService = userService;
    }

    /**
     * get logged in user
     *
     * @return {@link User}
     * @see User
     */
    public User getLoggedInUser() {
        UserDetails principal = (UserDetails) getContext().getAuthentication().getPrincipal();
        return userService.findByUserName(principal.getUsername());
    }

}