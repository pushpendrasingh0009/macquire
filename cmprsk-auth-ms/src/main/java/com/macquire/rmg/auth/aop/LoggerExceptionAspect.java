/**
 * 
 */
package com.macquire.rmg.auth.aop;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.macquire.rmg.auth.exception.AuthenticationException;
import com.macquire.rmg.auth.exception.UserNotFoundException;
import com.macquire.rmg.auth.model.entity.User;
import com.macquire.rmg.auth.security.JwtAuthenticationRequest;
import com.macquire.rmg.auth.security.JwtTokenUtil;
import com.macquire.rmg.auth.security.JwtUser;

/**
 * @author narkumar8 Aspect for Logger and Exception handling from Controller
 *         and Service layer
 */
@Aspect
@Configuration
public class LoggerExceptionAspect {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@Around("execution(* com.macquire.rmg.auth.controller.AuthenticationRestController.createAuthenticationToken(..))")
	public Object aroundCreateAuthenticationToken(ProceedingJoinPoint joinPoint) throws Throwable {
		JwtAuthenticationRequest request = (JwtAuthenticationRequest) joinPoint.getArgs()[0];
		logger.info("Generating JWT Token for user::" + request.getUsername());
		Object proceed = null;
		try {
			proceed = joinPoint.proceed();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			throw new AuthenticationException("Login Failed");
		}
		logger.info("JWT Token Generation Done:: " + proceed);
		return proceed;
	}

	@Around("execution(* com.macquire.rmg.auth.controller.AuthenticationRestController.refreshAndGetAuthenticationToken(..))")
	public Object aroundRefreshAndGetAuthenticationToken(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
		String token = request.getHeader(tokenHeader);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
		logger.info("Refreshing JWT Token for user::" + user.getUsername() + " last token refresh::"
				+ user.getLastPasswordResetDate());
		Object proceed = null;
		try {
			proceed = joinPoint.proceed();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			throw new Exception("Unknown Exception");
		}
		logger.info("Refresh Token Done:: " + proceed);
		return proceed;
	}

//	@Around("execution(* com.macquire.rmg.auth.controller.AuthenticationRestController.getAuthenticatedUser(..))")
//	public Object aroundGetAuthenticatedUser(ProceedingJoinPoint joinPoint) throws Throwable {
//		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
//		String token = request.getHeader(tokenHeader);
//		String username = jwtTokenUtil.getUsernameFromToken(token);
//		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
//		logger.info("Fetching user from Token::" + user.getUsername() + " last token refresh::"
//				+ user.getLastPasswordResetDate());
//		Object proceed = null;
//		try {
//			proceed = joinPoint.proceed();
//		} catch (Throwable e) {
//			logger.error(e.getMessage());
//			throw new UserNotFoundException("User Not Found");
//		}
//		logger.info("User Fetched from token:: " + user.getUsername());
//		return proceed;
//	}

	@Around("execution(* com.macquire.rmg.auth.controller.AuthenticationRestController.registerUser(..))")
	public Object aroundRegisterUser(ProceedingJoinPoint joinPoint) throws Throwable {
		User user = (User) joinPoint.getArgs()[0];
		logger.info("Registering User in System::" + user.getUsername() + " last token refresh::" + user.getEmail());
		Object proceed = null;
		try {
			proceed = joinPoint.proceed();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			throw new UserNotFoundException("Unknown Exception");
		}
		logger.info("User Registered Successful in System:: " + user.getUsername());
		return proceed;
	}
}
