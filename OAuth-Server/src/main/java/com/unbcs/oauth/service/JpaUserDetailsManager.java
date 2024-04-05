package com.unbcs.oauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.unbcs.oauth.model.AuthUserDetails;
import com.unbcs.oauth.repository.AuthUserDetailsRepository;

import jakarta.transaction.Transactional;

@Service
public class JpaUserDetailsManager implements UserDetailsManager {

	@Autowired
	private AuthUserDetailsRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username = " + username));
	}

	@Override
	public void createUser(UserDetails user) {
		repository.save((AuthUserDetails) user);
	}

	@Override
	public void updateUser(UserDetails user) {
		repository.save((AuthUserDetails) user);
	}

	@Override
	public void deleteUser(String username) {
		AuthUserDetails userDetails = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No User found for username -> " + username));
		repository.delete(userDetails);
	}

	/**
	 * This method assumes that both oldPassword and the newPassword params are
	 * encoded with configured passwordEncoder
	 *
	 * @param oldPassword the old password of the user
	 * @param newPassword the new password of the user
	 */
	@Override
	@Transactional
	public void changePassword(String oldPassword, String newPassword) {
		AuthUserDetails userDetails = repository.findByPassword(oldPassword)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid password "));
		userDetails.setPassword(newPassword);
		repository.save(userDetails);
	}

	@Override
	public boolean userExists(String username) {
		return repository.findByUsername(username).isPresent();
	}
}
