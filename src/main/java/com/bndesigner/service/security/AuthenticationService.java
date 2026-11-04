package com.bndesigner.service.security;

import com.bndesigner.dto.request.login.LoginRequest;
import com.bndesigner.dto.response.login.LoginResponse;

public interface AuthenticationService {
	
	LoginResponse autenticar(LoginRequest loginRequest);

}
