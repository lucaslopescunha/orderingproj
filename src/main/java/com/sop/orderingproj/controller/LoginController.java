package com.sop.orderingproj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sop.orderingproj.dto.LoginDto;
import com.sop.orderingproj.dto.RecoveryJwtTokenDto;
import com.sop.orderingproj.security.AuthenticationFilter;
import com.sop.orderingproj.service.JwtService;
import com.sop.orderingproj.service.UserDetailsServiceImpl;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final AuthenticationFilter authenticationFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginController(AuthenticationManager authenticationManager,
            JwtService jwtService, UserDetailsServiceImpl userDetailsServiceImpl,
            AuthenticationFilter authenticationFilter) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.authenticationFilter = authenticationFilter;
    }

    @PostMapping
    public ResponseEntity<RecoveryJwtTokenDto> postMethodName(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.username(), loginDto.password()));
        if (authentication.isAuthenticated()) {
            var token = jwtService.generateToken(userDetailsServiceImpl.loadUserByUsername(loginDto.username()));
            return new ResponseEntity<>(new RecoveryJwtTokenDto(token), HttpStatus.OK);

        } else {
            throw new UsernameNotFoundException("Invalid Credentials");
        }
    }

}
