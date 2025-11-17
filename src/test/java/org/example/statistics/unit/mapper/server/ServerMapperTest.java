package org.example.statistics.unit.mapper.server;

import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;
import org.example.statistics.mapper.server.ServerMapper;
import org.example.statistics.mapper.server.ServerMapperImpl;
import org.example.statistics.utils.HelperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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
	@DisplayName("Should calculate match count from match IDs list size")
	void toServerStatsDto_ShouldCalculateMatchCount() {
		// Given
		testServer.setMatchIds(List.of("m1", "m2", "m3", "m4", "m5"));

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getMatchCount()).isEqualTo(5);
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

	@ParameterizedTest
	@MethodSource("provideMatchCountScenarios")
	@DisplayName("Should correctly calculate match count for different scenarios")
	void toServerStatsDto_WithDifferentMatchCounts_ShouldCalculateCorrectly(
			List<String> matchIds,
			int expectedCount) {
		// Given
		testServer.setMatchIds(matchIds);

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getMatchCount()).isEqualTo(expectedCount);
	}

	private static Stream<Arguments> provideMatchCountScenarios() {
		return Stream.of(
				Arguments.of(null, 0),
				Arguments.of(Collections.emptyList(), 0),
				Arguments.of(List.of("m1"), 1),
				Arguments.of(List.of("m1", "m2"), 2),
				Arguments.of(List.of("m1", "m2", "m3", "m4", "m5"), 5),
				Arguments.of(List.of("m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8", "m9", "m10"), 10)
		);
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
	@DisplayName("Should handle server with empty name")
	void toServerStatsDto_WithEmptyName_ShouldMapEmpty() {
		// Given
		testServer.setName("");

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getName()).isEmpty();
	}

	@Test
	@DisplayName("Should handle server with very long match IDs list")
	void toServerStatsDto_WithManyMatches_ShouldCalculateCorrectly() {
		// Given
		List<String> manyMatches = Stream.generate(() -> "match")
				.limit(100)
				.toList();
		testServer.setMatchIds(manyMatches);

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getMatchCount()).isEqualTo(100);
	}

	@ParameterizedTest
	@MethodSource("provideCompleteServerScenarios")
	@DisplayName("Should map complete server objects correctly")
	void toServerStatsDto_WithCompleteScenarios_ShouldMapAllFieldsCorrectly(
			String name,
			List<String> matchIds,
			int expectedMatchCount) {
		// Given
		Server server = new Server();
		server.setName(name);
		server.setMatchIds(matchIds);

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(server);

		// Then
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getMatchCount()).isEqualTo(expectedMatchCount);
	}

	private static Stream<Arguments> provideCompleteServerScenarios() {
		return Stream.of(
				Arguments.of("Empty Server", Collections.emptyList(), 0),
				Arguments.of("Small Server", List.of("m1"), 1),
				Arguments.of("Medium Server", List.of("m1", "m2", "m3", "m4", "m5"), 5),
				Arguments.of("Large Server",
						List.of("m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8", "m9", "m10"), 10),
				Arguments.of("Null Matches Server", null, 0)
		);
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
	@DisplayName("Should handle server with duplicate match IDs")
	void toServerStatsDto_WithDuplicateMatchIds_ShouldCountAll() {
		// Given
		testServer.setMatchIds(List.of("m1", "m1", "m2", "m2", "m3"));

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(testServer);

		// Then
		assertThat(result.getMatchCount()).isEqualTo(5); // Counts duplicates
	}


	@Test
	@DisplayName("Should return null when server is null")
	void toServerStatsDto_WithNullServer_ShouldReturnNull() {
		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(null);

		// Then
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("Should handle server with all null fields")
	void toServerStatsDto_WithAllNullFields_ShouldMapNullsAndZeroCount() {
		// Given
		Server server = new Server();

		// When
		ServerStatsDto result = serverMapper.toServerStatsDto(server);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isNull();
		assertThat(result.getMatchCount()).isEqualTo(0);
	}
}