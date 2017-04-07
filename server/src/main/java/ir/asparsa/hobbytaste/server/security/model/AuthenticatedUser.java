package ir.asparsa.hobbytaste.server.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Holds the info for a authenticated user (Principal)
 *
 * @author pascal alma
 */
public class AuthenticatedUser implements UserDetails {

    private final Long id;
    private final String username;
    private final AccountModel account;
    private final String token;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthenticatedUser(
            Long id,
            String username,
            AccountModel account,
            String token,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.account = account;
        this.token = token;
        this.authorities = authorities;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public String getToken() {
        return token;
    }

    public AccountModel getAccount() {
        return account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

}
