package org.example.statistics.controller.player;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PlayerControllerImpl implements PlayerController {

	private final PlayerRepository playerRepository;
	private final MatchRepository matchRepository;

	@Override
	public ResponseEntity<Map<String, Object>> getStats(String name) {
		return playerRepository.findById(name)
				.map(player -> {
					Map<String, Object> stats = new HashMap<>();
					stats.put("name", player.getName());
					stats.put("totalScore", player.getTotalScore());
					stats.put("matchesPlayed", player.getMatchesPlayed());
					stats.put("winRate", player.getWinRate() + "%");

					if (player.getMatchIds() != null && !player.getMatchIds().isEmpty()) {
						List<Match> recentMatches = player.getMatchIds().stream()
								.map(matchRepository::findById)
								.filter(Optional::isPresent)
								.map(Optional::get)
								.collect(Collectors.toList());

						stats.put("recentMatches", recentMatches);
					} else {
						stats.put("recentMatches", List.of());
					}

					return ResponseEntity.ok(stats);
				})
				.orElse(ResponseEntity.notFound().build());
	}
}
