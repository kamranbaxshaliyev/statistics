package org.example.statistics.service.auth;

import org.example.statistics.dto.auth.LoginRequestDto;
import org.example.statistics.dto.auth.LoginResponseDto;

public interface AuthenticationService {
	LoginResponseDto login(LoginRequestDto loginRequestDto);
}
