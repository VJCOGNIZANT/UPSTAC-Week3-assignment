package org.upgrad.upstac.config.security;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.UserService;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.slf4j.LoggerFactory.getLogger;


@Service(value = "UpgradUserDetailsService")
public class UpgradUserDetailsService implements UserDetailsService {

    private static final Logger log = getLogger(UpgradUserDetailsService.class);
    private final UserService userService;

    @Autowired
    public UpgradUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUserName(username);
        log.info("loadUserByUsername " + user.toString());
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {

        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(toSet());

    }

}
