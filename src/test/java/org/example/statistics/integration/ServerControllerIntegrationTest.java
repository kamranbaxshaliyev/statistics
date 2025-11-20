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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ServerControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getServers_shouldReturnListOfServers() throws Exception {
		// Act & Assert
		mockMvc.perform(
						get("/servers/info")
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].endpoint").value(anyOf(is("eu-1.game.net"), is("us-1.game.net"))))
				.andExpect(jsonPath("$[0].name").value(anyOf(is("Europe Alpha"), is("US East"))))
				.andExpect(jsonPath("$[0].region").value(anyOf(is("EU"), is("US"))))
				.andExpect(jsonPath("$[0].rating").value(anyOf(is(4.5), is(4.7))));
	}

	@Test
	void getServer_shouldReturnServer_whenPlayerExists() throws Exception {
		// Arrange
		String endpoint = "eu-1.game.net";

		// Act & Assert
		mockMvc.perform(
				get("/servers/{endpoint}/info", endpoint)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(objectMapper.writeValueAsString(
						objectMapper.readTree(new ClassPathResource("__files/server/get-server-success.json").getInputStream())
				)));
	}

	@Test
	void getServer_shouldReturn400_whenPlayerNotFound() throws Exception {
		// Arrange
		String endpoint = "nonExistentServer";

		// Act & Assert
		mockMvc.perform(
						get("/servers/{endpoint}/info", endpoint)
								.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isBadRequest());
	}
}
