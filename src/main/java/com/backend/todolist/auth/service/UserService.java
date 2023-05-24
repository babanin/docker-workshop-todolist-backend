package com.backend.todolist.auth.service;

import com.backend.todolist.auth.controller.UserSigninRequest;
import com.backend.todolist.auth.controller.UserSigninResponse;
import com.backend.todolist.auth.controller.UserSignupRequest;
import com.backend.todolist.auth.controller.UserSignupResponse;
import com.backend.todolist.auth.jwt.JwtTokenGenerator;
import com.backend.todolist.auth.model.User;
import com.backend.todolist.auth.repository.UserRepository;
import com.backend.todolist.errorhandler.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtTokenGenerator jwtTokenGenerator) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    public UserSignupResponse signup(UserSignupRequest userSignupRequest) {
        try {
            String username = userSignupRequest.getUsername();
            String password = userSignupRequest.getPassword();

            if (userRepository.findByUsername(username) != null) {
                throw new BadRequestException("Username is already exist");
            }

            User user = new User(username, passwordEncoder.encode(password));
            user = userRepository.save(user);

            String token = jwtTokenGenerator.createToken(user.getUsername(), user.getRoleAsList());

            return new UserSignupResponse(username, token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password");
        }
    }

    public UserSigninResponse signin(UserSigninRequest userSigninRequest) {
        try {
            String username = userSigninRequest.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, userSigninRequest.getPassword()));
            String token = jwtTokenGenerator.createToken(username, this.userRepository.findByUsername(username).getRoleAsList());

            return new UserSigninResponse(username, token);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password");
        }
    }
}
