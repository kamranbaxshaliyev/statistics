package org.example.statistics.unit.mapper.player;

import org.example.statistics.domain.Player;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.example.statistics.mapper.player.PlayerMapper;
import org.example.statistics.mapper.player.PlayerMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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

	@Test
	@DisplayName("Should append percentage sign to win rate")
	void toPlayerStatsDto_ShouldAppendPercentageToWinRate() {
		// Given
		testPlayer.setWinRate(50);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getWinRate()).isEqualTo("50%");
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
	@DisplayName("Should handle player with zero score")
	void toPlayerStatsDto_WithZeroScore_ShouldMapCorrectly() {
		// Given
		testPlayer.setTotalScore(0);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getTotalScore()).isEqualTo("0");
	}

	@Test
	@DisplayName("Should handle player with negative score")
	void toPlayerStatsDto_WithNegativeScore_ShouldMapCorrectly() {
		// Given
		testPlayer.setTotalScore(-100);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getTotalScore()).isEqualTo("-100");
	}

	@Test
	@DisplayName("Should handle player with very high score")
	void toPlayerStatsDto_WithHighScore_ShouldMapCorrectly() {
		// Given
		testPlayer.setTotalScore(999999);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getTotalScore()).isEqualTo("999999");
	}

	@Test
	@DisplayName("Should handle player with decimal win rate")
	void toPlayerStatsDto_WithDecimalWinRate_ShouldPreserveDecimals() {
		// Given
		testPlayer.setWinRate(66);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getWinRate()).isEqualTo("66%");
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
	@DisplayName("Should handle empty player name")
	void toPlayerStatsDto_WithEmptyName_ShouldMapEmpty() {
		// Given
		testPlayer.setName("");

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getName()).isEmpty();
	}

	@Test
	@DisplayName("Should handle player with special characters in name")
	void toPlayerStatsDto_WithSpecialCharactersInName_ShouldMapCorrectly() {
		// Given
		testPlayer.setName("Player_123!@#");

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(testPlayer);

		// Then
		assertThat(result.getName()).isEqualTo("Player_123!@#");
	}

	@ParameterizedTest
	@MethodSource("provideCompletePlayerScenarios")
	@DisplayName("Should map complete player objects correctly")
	void toPlayerStatsDto_WithCompleteScenarios_ShouldMapAllFieldsCorrectly(
			String name,
			String totalScore,
			int winRate,
			String expectedWinRate) {
		// Given
		Player player = new Player();
		player.setName(name);
		player.setTotalScore(Integer.parseInt(totalScore));
		player.setWinRate(winRate);

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(player);

		// Then
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getTotalScore()).isEqualTo(totalScore);
		assertThat(result.getWinRate()).isEqualTo(expectedWinRate);
	}

	private static Stream<Arguments> provideCompletePlayerScenarios() {
		return Stream.of(
				Arguments.of("Beginner", "0", 0, "0%"),
				Arguments.of("Intermediate", "5000", 50, "50%"),
				Arguments.of("Advanced", "10000", 75, "75%"),
				Arguments.of("Pro", "50000", 95, "95%"),
				Arguments.of("Champion", "100000", 99, "99%")
		);
	}

	@Test
	@DisplayName("Should return null when player is null")
	void toPlayerStatsDto_WithNullPlayer_ShouldReturnNull() {
		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(null);

		// Then
		assertThat(result).isNull();
	}

	@Test
	@DisplayName("Should handle player with all null fields")
	void toPlayerStatsDto_WithAllNullFields_ShouldMapNulls() {
		// Given
		Player player = new Player();
		// All fields are null by default

		// When
		PlayerStatsDto result = playerMapper.toPlayerStatsDto(player);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isNull();
	}
}