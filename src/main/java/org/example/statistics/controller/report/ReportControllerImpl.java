package org.example.statistics.controller.report;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {

	private final MatchRepository matchRepository;
	private final PlayerRepository playerRepository;
	private final ServerRepository serverRepository;

	@Override
	public ResponseEntity<List<Match>> getRecentMatches(Integer count) {
		List<Match> allMatches = StreamSupport.stream(matchRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparing(Match::getTimestamp).reversed())
				.limit(count)
				.collect(Collectors.toList());

		return ResponseEntity.ok(allMatches);
	}

	@Override
	public ResponseEntity<List<Map<String, Object>>> getBestPlayers(Integer count) {
		List<Player> allPlayers = StreamSupport.stream(playerRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparingInt(Player::getTotalScore).reversed())
				.limit(count)
				.toList();

		List<Map<String, Object>> bestPlayers = allPlayers.stream()
				.map(p -> {
					Map<String, Object> map = new HashMap<>();
					map.put("name", p.getName());
					map.put("totalScore", p.getTotalScore());
					map.put("winRate", p.getWinRate() + "%");
					return map;
				})
				.collect(Collectors.toList());

		return ResponseEntity.ok(bestPlayers);
	}

	@Override
	public ResponseEntity<List<Map<String, Object>>> getPopularServers(Integer count) {
		List<Server> allServers = StreamSupport.stream(serverRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparingInt((Server s) ->
						s.getMatchIds() != null ? s.getMatchIds().size() : 0
				).reversed())
				.limit(count)
				.toList();

		List<Map<String, Object>> popularServers = allServers.stream()
				.map(s -> Map.<String, Object>of(
						"endpoint", s.getEndpoint(),
						"name", s.getName(),
						"region", s.getRegion(),
						"matchesPlayed", s.getMatchIds() != null ? s.getMatchIds().size() : 0,
						"rating", s.getRating()
				))
				.collect(Collectors.toList());

		return ResponseEntity.ok(popularServers);
	}
}
