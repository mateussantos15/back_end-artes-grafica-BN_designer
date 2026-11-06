package com.bndesigner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.bndesigner.service.security.UsuarioDetailsService;

@Configuration
public class SecurityConfig {	
	
	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		
		http
		    .csrf(csrf -> csrf.disable())
		    .sessionManagement(session -> session
		    		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    		)
		    		.authorizeHttpRequests(auth -> auth
		    				.requestMatchers("/api/auth/**").permitAll()
		    				.requestMatchers(HttpMethod.POST, "/api/usuarios/**").permitAll()
		    				.anyRequest().authenticated()
		    				);
		return http.build();
		
	}
	
	@Bean
	public AuthenticationManager authenticationManager (
			AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider(
			UsuarioDetailsService usuarioDetailsService, 
			PasswordEncoder passwordEncoder) {
		
		DaoAuthenticationProvider provider = 
				new DaoAuthenticationProvider(usuarioDetailsService);
		
		provider.setPasswordEncoder(passwordEncoder);
		
		return provider;
	}
}
