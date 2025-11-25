package org.example.statistics.unit.mapper.server;

import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;
import org.example.statistics.mapper.server.ServerMapper;
import org.example.statistics.mapper.server.ServerMapperImpl;
import org.example.statistics.utils.HelperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ServerMapper Unit Tests")
class ServerMapperTest {

	private ServerMapper serverMapper;
	private Server testServer;

	@BeforeEach
	void setUp() {
		// Create the mapper instance directly (no Spring context)
		serverMapper = new ServerMapperImpl();

		testServer = new Server();
		testServer.setEndpoint("test.server.com");
		testServer.setName("Test Server");
		testServer.setMatchIds(List.of("match1", "match2", "match3"));
	}

	@Test
	@DisplayName("Should map Server to ServerStatsDto correctly")
	void toServerStatsDto_WithValidServer_ShouldMapCorrectly() {
		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Test Server");
		assertThat(result.getMatchCount()).isEqualTo(3);
	}

	@Test
	@DisplayName("Should return zero match count when match IDs is null")
	void toServerStatsDto_WithNullMatchIds_ShouldReturnZeroMatchCount() {
		// Given
		testServer.setMatchIds(null);

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getMatchCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should return zero match count when match IDs is empty")
	void toServerStatsDto_WithEmptyMatchIds_ShouldReturnZeroMatchCount() {
		// Given
		testServer.setMatchIds(Collections.emptyList());

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getMatchCount()).isEqualTo(0);
	}

	@Test
	@DisplayName("Should handle server with null name")
	void toServerStatsDto_WithNullName_ShouldMapNull() {
		// Given
		testServer.setName(null);

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getName()).isNull();
	}

	@Test
	@DisplayName("Should use HelperUtils to calculate match count")
	void toServerStatsDto_ShouldUseHelperUtils() {
		// Given
		testServer.setMatchIds(List.of("m1", "m2", "m3"));

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		// Verify that the match count matches what HelperUtils would return
		int expectedCount = HelperUtils.getServerMatchCount(testServer);
		assertThat(result.getMatchCount()).isEqualTo(expectedCount);
	}

	@Test
	@DisplayName("Should return null when server is null")
	void toServerStatsDto_WithNullServer_ShouldReturnNull() {
		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(null);

		// Then
		assertThat(result).isNull();
	}
}