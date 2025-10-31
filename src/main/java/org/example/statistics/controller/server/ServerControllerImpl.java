package org.example.statistics.controller.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerControllerImpl implements ServerController {
	@Override
	public ResponseEntity<?> getServers() {
		return ResponseEntity.ok("Servers");
	}

	@Override
	public ResponseEntity<?> getServer(String endpoint) {
		return ResponseEntity.ok("Server for endpoint: " + endpoint);
	}

	@Override
	public ResponseEntity<?> getMatches(String endpoint, String timestamp) {
		return ResponseEntity.ok("Matches for endpoint: " + endpoint + ", on timestamp: " + timestamp);
	}

	@Override
	public ResponseEntity<?> getStats(String endpoint) {
		return ResponseEntity.ok("Stats for endpoint: " + endpoint);
	}
}
