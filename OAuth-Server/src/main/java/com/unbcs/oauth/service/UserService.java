package com.unbcs.oauth.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unbcs.oauth.dto.ChangeUserPasswordDTO;
import com.unbcs.oauth.dto.OperationResultStatus;
import com.unbcs.oauth.dto.OperationStatus;
import com.unbcs.oauth.dto.UserRegistrationDTO;
import com.unbcs.oauth.model.AuthGrantedAuthority;
import com.unbcs.oauth.model.AuthUserDetails;
import com.unbcs.oauth.repository.AuthGrantedAuthorityRepository;
import com.unbcs.oauth.repository.AuthUserDetailsRepository;

import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.Configuration;
import me.gosimple.nbvcxz.resources.ConfigurationBuilder;
import me.gosimple.nbvcxz.resources.Dictionary;
import me.gosimple.nbvcxz.resources.DictionaryBuilder;
import me.gosimple.nbvcxz.scoring.Result;

@Service
public class UserService {

	@Autowired
	private AuthUserDetailsRepository authUserDetailsRepository;

	@Autowired
	private AuthGrantedAuthorityRepository authGrantedAuthorityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	JpaUserDetailsManager jpaUserDetailsManager;

	private Boolean validatePassword(String password, String firstName, String lastName, String email,
			String username) {

		List<Dictionary> dictionaryList = ConfigurationBuilder.getDefaultDictionaries();
		dictionaryList.add(new DictionaryBuilder().setDictionaryName("exclude").setExclusion(true).addWord(firstName, 0)
				.addWord(lastName, 0).addWord(email, 0).addWord(username, 0).createDictionary());

		// Create our configuration object and set our custom minimum
		// entropy, and custom dictionary list
		Configuration configuration = new ConfigurationBuilder().setMinimumEntropy(20d).setDictionaries(dictionaryList)
				.createConfiguration();

		// Create our Nbvcxz object with the configuration we built
		Nbvcxz nbvcxz = new Nbvcxz(configuration);
		Result result = nbvcxz.estimate(password);
//		System.out.println(result.getBasicScore());
//		System.out.println(result.isMinimumEntropyMet());
		return result.isMinimumEntropyMet();
	}

	public OperationStatus registerUser(UserRegistrationDTO userDTO) {

		if (userDTO == null || userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
			return new OperationStatus("Username must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getFirstName() == null || userDTO.getFirstName().isBlank()) {
			return new OperationStatus("First Name must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getLastName() == null || userDTO.getLastName().isBlank()) {
			return new OperationStatus("Last Name must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
			return new OperationStatus("Email must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
			return new OperationStatus("Password must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || !userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
			return new OperationStatus("Password does not match.", OperationResultStatus.FAILED, 400);
		}

		if (jpaUserDetailsManager.userExists(userDTO.getUsername())) {
			return new OperationStatus("Username Already Exists!", OperationResultStatus.FAILED, 400);
		}

		AuthUserDetails user = new AuthUserDetails();
		user.setUsername(userDTO.getUsername());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.setEnabled(true);
		user.setCredentialsNonExpired(true);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());

		if (!validatePassword(userDTO.getPassword(), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
				userDTO.getUsername())) {
			return new OperationStatus("The given password is weak and can be guessed. Please provide a stronger password.", OperationResultStatus.FAILED, 400);
		}

		AuthGrantedAuthority grantedAuthority = new AuthGrantedAuthority();
		grantedAuthority.setAuthority("ROLE_USER");
		grantedAuthority.setAuthUserDetails(user);
		authUserDetailsRepository.save(user);
		authGrantedAuthorityRepository.save(grantedAuthority);
		user.setAuthorities(Arrays.asList(grantedAuthority));

		return new OperationStatus("User is created.", OperationResultStatus.SUCCESS, 200);
	}

	public OperationStatus changeUserPassword(ChangeUserPasswordDTO changeUserPasswordDTO, String username) {

		if (changeUserPasswordDTO == null) {
			return new OperationStatus("Fields cannot be empty.", OperationResultStatus.FAILED, 400);
		}

		if (changeUserPasswordDTO.getPassword() == null || changeUserPasswordDTO.getPassword().isBlank()) {
			return new OperationStatus("Password must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (changeUserPasswordDTO.getConfirmPassword() == null || changeUserPasswordDTO.getConfirmPassword().isBlank()
				|| !changeUserPasswordDTO.getPassword().equals(changeUserPasswordDTO.getConfirmPassword())) {
			return new OperationStatus("Password does not match.", OperationResultStatus.FAILED, 400);
		}

		Optional<AuthUserDetails> user = authUserDetailsRepository.findByUsername(username);


		if (user.isEmpty()
				|| !passwordEncoder.matches(changeUserPasswordDTO.getOldPassword(), user.get().getPassword())) {
			return new OperationStatus("Password change failed.", OperationResultStatus.FAILED, 400);
		}

		AuthUserDetails updatedUser = user.get();
		updatedUser.setPassword(passwordEncoder.encode(changeUserPasswordDTO.getPassword()));
		authUserDetailsRepository.save(updatedUser);

		return new OperationStatus("User password is updated.", OperationResultStatus.SUCCESS, 200);
	}

	public OperationStatus registerAdminUser(UserRegistrationDTO userDTO) {

		if (userDTO == null || userDTO.getUsername() == null || userDTO.getUsername().isBlank()) {
			return new OperationStatus("Username must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getFirstName() == null || userDTO.getFirstName().isBlank()) {
			return new OperationStatus("First Name must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getLastName() == null || userDTO.getLastName().isBlank()) {
			return new OperationStatus("Last Name must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
			return new OperationStatus("Email must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
			return new OperationStatus("Password must not be null or empty.", OperationResultStatus.FAILED, 400);
		}

		if (userDTO == null || !userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
			return new OperationStatus("Password does not match.", OperationResultStatus.FAILED, 400);
		}

		if (jpaUserDetailsManager.userExists(userDTO.getUsername())) {
			return new OperationStatus("Username Already Exists!", OperationResultStatus.FAILED, 400);
		}

		AuthUserDetails user = new AuthUserDetails();
		user.setUsername(userDTO.getUsername());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.setEnabled(true);
		user.setCredentialsNonExpired(true);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEmail(userDTO.getEmail());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());

		AuthGrantedAuthority grantedAuthority = new AuthGrantedAuthority();
		grantedAuthority.setAuthority("ROLE_ADMIN");
		grantedAuthority.setAuthUserDetails(user);
		authUserDetailsRepository.save(user);
		authGrantedAuthorityRepository.save(grantedAuthority);
		user.setAuthorities(Arrays.asList(grantedAuthority));

		return new OperationStatus("User is created.", OperationResultStatus.SUCCESS, 200);
	}

}
