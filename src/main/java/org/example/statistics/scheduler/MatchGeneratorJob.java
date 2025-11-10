package org.example.statistics.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MatchGeneratorJob {

	private final MatchRepository matchRepository;
	private final ServerRepository serverRepository;
	private final PlayerRepository playerRepository;

	private final Random random = new Random();

	@Scheduled(cron = "${match.generator.cron}")
	public void generateRandomMatch() {
		List<Server> servers = new ArrayList<>();
		serverRepository.findAll().forEach(servers::add);

		List<Player> players = new ArrayList<>();
		playerRepository.findAll().forEach(players::add);

		if (servers.isEmpty() || players.size() < 2) {
			System.out.println("Not enough data for match generation.");
			return;
		}

		// Select server
		Server server = servers.get(random.nextInt(servers.size()));

		// Select player
		Player p1 = players.get(random.nextInt(players.size()));
		Player p2;

		do {
			p2 = players.get(random.nextInt(players.size()));
		}
		while (p1.equals(p2));

		int scoreBound = 21;
		int score1 = random.nextInt(scoreBound);
		int score2 = random.nextInt(scoreBound);

		String winner = score1 >= score2 ? p1.getName() : p2.getName();

		Map<String, Integer> scores = new LinkedHashMap<>();
		scores.put(p1.getName(), score1);
		scores.put(p2.getName(), score2);

		Match match = Match.builder()
				.id(UUID.randomUUID().toString())
				.serverEndpoint(server.getEndpoint())
				.timestamp(LocalDateTime.now())
				.playerScores(scores)
				.build();

		matchRepository.save(match);

		if (server.getMatchIds() == null) {
			server.setMatchIds(new ArrayList<>());
		}

		server.getMatchIds().add(match.getId());
		serverRepository.save(server);

		for (Player player : List.of(p1, p2)) {
			if (player.getMatchIds() == null) {
				player.setMatchIds(new ArrayList<>());
			}

			player.getMatchIds().add(match.getId());
			player.setMatchesPlayed(player.getMatchesPlayed() + 1);

			int earnedPoints = player.getName().equals(winner) ? 100 : 30;
			player.setTotalScore(player.getTotalScore() + earnedPoints);

			int matchesBefore = player.getMatchesPlayed();
			double winsBefore = player.getWinRate() * matchesBefore / 100.0;

			boolean isWinner = player.getName().equals(winner);

			int matchesAfter = matchesBefore + 1;
			double winsAfter = winsBefore + (isWinner ? 1.0 : 0.0);

			double newWinRate = (winsAfter / matchesAfter) * 100.0;

			newWinRate = Math.min(newWinRate, 100.0);
			newWinRate = Math.max(newWinRate, 0.0);

			player.setMatchesPlayed(matchesAfter);
			player.setWinRate((int) Math.round(newWinRate));

			playerRepository.save(player);
		}

		System.out.printf("New match generated on %s between %s (%d) and %s (%d). Winner: %s%n",
				server.getEndpoint(), p1.getName(), score1, p2.getName(), score2, winner);
	}
}
