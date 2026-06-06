package com.conx.server.global.security.userDetails;

import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.domain.User;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private CustomUserDetails(long id, String email, String password,
                              Collection<? extends GrantedAuthority> role, UserStatus status){
        super();
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    @Getter
    private long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> role;
    @Getter
    private UserStatus status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getUserEmail(){
        return email;
    }

    public static CustomUserDetails of(User user){
        Collection<? extends GrantedAuthority> role =
                List.of(
                        new SimpleGrantedAuthority(user.getRole().getRole())
                );

        System.out.println("Role in CustomUserDetails >>> " + user.getRole());

        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), role, user.getStatus());
    }
}
