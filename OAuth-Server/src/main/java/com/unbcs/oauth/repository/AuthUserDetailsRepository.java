package com.unbcs.oauth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unbcs.oauth.model.AuthUserDetails;

@Repository
public interface AuthUserDetailsRepository extends JpaRepository<AuthUserDetails, Long> {
	Optional<AuthUserDetails> findByUsername(String username);

	Optional<AuthUserDetails> findByPassword(String password);
}
