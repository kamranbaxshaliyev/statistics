package org.example.statistics.integration;

import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.example.statistics.scheduler.MatchGeneratorJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataInitializationIntegrationTest {

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private ServerRepository serverRepository;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private MatchGeneratorJob matchGeneratorJob;

	@Test
	void shouldLoadInitialPlayersIntoRedis() {

		List<Player> players = new ArrayList<>();
		playerRepository.findAll().forEach(players::add);

		assertThat(players).hasSize(3);

		assertThat(players).anyMatch(p -> p.getName().equals("PlayerOne"));
		assertThat(players).anyMatch(p -> p.getName().equals("PlayerTwo"));
		assertThat(players).anyMatch(p -> p.getName().equals("PlayerThree"));

		players.forEach(p -> {
			assertThat(p.getTotalScore()).isNotNull();
			assertThat(p.getMatchesPlayed()).isNotNull();
			assertThat(p.getWinRate()).isNotNull();
		});
	}

	@Test
	void shouldLoadInitialServersIntoRedis() {

		List<Server> servers = new ArrayList<>();
		serverRepository.findAll().forEach(servers::add);

		assertThat(servers).hasSize(2);

		assertThat(servers)
				.anySatisfy(s -> {
					assertThat(s.getEndpoint()).isEqualTo("eu-1.game.net");
					assertThat(s.getName()).isEqualTo("Europe Alpha");
					assertThat(s.getRating()).isEqualTo(4.5);
				});
	}

	@Test
	void shouldGenerateMatchAndUpdateRedisAfterCron() {
		List<Match> matchesBefore = new ArrayList<>();
		matchRepository.findAll().forEach(matchesBefore::add);

		assertThat(matchesBefore).isEmpty();

		matchGeneratorJob.generateRandomMatch();

		List<Match> matchesAfter = new ArrayList<>();
		matchRepository.findAll().forEach(matchesAfter::add);

		assertThat(matchesAfter).hasSize(1);

		Match generated = matchesAfter.getFirst();
		assertThat(generated.getPlayerScores()).hasSize(2);
		assertThat(generated.getServerEndpoint()).isNotBlank();

		Server server = serverRepository.findById(generated.getServerEndpoint()).orElse(null);
		assertThat(server).isNotNull();
		assertThat(server.getMatchIds()).contains(generated.getId());

		List<Player> playersAfter = new ArrayList<>();
		playerRepository.findAll().forEach(playersAfter::add);

		long playersUpdated = playersAfter.stream()
				.filter(p -> p.getMatchIds() != null && p.getMatchIds().contains(generated.getId()))
				.count();

		assertThat(playersUpdated).isEqualTo(2);
	}
}
