package com.macquire.rmg.auth.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author pussingh5
 *
 * This will filter all request and verify and validate the token.
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
	ServletException, IOException {

		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");

		
		logger.info("\n\n\n URL: " + request.getRequestURL());
		String authToken = request.getHeader(this.tokenHeader);
		if(!request.getRequestURL().toString().contains("swagger-ui.html") && !request.getRequestURL().toString().contains("api/auth") && !request.getRequestURL().toString().contains("user/register") && !request.getMethod().equals("OPTIONS")) {
			if(null == authToken) {

				throw new IOException("Token can not be left blank");
			}
			if(authToken.startsWith("Bearer ")) {
				authToken = authToken.substring(7);
			}

			String username = jwtTokenUtil.getUsernameFromToken(authToken);

			logger.info("checking authentication: " + username);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				// It is not compelling necessary to load the use details from the database. You could also store the information
				// in the token and read it from it. It's up to you ;)
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

				// For simple validation it is completely sufficient to just check the token integrity. You don't have to call
				// the database compellingly. Again it's up to you ;)
				if (jwtTokenUtil.validateToken(authToken, userDetails)) {
	                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                logger.info("authenticated user " + username + ", setting security context");
	                SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}

		}

		chain.doFilter(request, response);
	}
}