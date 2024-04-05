package com.unbcs.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unbcs.oauth.repository.AuthGrantedAuthorityRepository;
import com.unbcs.oauth.repository.AuthUserDetailsRepository;

@Configuration
public class DBInitializerConfig {
	@Autowired
	private AuthUserDetailsRepository authUserDetailsRepository;

	@Autowired
	private AuthGrantedAuthorityRepository authGrantedAuthorityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// initialize the user in DB
	@Bean
	public CommandLineRunner initializeJpaData() {
		return (args) -> {
			System.out.println("application started");

//			HashMap<String, String> users=  (HashMap<String, String>) Map.ofEntries(
//				    Map.entry("a", "b"),
//				    Map.entry("c", "d")
//				);
//
//			// uncomment if required
//
//			AuthUserDetails user2 = new AuthUserDetails();
//			user2.setUsername("user2");
//			user2.setPassword(passwordEncoder.encode("password"));
//			user2.setEnabled(true);
//			user2.setCredentialsNonExpired(true);
//			user2.setAccountNonExpired(true);
//			user2.setAccountNonLocked(true);
//
//			AuthGrantedAuthority grantedAuthority = new AuthGrantedAuthority();
//			grantedAuthority.setAuthority("ROLE_USER");
//			grantedAuthority.setAuthUserDetails(user2);
//			authUserDetailsRepository.save(user2);
//			authGrantedAuthorityRepository.save(grantedAuthority);
//			user2.setAuthorities(Arrays.asList(grantedAuthority));
////			
//			AuthUserDetails user1 = new AuthUserDetails();
//			user1.setUsername("user1");
//			user1.setPassword(passwordEncoder.encode("password"));
//			user1.setEnabled(true);
//			user1.setCredentialsNonExpired(true);
//			user1.setAccountNonExpired(true);
//			user1.setAccountNonLocked(true);
//
//			AuthGrantedAuthority grantedAuthority1 = new AuthGrantedAuthority();
//			grantedAuthority1.setAuthority("ROLE_ADMIN");
//			grantedAuthority1.setAuthUserDetails(user1);
//			authUserDetailsRepository.save(user1);
//			authGrantedAuthorityRepository.save(grantedAuthority1);
//			user2.setAuthorities(Arrays.asList(grantedAuthority1));
//			

		};

	}
}
