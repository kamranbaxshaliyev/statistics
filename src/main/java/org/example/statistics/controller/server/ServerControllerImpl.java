package org.example.statistics.controller.server;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;
import org.example.statistics.service.server.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerControllerImpl implements ServerController {

	private final ServerService serverService;

	@Override
	public ResponseEntity<List<Server>> getServers() {
		List<Server> servers = serverService.getServers();
		return ResponseEntity.ok(servers);
	}

	@Override
	public ResponseEntity<Server> getServer(String endpoint) {
		Server server = serverService.getServer(endpoint);

		if (server == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(server);
	}

	@Override
	public ResponseEntity<List<Match>> getMatches(String endpoint, String timestamp) {
		List<Match> matches = serverService.getMatches(endpoint, timestamp);
		return ResponseEntity.ok(matches);
	}

	@Override
	public ResponseEntity<ServerStatsDto> getStats(String endpoint) {
		ServerStatsDto stats = serverService.getStats(endpoint);
		return ResponseEntity.ok(stats);
	}
}
