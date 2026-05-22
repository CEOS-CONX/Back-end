package com.conx.server.global.security.userDetails;

import com.conx.server.user.domain.User;
import com.conx.server.user.service.UserFinder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserFinder userFinder;

    public CustomUserDetailsService(UserFinder userFinder) {
        this.userFinder = userFinder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userFinder.findByEmail(email);

        return CustomUserDetails.of(user);
    }
}
