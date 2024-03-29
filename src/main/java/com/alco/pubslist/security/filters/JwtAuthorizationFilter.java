package com.alco.pubslist.security.filters;

import com.alco.pubslist.Helper;
import com.alco.pubslist.security.RestResponses;
import com.alco.pubslist.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends GenericFilterBean {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

		String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
		if (!StringUtils.isEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			try {
				byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

				Jws<Claims> parsedToken = Jwts.parser()
						.setSigningKey(signingKey)
						.parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));

				String username = parsedToken.getBody().getSubject();

				String userId = parsedToken.getHeader().get("user_id").toString();

				List<GrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
						.get("role")).stream().map(role -> (String) role)
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());

				if (!StringUtils.isEmpty(username)) {

					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null
							, authorities);
					auth.setDetails(userId);
					return auth;
				}
			}
			catch (ExpiredJwtException exception) {
				log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
			}
			catch (UnsupportedJwtException exception) {
				log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
			}
			catch (MalformedJwtException exception) {
				log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
			}
			catch (IllegalArgumentException exception) {
				log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
			}
			catch (SignatureException exception) {
				log.warn("Request to parse JWT with wrong signature : {} failed : {}", token, exception.getMessage());
			}
		}

		return null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest servletRequest = (HttpServletRequest) request;
		Authentication authentication = getAuthentication(servletRequest);
		if (authentication == null) {
			if (servletRequest.getServletPath().equals(SecurityConstants.SIGN_UP_URL)
					&& servletRequest.getMethod().equals("POST"))
				chain.doFilter(request, response);
			else {
				Helper.formResponse(response, RestResponses.AUTHORIZATION_FAILED);
			}
			return;
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}

}
