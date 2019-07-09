package com.macquire.rmg.auth.model;

import java.io.Serializable;
import java.util.Date;

import com.macquire.rmg.auth.security.JwtUser;

public class AuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;
    
    private JwtUser user;
    
    private Date tokenExpirationDate;

    public AuthenticationResponse(String token, Date tokenExpirationDate) {
		super();
		this.token = token;
		this.tokenExpirationDate = tokenExpirationDate;
	}

	public AuthenticationResponse(String token, JwtUser user, Date tokenExpirationDate) {
		super();
		this.token = token;
		this.user = user;
		this.tokenExpirationDate = tokenExpirationDate;
	}

	public String getToken() {
        return this.token;
    }

	public JwtUser getUser() {
		return user;
	}

	public void setUser(JwtUser user) {
		this.user = user;
	}

	public Date getTokenExpirationDate() {
		return tokenExpirationDate;
	}

	public void setTokenExpirationDate(Date tokenExpirationDate) {
		this.tokenExpirationDate = tokenExpirationDate;
	}
    
}
