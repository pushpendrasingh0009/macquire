package com.macquire.rmg.auth.controller;

import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.macquire.rmg.auth.exception.GenericResponse;
import com.macquire.rmg.auth.model.AuthorityName;
import com.macquire.rmg.auth.model.entity.User;
import com.macquire.rmg.auth.repository.AuthorityRepository;
import com.macquire.rmg.auth.repository.UserRepository;
import com.macquire.rmg.auth.security.JwtAuthenticationRequest;
import com.macquire.rmg.auth.security.JwtAuthenticationResponse;
import com.macquire.rmg.auth.security.JwtTokenUtil;
import com.macquire.rmg.auth.security.JwtUser;

/**
 * @author pussingh5
 *
 * This file is for authentication the user and provide and refresh the JWT token.
 */
@RestController
public class AuthenticationRestController {


    @Value("${jwt.header}")
    private String tokenHeader;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    private final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
    	logger.info("auth "+ authenticationRequest.getUsername()+ " pas: "+ authenticationRequest.getPassword());
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security so we can generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails, null);

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String  token    = request.getHeader(tokenHeader);
        String  username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user     = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @RequestMapping(value = "api/user", method = RequestMethod.GET)
    public JwtUser getAuthenticatedUser(HttpServletRequest request) {
        String authToken = request.getHeader(this.tokenHeader);
        if(authToken.startsWith("Bearer ")) {
        	authToken = authToken.substring(7);
        }
        String username = jwtTokenUtil.getUsernameFromToken(authToken);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        return user;
    }
    
    @RequestMapping(value = "api/user/register", method = RequestMethod.POST)
    public ResponseEntity<?> registerUser(@RequestBody User user) {

    	GenericResponse response = new GenericResponse();
    	response.setStatus("success");
    	
    	UserDetails userDetails = null;
    	try {
    		userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    	}catch (Exception e) {
    		logger.info(String.format("Warning exception = %s ", e.getMessage()));
    	}
    	
		if(null != userDetails) {
			response.setStatus("failure");
			response.setMessage(String.format("User is already exist for this username: %s", user.getUsername()));
			
			return new ResponseEntity<GenericResponse>(response, HttpStatus.OK);
		}
    	
    	User userEntity = new User();
    	userEntity.setUsername(user.getUsername());
    	userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
    	userEntity.setEmail(user.getEmail());
    	userEntity.setFirstname(user.getFirstname());
    	userEntity.setLastname(user.getLastname());
    	userEntity.setEnabled(true);
    	userEntity.setLastPasswordResetDate(new Date());
    	
    	userEntity.setAuthorities(Arrays.asList(authorityRepository.findByName(AuthorityName.ROLE_ADMIN)));
    	
    	long size = userRepository.findAll().size();
    	userEntity.setId(size+1);
    	userRepository.save(userEntity);
    	
        return new ResponseEntity<GenericResponse>(response, HttpStatus.OK);
    }

}
