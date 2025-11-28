package org.example.statistics.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.statistics.domain.Session;
import org.example.statistics.domain.User;
import org.example.statistics.enums.UserType;
import org.example.statistics.dto.auth.LoginRequestDto;
import org.example.statistics.repository.SessionRepository;
import org.example.statistics.repository.UserRepository;
import org.example.statistics.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtUtil jwtUtil;

	@BeforeEach
	void setUp() {
		sessionRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void login_WithValidCredentials_ReturnsToken() throws Exception {
		// Given
		createUser("testuser", "password123", UserType.ADMIN);
		LoginRequestDto loginRequest = new LoginRequestDto("testuser", "password123");

		// When & Then
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", notNullValue()));
	}

	@Test
	void login_WithInvalidUsername_ReturnsBadRequest() throws Exception {
		// Given
		LoginRequestDto loginRequest = new LoginRequestDto("nonexistent", "password123");

		// When & Then
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void login_WithInvalidPassword_ReturnsBadRequest() throws Exception {
		// Given
		createUser("testuser", "password123", UserType.ADMIN);
		LoginRequestDto loginRequest = new LoginRequestDto("testuser", "wrongpassword");

		// When & Then
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void accessProtectedEndpoint_WithValidToken_ReturnsOk() throws Exception {
		// Given
		User user = createUser("admin", "password123", UserType.ADMIN);
		Session session = createSession(user.getUserName());
		String token = jwtUtil.generateToken(user.getUserName(), user.getUserType().name(), session.getSessionId());

		// When & Then
		mockMvc.perform(get("/servers/info")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	void accessProtectedEndpoint_WithoutToken_ReturnsUnauthorized() throws Exception {
		// When & Then
		mockMvc.perform(get("/servers/info"))
				.andExpect(status().isForbidden());
	}

	@Test
	void accessProtectedEndpoint_WithInvalidToken_ReturnsUnauthorized() throws Exception {
		// When & Then
		mockMvc.perform(get("/servers/info")
						.header("Authorization", "Bearer invalid.token.here"))
				.andExpect(status().isForbidden());
	}

	@Test
	void accessProtectedEndpoint_WithExpiredSession_ReturnsUnauthorized() throws Exception {
		// Given
		User user = createUser("admin", "password123", UserType.ADMIN);
		String token = jwtUtil.generateToken(user.getUserName(), user.getUserType().name(), "different-session-id");

		// When & Then
		mockMvc.perform(get("/servers/info")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void accessAdminEndpoint_WithPlayerRole_ReturnsForbidden() throws Exception {
		// Given
		User user = createUser("player", "password123", UserType.PLAYER);
		Session session = createSession(user.getUserName());
		String token = jwtUtil.generateToken(user.getUserName(), user.getUserType().name(), session.getSessionId());

		// When & Then
		mockMvc.perform(get("/servers/info")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	private User createUser(String username, String password, UserType userType) {
		User user = User.builder()
				.userName(username)
				.password(passwordEncoder.encode(password))
				.userType(userType)
				.build();
		return userRepository.save(user);
	}

	private Session createSession(String username) {
		Session session = Session.builder()
				.sessionId(java.util.UUID.randomUUID().toString())
				.userName(username)
				.build();
		return sessionRepository.save(session);
	}
}
