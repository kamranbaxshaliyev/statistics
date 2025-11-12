package org.example.statistics.controller.report;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.service.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {

	private final ReportService reportService;

	@Override
	public ResponseEntity<List<Match>> getRecentMatches(Integer count) {
		List<Match> matches = reportService.getRecentMatches(count);

		return ResponseEntity.ok(matches);
	}

	@Override
	public ResponseEntity<List<Player>> getBestPlayers(Integer count) {
		List<Player> players = reportService.getBestPlayers(count);

		return ResponseEntity.ok(players);
	}

	@Override
	public ResponseEntity<List<Server>> getPopularServers(Integer count) {
		List<Server> servers = reportService.getPopularServers(count);

		return ResponseEntity.ok(servers);
	}
}
