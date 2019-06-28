package com.macquire.rmg.search.controller;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.macquire.rmg.search.exception.GenericResponse;
import com.macquire.rmg.search.model.AuthorityName;
import com.macquire.rmg.search.model.entity.User;
import com.macquire.rmg.search.repository.AuthorityRepository;
import com.macquire.rmg.search.repository.UserRepository;
import com.macquire.rmg.search.security.JwtTokenUtil;
import com.macquire.rmg.search.security.JwtUser;

/**
 * @author pussingh5
 *
 * This file is for getting user information and register new user.
 */
@RestController
public class UserRestController {

    @Value("${jwt.header}")
    private String tokenHeader;

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
