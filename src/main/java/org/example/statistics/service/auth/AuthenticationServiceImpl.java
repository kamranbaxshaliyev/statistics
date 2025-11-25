package org.example.statistics.service.auth;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.User;
import org.example.statistics.dto.auth.LoginRequestDto;
import org.example.statistics.dto.auth.LoginResponseDto;
import org.example.statistics.exception.EntityNotFoundException;
import org.example.statistics.repository.UserRepository;
import org.example.statistics.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {
		User user = userRepository.findByUserName(loginRequestDto.getUsername())
				.orElseThrow(() -> new EntityNotFoundException("Invalid username or password"));

		if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
			throw new EntityNotFoundException("Invalid username or password");
		}

		String token = jwtUtil.generateToken(user.getUserName(), user.getUserType().name());

		return new LoginResponseDto(token);
	}
}
