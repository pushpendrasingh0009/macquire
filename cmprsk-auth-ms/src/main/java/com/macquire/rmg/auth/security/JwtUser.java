package com.macquire.rmg.auth.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JwtUser implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private final Long id;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    
    public JwtUser(
          Long id,
          String username,
          Collection<? extends GrantedAuthority> authorities,
          boolean enabled
    ) {
        this.id = id;
        this.username = username;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

}
