package com.sop.orderingproj.configuration;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console; // Static import is useful

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sop.orderingproj.security.AuthenticationFilter;
import com.sop.orderingproj.service.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {

        private final AuthenticationFilter authenticationFilter;
        private final UserDetailsServiceImpl userDetailsService;

        public static final String[] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {
                        "/login",
                        "/users",
                        "/actuator",
                        "/favicon.ico",
                        "/h2-console"
        };

        public static final String[] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {
                        "/users/test",
                        "/users/all",
                        "/vehicle",
                        "/users/uploadphoto"
        };

        public static final String[] ENDPOINTS_CUSTOMER = {
                        "/users/test/customer"
        };

        public static final String[] ENDPOINTS_ADMIN = {
                        "/users/test/admin"
        };

        public SecurityConfig(AuthenticationFilter authenticationFilter,
                        UserDetailsServiceImpl userDetailsService) {
                this.authenticationFilter = authenticationFilter;
                this.userDetailsService = userDetailsService;
        }

        /*
         * @Bean
         * 
         * @Order(1)
         * public SecurityFilterChain securityFilterChainUser(HttpSecurity http) throws
         * Exception {
         * http
         * .csrf(AbstractHttpConfigurer::disable)
         * .securityMatcher("/login")
         * .authorizeHttpRequests(registry -> {
         * registry.requestMatchers("*").authenticated().anyRequest().permitAll();
         * })
         * // .addFilterBefore(filterChainExceptionHandler,
         * // UsernamePasswordAuthenticationFilter.class)
         * .userDetailsService(userDetailsService)
         * // Disable CSRF protection for the H2 console path
         * // The 'csrf' method now takes a lambda for configuration.
         * .csrf(CsrfConfigurer::disable)
         * // Configure headers, specifically X-Frame-Options to allow H2 console to run
         * in
         * // an iframe
         * // The 'headers' method also takes a lambda for configuration.
         * .headers(headers -> headers
         * // Allow frames from the same origin to display the H2 console UI
         * .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
         * 
         * .sessionManagement(session -> session
         * .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
         * http.addFilterBefore(this.authenticationFilter,
         * UsernamePasswordAuthenticationFilter.class);
         * 
         * return http.build();
         * }
         */
        // @Order(2)
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // Configure authorization rules
                                .authorizeHttpRequests(authorize -> {
                                        authorize
                                                        // Allow public access to the H2 console path.
                                                        // 'requestMatchers' is the modern equivalent of 'antMatchers'
                                                        // or 'mvcMatchers'.
                                                        // Use AntPathRequestMatcher for pattern matching.
                                                        .requestMatchers(EndpointRequest.to(
                                                                        "startup", "health"))
                                                        .permitAll()
                                                        .requestMatchers(toH2Console()).permitAll()
                                                        .requestMatchers("/users/**", "/login", "/actuator/health/**", "/actuator/info").permitAll()
                                                        .requestMatchers("/task").authenticated()

                                                        // All other requests require authentication
                                                        .anyRequest().denyAll();
                                })
                                .userDetailsService(userDetailsService)
                                // Disable CSRF protection for the H2 console path
                                // The 'csrf' method now takes a lambda for configuration.
                                .csrf(CsrfConfigurer::disable)
                                // Configure headers, specifically X-Frame-Options to allow H2 console to run in
                                // an iframe
                                // The 'headers' method also takes a lambda for configuration.
                                .headers(headers -> headers
                                                // Allow frames from the same origin to display the H2 console UI
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
                http.addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);
                // If you have other security configurations (e.g., form login, http basic),
                // you can add them here using the modern lambda-based syntax:
                // .formLogin(withDefaults()); // Uses Spring Security's default login page
                // .httpBasic(withDefaults()); // Enables HTTP Basic authentication

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder getPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public ApplicationListener<AuthenticationSuccessEvent> successListener() {

                return event -> {
                        System.out.println("SUCCESS [%s] %s".formatted(
                                        event.getAuthentication().getClass().getSimpleName(),
                                        event.getAuthentication().getName()));
                };
        }

}
