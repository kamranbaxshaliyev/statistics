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
		return ResponseEntity.ok(serverService.getServers());
	}

	@Override
	public ResponseEntity<Server> getServer(String endpoint) {
		return ResponseEntity.ok(serverService.getServer(endpoint));
	}

	@Override
	public ResponseEntity<List<Match>> getMatches(String endpoint, String timestamp) {
		return ResponseEntity.ok(serverService.getMatches(endpoint, timestamp));
	}

	@Override
	public ResponseEntity<ServerStatsDto> getStats(String endpoint) {
		return ResponseEntity.ok(serverService.getStats(endpoint));
	}
}
