package org.example.statistics.controller.server;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.ServerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
public class ServerControllerImpl implements ServerController {

	private final ServerRepository serverRepository;
	private final MatchRepository matchRepository;

	@Override
	public ResponseEntity<List<Server>> getServers() {
		List<Server> servers = StreamSupport
				.stream(serverRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());
		return ResponseEntity.ok(servers);
	}

	@Override
	public ResponseEntity<Server> getServer(String endpoint) {
		return serverRepository.findById(endpoint)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Override
	public ResponseEntity<List<Match>> getMatches(String endpoint, String timestamp) {
		List<Match> matches = StreamSupport
				.stream(matchRepository.findAll().spliterator(), false)
				.filter(m -> m.getServerEndpoint().equals(endpoint)
						&& m.getTimestamp().toLocalDate().toString().equals(timestamp))
				.collect(Collectors.toList());
		return ResponseEntity.ok(matches);
	}

	@Override
	public ResponseEntity<Map<String, Object>> getStats(String endpoint) {
		return serverRepository.findById(endpoint)
				.map(server -> {
					Map<String, Object> stats = new HashMap<>();
					stats.put("server", server.getName());
					stats.put("region", server.getRegion());
					stats.put("matchCount",
							server.getMatchIds() != null ? server.getMatchIds().size() : 0);
					stats.put("rating", server.getRating());
					return ResponseEntity.ok(stats);
				})
				.orElse(ResponseEntity.notFound().build());
	}
}
