package com.macquire.rmg.auth.security.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.macquire.rmg.auth.security.JwtTokenUtil;

/**
 * @author pussingh5
 *
 * This filter is to verify and validate the token for all authenticated request.
 */
public class AuthenticationTokenFilter implements Filter {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${jwt.header}")
	private String tokenHeader;

	private List<String> excludedUrls;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		excludedUrls = Arrays.asList("swagger-ui.html","api/auth","user/register","OPTIONS");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpServletRequest request= (HttpServletRequest) servletRequest;
		String username = null;

		if(!(excludedUrls.stream().filter(url-> request.getRequestURL().toString().toLowerCase().contains(url.toLowerCase())).count() > 0) && !request.getMethod().equals("OPTIONS")) {
			String authToken = request.getHeader(this.tokenHeader);
			
			if(null == authToken) {
				logger.info("Token can not be left blank.");
				throw new ServiceException("Token can not be left blank.");
			}
			
			if(authToken.startsWith("Bearer ")) {
				authToken = authToken.substring(7);
				username = jwtTokenUtil.getUsernameFromToken(authToken);
			}

			if(null == username) {
				logger.info("Username can not be left blank.");
				throw new ServiceException("Username can not be left blank.");
			}
			
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				logger.info("authenticated user: " + username + ", setting security context");
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}else{
				logger.info("token is invalided.");
				throw new ServiceException("token is invalided");
			}

		}

		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}