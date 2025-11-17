package org.example.statistics.unit.service.report;

import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.example.statistics.service.report.ReportServiceImpl;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Unit Tests")
class ReportServiceImplTest {

	@Mock
	private MatchRepository matchRepository;

	@Mock
	private PlayerRepository playerRepository;

	@Mock
	private ServerRepository serverRepository;

	@InjectMocks
	private ReportServiceImpl reportService;

	private Match match1, match2, match3, match4;
	private Player player1, player2, player3, player4;
	private Server server1, server2, server3;

	@BeforeEach
	void setUp() {
		// Setup matches with different timestamps
		match1 = createMatch("match1", LocalDateTime.of(2024, 1, 1, 10, 0));
		match2 = createMatch("match2", LocalDateTime.of(2024, 1, 2, 11, 0));
		match3 = createMatch("match3", LocalDateTime.of(2024, 1, 3, 12, 0));
		match4 = createMatch("match4", LocalDateTime.of(2024, 1, 4, 13, 0));

		// Setup players with different scores
		player1 = createPlayer("Player1", 1000);
		player2 = createPlayer("Player2", 800);
		player3 = createPlayer("Player3", 600);
		player4 = createPlayer("Player4", 400);

		// Setup servers with different match counts
		server1 = createServer("server1.com", List.of("m1", "m2", "m3"));
		server2 = createServer("server2.com", List.of("m4", "m5"));
		server3 = createServer("server3.com", List.of("m6"));
	}

	private Match createMatch(String id, LocalDateTime timestamp) {
		Match match = new Match();
		match.setId(id);
		match.setTimestamp(timestamp);
		return match;
	}

	private Player createPlayer(String name, int totalScore) {
		Player player = new Player();
		player.setName(name);
		player.setTotalScore(totalScore);
		return player;
	}

	private Server createServer(String endpoint, List<String> matchIds) {
		Server server = new Server();
		server.setEndpoint(endpoint);
		server.setMatchIds(matchIds);
		return server;
	}

	// ==================== getRecentMatches Tests ====================

	@Test
	@DisplayName("Should return recent matches sorted by timestamp descending")
	void getRecentMatches_WhenMatchesExist_ShouldReturnSortedByTimestampDesc() {
		// Given
		List<Match> allMatches = List.of(match1, match3, match2, match4);
		when(matchRepository.findAll()).thenReturn(allMatches);

		// When
		List<Match> result = reportService.getRecentMatches(4);

		// Then
		assertThat(result).hasSize(4);
		assertThat(result).containsExactly(match4, match3, match2, match1);
		verify(matchRepository).findAll();
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4})
	@DisplayName("Should limit results to requested count")
	void getRecentMatches_WithDifferentCounts_ShouldLimitResults(int count) {
		// Given
		List<Match> allMatches = List.of(match1, match2, match3, match4);
		when(matchRepository.findAll()).thenReturn(allMatches);

		// When
		List<Match> result = reportService.getRecentMatches(count);

		// Then
		assertThat(result).hasSize(count);
		assertThat(result.get(0)).isEqualTo(match4); // Most recent should be first
	}

	@Test
	@DisplayName("Should return all matches when count is greater than total")
	void getRecentMatches_WhenCountExceedsTotalMatches_ShouldReturnAllMatches() {
		// Given
		List<Match> allMatches = List.of(match1, match2);
		when(matchRepository.findAll()).thenReturn(allMatches);

		// When
		List<Match> result = reportService.getRecentMatches(10);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(match2, match1);
	}

	@Test
	@DisplayName("Should return empty list when no matches exist")
	void getRecentMatches_WhenNoMatchesExist_ShouldReturnEmptyList() {
		// Given
		when(matchRepository.findAll()).thenReturn(Collections.emptyList());

		// When
		List<Match> result = reportService.getRecentMatches(5);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should return empty list when count is zero")
	void getRecentMatches_WhenCountIsZero_ShouldReturnEmptyList() {
		// Given
		List<Match> allMatches = List.of(match1, match2, match3);
		when(matchRepository.findAll()).thenReturn(allMatches);

		// When
		List<Match> result = reportService.getRecentMatches(0);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should handle matches with same timestamp")
	void getRecentMatches_WhenMatchesHaveSameTimestamp_ShouldIncludeAll() {
		// Given
		Match matchA = createMatch("matchA", LocalDateTime.of(2024, 1, 1, 10, 0));
		Match matchB = createMatch("matchB", LocalDateTime.of(2024, 1, 1, 10, 0));
		Match matchC = createMatch("matchC", LocalDateTime.of(2024, 1, 2, 10, 0));

		when(matchRepository.findAll()).thenReturn(List.of(matchA, matchB, matchC));

		// When
		List<Match> result = reportService.getRecentMatches(3);

		// Then
		assertThat(result).hasSize(3);
		assertThat(result.get(0)).isEqualTo(matchC);
	}

	// ==================== getBestPlayers Tests ====================

	@Test
	@DisplayName("Should return players sorted by total score descending")
	void getBestPlayers_WhenPlayersExist_ShouldReturnSortedByScoreDesc() {
		// Given
		List<Player> allPlayers = List.of(player3, player1, player4, player2);
		when(playerRepository.findAll()).thenReturn(allPlayers);

		// When
		List<Player> result = reportService.getBestPlayers(4);

		// Then
		assertThat(result).hasSize(4);
		assertThat(result).containsExactly(player1, player2, player3, player4);
		verify(playerRepository).findAll();
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4})
	@DisplayName("Should limit player results to requested count")
	void getBestPlayers_WithDifferentCounts_ShouldLimitResults(int count) {
		// Given
		List<Player> allPlayers = List.of(player1, player2, player3, player4);
		when(playerRepository.findAll()).thenReturn(allPlayers);

		// When
		List<Player> result = reportService.getBestPlayers(count);

		// Then
		assertThat(result).hasSize(count);
		assertThat(result.get(0)).isEqualTo(player1); // Highest score should be first
	}

	@Test
	@DisplayName("Should return all players when count is greater than total")
	void getBestPlayers_WhenCountExceedsTotalPlayers_ShouldReturnAllPlayers() {
		// Given
		List<Player> allPlayers = List.of(player1, player2);
		when(playerRepository.findAll()).thenReturn(allPlayers);

		// When
		List<Player> result = reportService.getBestPlayers(10);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(player1, player2);
	}

	@Test
	@DisplayName("Should return empty list when no players exist")
	void getBestPlayers_WhenNoPlayersExist_ShouldReturnEmptyList() {
		// Given
		when(playerRepository.findAll()).thenReturn(Collections.emptyList());

		// When
		List<Player> result = reportService.getBestPlayers(5);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should return empty list when count is zero")
	void getBestPlayers_WhenCountIsZero_ShouldReturnEmptyList() {
		// Given
		List<Player> allPlayers = List.of(player1, player2);
		when(playerRepository.findAll()).thenReturn(allPlayers);

		// When
		List<Player> result = reportService.getBestPlayers(0);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should handle players with same score")
	void getBestPlayers_WhenPlayersHaveSameScore_ShouldIncludeAll() {
		// Given
		Player playerA = createPlayer("PlayerA", 500);
		Player playerB = createPlayer("PlayerB", 500);
		Player playerC = createPlayer("PlayerC", 600);

		when(playerRepository.findAll()).thenReturn(List.of(playerA, playerB, playerC));

		// When
		List<Player> result = reportService.getBestPlayers(3);

		// Then
		assertThat(result).hasSize(3);
		assertThat(result.get(0)).isEqualTo(playerC);
	}

	// ==================== getPopularServers Tests ====================

	@Test
	@DisplayName("Should return servers sorted by match count descending")
	void getPopularServers_WhenServersExist_ShouldReturnSortedByMatchCountDesc() {
		// Given
		List<Server> allServers = List.of(server2, server3, server1);
		when(serverRepository.findAll()).thenReturn(allServers);

		// When
		List<Server> result = reportService.getPopularServers(3);

		// Then
		assertThat(result).hasSize(3);
		assertThat(result).containsExactly(server1, server2, server3);
		verify(serverRepository).findAll();
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3})
	@DisplayName("Should limit server results to requested count")
	void getPopularServers_WithDifferentCounts_ShouldLimitResults(int count) {
		// Given
		List<Server> allServers = List.of(server1, server2, server3);
		when(serverRepository.findAll()).thenReturn(allServers);

		// When
		List<Server> result = reportService.getPopularServers(count);

		// Then
		assertThat(result).hasSize(count);
		assertThat(result.get(0)).isEqualTo(server1); // Most matches should be first
	}

	@Test
	@DisplayName("Should return all servers when count is greater than total")
	void getPopularServers_WhenCountExceedsTotalServers_ShouldReturnAllServers() {
		// Given
		List<Server> allServers = List.of(server1, server2);
		when(serverRepository.findAll()).thenReturn(allServers);

		// When
		List<Server> result = reportService.getPopularServers(10);

		// Then
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("Should return empty list when no servers exist")
	void getPopularServers_WhenNoServersExist_ShouldReturnEmptyList() {
		// Given
		when(serverRepository.findAll()).thenReturn(Collections.emptyList());

		// When
		List<Server> result = reportService.getPopularServers(5);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should return empty list when count is zero")
	void getPopularServers_WhenCountIsZero_ShouldReturnEmptyList() {
		// Given
		List<Server> allServers = List.of(server1, server2);
		when(serverRepository.findAll()).thenReturn(allServers);

		// When
		List<Server> result = reportService.getPopularServers(0);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should handle servers with null match IDs")
	void getPopularServers_WhenServerHasNullMatchIds_ShouldTreatAsZero() {
		// Given
		Server serverWithNull = createServer("nullserver.com", null);
		List<Server> allServers = List.of(server1, serverWithNull);
		when(serverRepository.findAll()).thenReturn(allServers);

		// When
		List<Server> result = reportService.getPopularServers(2);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).isEqualTo(server1);
		assertThat(result.get(1)).isEqualTo(serverWithNull);
	}

	@Test
	@DisplayName("Should handle servers with empty match IDs")
	void getPopularServers_WhenServerHasEmptyMatchIds_ShouldTreatAsZero() {
		// Given
		Server serverWithEmpty = createServer("emptyserver.com", Collections.emptyList());
		List<Server> allServers = List.of(server1, serverWithEmpty);
		when(serverRepository.findAll()).thenReturn(allServers);

		// When
		List<Server> result = reportService.getPopularServers(2);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0)).isEqualTo(server1);
		assertThat(result.get(1)).isEqualTo(serverWithEmpty);
	}

	// ==================== Parameterized Tests for All Methods ====================

	@ParameterizedTest
	@MethodSource("provideCountScenarios")
	@DisplayName("Should handle various count scenarios for all report methods")
	void allReportMethods_WithVariousCounts_ShouldHandleCorrectly(int count, int expectedSize) {
		// Given
		when(matchRepository.findAll()).thenReturn(List.of(match1, match2, match3));
		when(playerRepository.findAll()).thenReturn(List.of(player1, player2, player3));
		when(serverRepository.findAll()).thenReturn(List.of(server1, server2, server3));

		// When & Then
		assertThat(reportService.getRecentMatches(count)).hasSize(expectedSize);
		assertThat(reportService.getBestPlayers(count)).hasSize(expectedSize);
		assertThat(reportService.getPopularServers(count)).hasSize(expectedSize);
	}

	private static Stream<Arguments> provideCountScenarios() {
		return Stream.of(
				Arguments.of(0, 0),
				Arguments.of(1, 1),
				Arguments.of(2, 2),
				Arguments.of(3, 3),
				Arguments.of(10, 3)  // Exceeds available items
		);
	}
}