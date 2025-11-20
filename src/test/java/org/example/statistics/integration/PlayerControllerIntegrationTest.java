package org.example.statistics.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getStats_shouldReturnPlayerStats_whenPlayerExists() throws Exception {
		// Arrange
		String playerName = "PlayerOne";

		// Act & Assert
		mockMvc.perform(
						get("/players/{playerName}/stats", playerName)
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(objectMapper.writeValueAsString(
						objectMapper.readTree(new ClassPathResource("__files/player/get-stats-success.json").getInputStream())
				)));
	}

	@Test
	void getStats_shouldReturn400_whenPlayerNotFound() throws Exception {
		// Arrange
		String playerName = "nonExistentPlayer";

		// Act & Assert
		mockMvc.perform(
						get("/players/{playerName}/stats", playerName)
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isBadRequest());
	}
}
