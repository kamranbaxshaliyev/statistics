package org.example.statistics.controller.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportControllerImpl implements ReportController {

	@Override
	public ResponseEntity<?> getRecentMatches(Integer count) {
		if (count == null) {
			count = 5;
		}

		List<String> matches = List.of("Match1", "Match2", "Match3", "Match4", "Match5");
		return ResponseEntity.ok(matches.subList(0, Math.min(count, matches.size())));
	}

	@Override
	public ResponseEntity<?> getBestPlayers(Integer count) {
		if (count == null) {
			count = 5;
		}

		List<String> players = List.of("PlayerA", "PlayerB", "PlayerC", "PlayerD", "PlayerE");
		return ResponseEntity.ok(players.subList(0, Math.min(count, players.size())));
	}

	@Override
	public ResponseEntity<?> getPopularServers(Integer count) {
		if (count == null) {
			count = 5;
		}

		List<String> servers = List.of("Server1", "Server2", "Server3", "Server4", "Server5");
		return ResponseEntity.ok(servers.subList(0, Math.min(count, servers.size())));
	}
}
