	package org.example.statistics.unit.service.server;

	import org.example.statistics.domain.Match;
	import org.example.statistics.domain.Server;
	import org.example.statistics.dto.server.ServerStatsDto;
	import org.example.statistics.exception.EntityNotFoundException;
	import org.example.statistics.mapper.server.ServerMapper;
	import org.example.statistics.repository.MatchRepository;
	import org.example.statistics.repository.ServerRepository;
	import org.example.statistics.service.server.ServerServiceImpl;
	import org.junit.jupiter.api.BeforeEach;
	import org.junit.jupiter.api.DisplayName;
	import org.junit.jupiter.api.Test;
	import org.junit.jupiter.api.extension.ExtendWith;
	import org.junit.jupiter.params.ParameterizedTest;
	import org.junit.jupiter.params.provider.Arguments;
	import org.junit.jupiter.params.provider.MethodSource;
	import org.junit.jupiter.params.provider.ValueSource;
	import org.mockito.InjectMocks;
	import org.mockito.Mock;
	import org.mockito.junit.jupiter.MockitoExtension;

	import java.time.LocalDateTime;
	import java.util.Collections;
	import java.util.List;
	import java.util.Optional;
	import java.util.stream.Stream;

	import static org.assertj.core.api.Assertions.assertThat;
	import static org.assertj.core.api.Assertions.assertThatThrownBy;
	import static org.mockito.ArgumentMatchers.any;
	import static org.mockito.Mockito.*;

	@ExtendWith(MockitoExtension.class)
	@DisplayName("ServerService Unit Tests")
	class ServerServiceImplTest {

		@Mock
		private ServerRepository serverRepository;

		@Mock
		private MatchRepository matchRepository;

		@Mock
		private ServerMapper serverMapper;

		@InjectMocks
		private ServerServiceImpl serverService;

		private Server testServer1;
		private Server testServer2;
		private Match testMatch1;
		private Match testMatch2;
		private Match testMatch3;

		@BeforeEach
		void setUp() {
			testServer1 = new Server();
			testServer1.setEndpoint("server1.com");
			testServer1.setName("Test Server 1");

			testServer2 = new Server();
			testServer2.setEndpoint("server2.com");
			testServer2.setName("Test Server 2");

			testMatch1 = new Match();
			testMatch1.setId("match1");
			testMatch1.setServerEndpoint("server1.com");
			testMatch1.setTimestamp(LocalDateTime.of(2024, 1, 15, 10, 0));

			testMatch2 = new Match();
			testMatch2.setId("match2");
			testMatch2.setServerEndpoint("server1.com");
			testMatch2.setTimestamp(LocalDateTime.of(2024, 1, 15, 14, 30));

			testMatch3 = new Match();
			testMatch3.setId("match3");
			testMatch3.setServerEndpoint("server2.com");
			testMatch3.setTimestamp(LocalDateTime.of(2024, 1, 16, 9, 0));
		}

		// ==================== getServers Tests ====================

		@Test
		@DisplayName("Should return all servers when servers exist")
		void getServers_WhenServersExist_ShouldReturnAllServers() {
			// Given
			List<Server> servers = List.of(testServer1, testServer2);
			when(serverRepository.findAll()).thenReturn(servers);

			// When
			List<Server> result = serverService.getServers();

			// Then
			assertThat(result).hasSize(2);
			assertThat(result).containsExactly(testServer1, testServer2);
			verify(serverRepository).findAll();
		}

		@Test
		@DisplayName("Should return empty list when no servers exist")
		void getServers_WhenNoServersExist_ShouldReturnEmptyList() {
			// Given
			when(serverRepository.findAll()).thenReturn(Collections.emptyList());

			// When
			List<Server> result = serverService.getServers();

			// Then
			assertThat(result).isEmpty();
			verify(serverRepository).findAll();
		}

		@Test
		@DisplayName("Should return single server when only one exists")
		void getServers_WhenSingleServerExists_ShouldReturnSingleServer() {
			// Given
			when(serverRepository.findAll()).thenReturn(List.of(testServer1));

			// When
			List<Server> result = serverService.getServers();

			// Then
			assertThat(result).hasSize(1);
			assertThat(result).containsExactly(testServer1);
		}

		// ==================== getServer Tests ====================

		@Test
		@DisplayName("Should return server when server exists")
		void getServer_WhenServerExists_ShouldReturnServer() {
			// Given
			when(serverRepository.findById("server1.com")).thenReturn(Optional.of(testServer1));

			// When
			Server result = serverService.getServer("server1.com");

			// Then
			assertThat(result).isNotNull();
			assertThat(result.getEndpoint()).isEqualTo("server1.com");
			assertThat(result.getName()).isEqualTo("Test Server 1");
			verify(serverRepository).findById("server1.com");
		}

		@ParameterizedTest
		@ValueSource(strings = {"nonexistent.com", "invalid-server", "missing.endpoint"})
		@DisplayName("Should throw EntityNotFoundException when server does not exist")
		void getServer_WhenServerDoesNotExist_ShouldThrowEntityNotFoundException(String endpoint) {
			// Given
			when(serverRepository.findById(endpoint)).thenReturn(Optional.empty());

			// When & Then
			assertThatThrownBy(() -> serverService.getServer(endpoint))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage("Server with endpoint " + endpoint + " not found");

			verify(serverRepository).findById(endpoint);
		}

		// ==================== getMatches Tests ====================

		@Test
		@DisplayName("Should return matches for specific server and date")
		void getMatches_WhenMatchesExist_ShouldReturnFilteredMatches() {
			// Given
			List<Match> allMatches = List.of(testMatch1, testMatch2, testMatch3);
			when(matchRepository.findAll()).thenReturn(allMatches);

			// When
			List<Match> result = serverService.getMatches("server1.com", "2024-01-15");

			// Then
			assertThat(result).hasSize(2);
			assertThat(result).containsExactly(testMatch1, testMatch2);
			verify(matchRepository).findAll();
		}

		@Test
		@DisplayName("Should return empty list when no matches for server and date")
		void getMatches_WhenNoMatchesForServerAndDate_ShouldReturnEmptyList() {
			// Given
			List<Match> allMatches = List.of(testMatch1, testMatch2, testMatch3);
			when(matchRepository.findAll()).thenReturn(allMatches);

			// When
			List<Match> result = serverService.getMatches("server3.com", "2024-01-15");

			// Then
			assertThat(result).isEmpty();
			verify(matchRepository).findAll();
		}

		@Test
		@DisplayName("Should return empty list when server matches but date does not")
		void getMatches_WhenServerMatchesButDateDoesNot_ShouldReturnEmptyList() {
			// Given
			List<Match> allMatches = List.of(testMatch1, testMatch2, testMatch3);
			when(matchRepository.findAll()).thenReturn(allMatches);

			// When
			List<Match> result = serverService.getMatches("server1.com", "2024-01-20");

			// Then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should return empty list when date matches but server does not")
		void getMatches_WhenDateMatchesButServerDoesNot_ShouldReturnEmptyList() {
			// Given
			List<Match> allMatches = List.of(testMatch1, testMatch2, testMatch3);
			when(matchRepository.findAll()).thenReturn(allMatches);

			// When
			List<Match> result = serverService.getMatches("server3.com", "2024-01-15");

			// Then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("Should return empty list when no matches exist at all")
		void getMatches_WhenNoMatchesExist_ShouldReturnEmptyList() {
			// Given
			when(matchRepository.findAll()).thenReturn(Collections.emptyList());

			// When
			List<Match> result = serverService.getMatches("server1.com", "2024-01-15");

			// Then
			assertThat(result).isEmpty();
		}

		@ParameterizedTest
		@MethodSource("provideMatchFilteringScenarios")
		@DisplayName("Should correctly filter matches based on server and date combinations")
		void getMatches_VariousScenarios_ShouldFilterCorrectly(
				String serverEndpoint,
				String date,
				int expectedCount,
				String description) {
			// Given
			List<Match> allMatches = List.of(testMatch1, testMatch2, testMatch3);
			when(matchRepository.findAll()).thenReturn(allMatches);

			// When
			List<Match> result = serverService.getMatches(serverEndpoint, date);

			// Then
			assertThat(result).hasSize(expectedCount);
		}

		private static Stream<Arguments> provideMatchFilteringScenarios() {
			return Stream.of(
					Arguments.of("server1.com", "2024-01-15", 2, "Server1 on Jan 15"),
					Arguments.of("server2.com", "2024-01-16", 1, "Server2 on Jan 16"),
					Arguments.of("server1.com", "2024-01-16", 0, "Server1 on different date"),
					Arguments.of("server3.com", "2024-01-15", 0, "Non-existent server")
			);
		}

		// ==================== getStats Tests ====================

		@Test
		@DisplayName("Should return server stats when server exists")
		void getStats_WhenServerExists_ShouldReturnServerStats() {
			// Given
			ServerStatsDto expectedDto = new ServerStatsDto();
			expectedDto.setName("Test Server 1");

			when(serverRepository.findById("server1.com")).thenReturn(Optional.of(testServer1));
			when(serverMapper.toServerStatsDto(testServer1)).thenReturn(expectedDto);

			// When
			ServerStatsDto result = serverService.getStats("server1.com");

			// Then
			assertThat(result).isNotNull();
			assertThat(result.getName()).isEqualTo("Test Server 1");

			verify(serverRepository).findById("server1.com");
			verify(serverMapper).toServerStatsDto(testServer1);
		}

		@ParameterizedTest
		@ValueSource(strings = {"nonexistent.com", "invalid-endpoint", "missing.server"})
		@DisplayName("Should throw EntityNotFoundException when getting stats for non-existent server")
		void getStats_WhenServerDoesNotExist_ShouldThrowEntityNotFoundException(String endpoint) {
			// Given
			when(serverRepository.findById(endpoint)).thenReturn(Optional.empty());

			// When & Then
			assertThatThrownBy(() -> serverService.getStats(endpoint))
					.isInstanceOf(EntityNotFoundException.class)
					.hasMessage("Server with endpoint " + endpoint + " not found");

			verify(serverRepository).findById(endpoint);
			verify(serverMapper, never()).toServerStatsDto(any());
		}

		@Test
		@DisplayName("Should call mapper exactly once when getting stats")
		void getStats_ShouldCallMapperOnce() {
			// Given
			ServerStatsDto expectedDto = new ServerStatsDto();
			when(serverRepository.findById("server1.com")).thenReturn(Optional.of(testServer1));
			when(serverMapper.toServerStatsDto(testServer1)).thenReturn(expectedDto);

			// When
			serverService.getStats("server1.com");

			// Then
			verify(serverMapper, times(1)).toServerStatsDto(testServer1);
		}
	}