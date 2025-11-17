package org.example.statistics.unit.service.player;

import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.example.statistics.exception.EntityNotFoundException;
import org.example.statistics.mapper.player.PlayerMapper;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.service.player.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlayerService Unit Tests")
class PlayerServiceImplTest {

	@Mock
	private PlayerRepository playerRepository;

	@Mock
	private MatchRepository matchRepository;

	@Mock
	private PlayerMapper playerMapper;

	@InjectMocks
	private PlayerServiceImpl playerService;

	private Player testPlayer;
	private PlayerStatsDto testPlayerStatsDto;
	private Match testMatch1;
	private Match testMatch2;

	@BeforeEach
	void setUp() {
		testPlayer = new Player();
		testPlayer.setName("TestPlayer");
		testPlayer.setTotalScore(1000);
		testPlayer.setWinRate(75);

		testPlayerStatsDto = new PlayerStatsDto();
		testPlayerStatsDto.setName("TestPlayer");
		testPlayerStatsDto.setTotalScore("1000");
		testPlayerStatsDto.setWinRate("75.5%");

		testMatch1 = new Match();
		testMatch1.setId("match1");

		testMatch2 = new Match();
		testMatch2.setId("match2");
	}

	@Test
	@DisplayName("Should return player stats when player exists with matches")
	void getStats_WhenPlayerExistsWithMatches_ShouldReturnPlayerStatsWithMatches() {
		// Given
		List<String> matchIds = List.of("match1", "match2");
		testPlayer.setMatchIds(matchIds);

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);
		when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch1));
		when(matchRepository.findById("match2")).thenReturn(Optional.of(testMatch2));

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("TestPlayer");
		assertThat(result.getRecentMatches()).hasSize(2);
		assertThat(result.getRecentMatches()).containsExactly(testMatch1, testMatch2);

		verify(playerRepository).findById("TestPlayer");
		verify(playerMapper).toPlayerStatsDto(testPlayer);
		verify(matchRepository, times(2)).findById(anyString());
	}

	@Test
	@DisplayName("Should return player stats with empty matches when player has no match IDs")
	void getStats_WhenPlayerHasNoMatchIds_ShouldReturnPlayerStatsWithEmptyMatches() {
		// Given
		testPlayer.setMatchIds(null);

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("TestPlayer");
		assertThat(result.getRecentMatches()).isEmpty();

		verify(playerRepository).findById("TestPlayer");
		verify(playerMapper).toPlayerStatsDto(testPlayer);
		verify(matchRepository, never()).findById(anyString());
	}

	@Test
	@DisplayName("Should return player stats with empty matches when player has empty match IDs list")
	void getStats_WhenPlayerHasEmptyMatchIdsList_ShouldReturnPlayerStatsWithEmptyMatches() {
		// Given
		testPlayer.setMatchIds(Collections.emptyList());

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getRecentMatches()).isEmpty();

		verify(playerRepository).findById("TestPlayer");
		verify(playerMapper).toPlayerStatsDto(testPlayer);
		verify(matchRepository, never()).findById(anyString());
	}

	@Test
	@DisplayName("Should filter out matches that don't exist")
	void getStats_WhenSomeMatchesDoNotExist_ShouldFilterThemOut() {
		// Given
		List<String> matchIds = List.of("match1", "nonexistent", "match2");
		testPlayer.setMatchIds(matchIds);

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);
		when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch1));
		when(matchRepository.findById("nonexistent")).thenReturn(Optional.empty());
		when(matchRepository.findById("match2")).thenReturn(Optional.of(testMatch2));

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getRecentMatches()).hasSize(2);
		assertThat(result.getRecentMatches()).containsExactly(testMatch1, testMatch2);

		verify(matchRepository, times(3)).findById(anyString());
	}

	@Test
	@DisplayName("Should throw EntityNotFoundException when player does not exist")
	void getStats_WhenPlayerDoesNotExist_ShouldThrowEntityNotFoundException() {
		// Given
		when(playerRepository.findById("NonExistentPlayer")).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> playerService.getStats("NonExistentPlayer"))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage("Player with name NonExistentPlayer not found");

		verify(playerRepository).findById("NonExistentPlayer");
		verify(playerMapper, never()).toPlayerStatsDto(any());
		verify(matchRepository, never()).findById(anyString());
	}

	@Test
	@DisplayName("Should handle player with matches list but all matches are missing")
	void getStats_WhenAllMatchesAreMissing_ShouldReturnEmptyMatchesList() {
		// Given
		List<String> matchIds = List.of("missing1", "missing2");
		testPlayer.setMatchIds(matchIds);

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);
		when(matchRepository.findById("missing1")).thenReturn(Optional.empty());
		when(matchRepository.findById("missing2")).thenReturn(Optional.empty());

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getRecentMatches()).isEmpty();

		verify(matchRepository, times(2)).findById(anyString());
	}

	@Test
	@DisplayName("Should handle player with single match")
	void getStats_WhenPlayerHasSingleMatch_ShouldReturnSingleMatch() {
		// Given
		List<String> matchIds = List.of("match1");
		testPlayer.setMatchIds(matchIds);

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);
		when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch1));

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getRecentMatches()).hasSize(1);
		assertThat(result.getRecentMatches()).containsExactly(testMatch1);
	}

	@Test
	@DisplayName("Should preserve match order from player's match IDs")
	void getStats_ShouldPreserveMatchOrder() {
		// Given
		List<String> matchIds = List.of("match2", "match1");
		testPlayer.setMatchIds(matchIds);

		when(playerRepository.findById("TestPlayer")).thenReturn(Optional.of(testPlayer));
		when(playerMapper.toPlayerStatsDto(testPlayer)).thenReturn(testPlayerStatsDto);
		when(matchRepository.findById("match2")).thenReturn(Optional.of(testMatch2));
		when(matchRepository.findById("match1")).thenReturn(Optional.of(testMatch1));

		// When
		PlayerStatsDto result = playerService.getStats("TestPlayer");

		// Then
		assertThat(result.getRecentMatches()).containsExactly(testMatch2, testMatch1);
	}
}