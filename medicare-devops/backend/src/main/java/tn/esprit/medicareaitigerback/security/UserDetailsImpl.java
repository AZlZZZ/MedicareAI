package tn.esprit.medicareaitigerback.security;

import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {

    private final Long id;

    private final String username;

    private UserType userType;

    @JsonIgnore
    private final String password;

    private Collection<GrantedAuthority> authorities = new ArrayList<>();

    public UserDetailsImpl(Long id, String username, String password, Collection<GrantedAuthority> authorities
            , UserType userType, UserState userState
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.userType = userType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserType getUserType() {
        return userType;
    }

    public Long getId() {
        return id;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
