package org.example.authenticationservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authenticationservice.DTO.AuthenticationRequest;
import org.example.authenticationservice.DTO.AuthenticationResponse;
import org.example.authenticationservice.DTO.RegisterRequest;
import org.example.authenticationservice.config.ApplicationConfig;
import org.example.authenticationservice.config.JwtService;
import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.entity.User;
import org.example.authenticationservice.repository.UserRepository;
import org.example.authenticationservice.repository.RoleRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final ApplicationConfig applicationConfig;

    public AuthenticationResponse register(RegisterRequest request) {
        log.info(request.toString());
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("user already exists");
        }

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(applicationConfig.passwordEncoder().encode(request.getPassword()))
                .roles(Collections.singletonList(roleRepository.findByName("CUSTOMER").get()))
                .build();

        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken((UserDetails) newUser);

        return AuthenticationResponse.builder().
                token(jwtToken)
                .authorisations(newUser
                        .getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info(request.toString());

        applicationConfig.authenticationProvider().authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );
        log.info(request.toString());
        log.info(userRepository.findByEmail(request.getEmail()).orElseThrow(RuntimeException::new).getFirstName());
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new IllegalArgumentException("not found"));
        var jwtToken = jwtService.generateToken((UserDetails) user);

        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .authorisations(user
                        .getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName()+" hcuibhjknabhv hibjociyv hjo");
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new RuntimeException("No user with this email"));
        }else{
            throw new RuntimeException("the jwt is incomplete");
        }
    }

    @Transactional(readOnly = true)
    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public List<String> getAuthorities(String email) {
        var customer = userRepository.findByEmail(email);
        return customer
                .get()
                .getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }



}
