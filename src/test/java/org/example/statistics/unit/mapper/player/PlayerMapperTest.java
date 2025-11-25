package org.example.statistics.unit.mapper.player;

import org.example.statistics.domain.Player;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.example.statistics.mapper.player.PlayerMapper;
import org.example.statistics.mapper.player.PlayerMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlayerMapper Unit Tests")
class PlayerMapperTest {

	private PlayerMapper playerMapper;
	private Player testPlayer;

	@BeforeEach
	void setUp() {
		// Create the mapper instance directly (no Spring context)
		playerMapper = new PlayerMapperImpl();

		testPlayer = new Player();
		testPlayer.setName("TestPlayer");
		testPlayer.setTotalScore(1500);
		testPlayer.setWinRate(75);
	}

	@Test
	@DisplayName("Should map Player to PlayerStatsDto correctly")
	void toPlayerStatsDto_WithValidPlayer_ShouldMapCorrectly() {
		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("TestPlayer");
		assertThat(result.getTotalScore()).isEqualTo("1500");
		assertThat(result.getWinRate()).isEqualTo("75%");
	}

	@ParameterizedTest
	@CsvSource({
			"0, 0%",
			"25, 25%",
			"50, 50%",
			"75, 75%",
			"100, 100%"
	})
	@DisplayName("Should correctly format different win rate values")
	void toPlayerStatsDto_WithDifferentWinRates_ShouldFormatCorrectly(
			int winRate,
			String expectedFormatted) {
		// Given
		testPlayer.setWinRate(winRate);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getWinRate()).isEqualTo(expectedFormatted);
	}

	@Test
	@DisplayName("Should handle null player name")
	void toPlayerStatsDto_WithNullName_ShouldMapNull() {
		// Given
		testPlayer.setName(null);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getName()).isNull();
	}

	@Test
	@DisplayName("Should return null when player is null")
	void toPlayerStatsDto_WithNullPlayer_ShouldReturnNull() {
		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(null);

		// Then
		assertThat(result).isNull();
	}
}
