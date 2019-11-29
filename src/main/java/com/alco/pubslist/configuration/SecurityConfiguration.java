package com.alco.pubslist.configuration;

import com.alco.pubslist.CachedAuthenticationProvider;
import com.alco.pubslist.security.exceptions.JwtAuthorizationFilter;
import com.alco.pubslist.security.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Value("${pubslist.expirationTime}")
	private String expirationTime;

	@Autowired
	private CachedAuthenticationProvider authenticationProvider;

	@Bean
	public HttpSessionStrategy httpSessionStrategy() {

		return new HeaderHttpSessionStrategy();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {

		auth.authenticationProvider(authenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().disable()
				.csrf().disable()
				.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), Long.parseLong(expirationTime)),
						JwtAuthorizationFilter.class)
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}