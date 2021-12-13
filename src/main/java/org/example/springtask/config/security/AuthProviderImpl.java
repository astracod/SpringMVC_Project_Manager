package org.example.springtask.config.security;

import lombok.extern.slf4j.Slf4j;
import org.example.springtask.dto.SecurityWorkerDto;
import org.example.springtask.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AuthProviderImpl implements AuthenticationProvider {

    private final ProjectService projectService;

    private final PasswordEncoder passwordEncoder;

    public AuthProviderImpl(ProjectService projectService, PasswordEncoder passwordEncoder) {
        this.projectService = projectService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();

        String password = authentication.getCredentials().toString();

        SecurityWorkerDto workerDto = projectService.getWorkerByEmail(email);

        if (!passwordEncoder.matches(password, workerDto.getPassword())) {
            throw new BadCredentialsException(" Внимание!!! Пароль не верен.");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();

        return new UsernamePasswordAuthenticationToken(workerDto, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
