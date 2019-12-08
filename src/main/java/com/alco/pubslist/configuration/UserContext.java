package com.alco.pubslist.configuration;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Contains helper methods related to current user
 *
 * @author victor
 * @date 07.12.2019
 */
public class UserContext {

	/**
	 * Checks if user is admin
	 */
	public static boolean isAdmin() {

		return SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getAuthorities()
				.stream()
				.anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
	}

	/**
	 * Can be used to get current user id
	 */
	public static String getUserId() {

		return SecurityContextHolder
				.getContext().getAuthentication().getDetails().toString();
	}
}