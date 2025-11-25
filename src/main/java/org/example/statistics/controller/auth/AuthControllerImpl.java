package org.example.statistics.controller.auth;

import lombok.RequiredArgsConstructor;
import org.example.statistics.dto.auth.LoginRequestDto;
import org.example.statistics.dto.auth.LoginResponseDto;
import org.example.statistics.service.auth.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

	private final AuthenticationService authenticationService;

	@Override
	public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
		LoginResponseDto response = authenticationService.login(loginRequestDto);
		return ResponseEntity.ok(response);
	}
}
