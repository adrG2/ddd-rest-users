package com.minderest.user.application;

import com.minderest.shared.util.StringCheck;
import com.minderest.user.domain.PasswordEncoder;
import com.minderest.user.domain.User;
import com.minderest.user.domain.UserRepositoryDomainInterface;
import com.minderest.user.domain.exception.ForbiddenException;
import com.minderest.user.domain.exception.UserLoginBadParamsException;
import com.minderest.user.infrastructure.controller.model.LoginUserRest;

public class AccessUser {
    private final UserRepositoryDomainInterface userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccessUser(final UserRepositoryDomainInterface userRepository, final PasswordEncoder passwordEncoder) {
	this.userRepository = userRepository;
	this.passwordEncoder = passwordEncoder;
    }

    public User login(final LoginUserRest loginUserRest) {
	String email = loginUserRest.getEmail();
	String password = loginUserRest.getPassword();

	validateParams(email, password);

	User user = userRepository.findByEmail(email).orElseThrow(ForbiddenException::new);
	String hashedPassword = endocePassword(email, password);

	if (checkPassword(password, hashedPassword))
	    throw new ForbiddenException("Error autenticacion");

	return user;
    }

    private void validateParams(String email, String password) {
	if (StringCheck.isBlankAnyArgument(email, password)) {
	    throw new UserLoginBadParamsException();
	}
    }

    private String endocePassword(String email, String password) {
	return passwordEncoder.encode(email + password);
    }

    private boolean checkPassword(String password, String hashedPassword) {
	return !password.equals(hashedPassword);
    }
}
