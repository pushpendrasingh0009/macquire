package com.macquire.rmg.auth.service;

import com.macquire.rmg.auth.model.AuthenticationRequest;
import com.macquire.rmg.auth.security.JwtUser;

public interface UserAuthenticationService {

	String createAuthenticationToken(AuthenticationRequest authenticationRequest);

	String refreshAndGetAuthenticationToken(String authToken, JwtUser user);

}
