package org.example.statistics.controller.auth;

import org.example.statistics.dto.auth.LoginRequestDto;
import org.example.statistics.dto.auth.LoginResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface AuthController {

	@PostMapping("/login")
	ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto);
}
