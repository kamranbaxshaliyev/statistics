package org.example.statistics.service.server;

import org.example.statistics.domain.Match;
import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;

import java.util.List;

public interface ServerService {
	List<Server> getServers();

	Server getServer(String endpoint);

	List<Match>  getMatches(String endpoint, String timestamp);

	ServerStatsDto getStats(String endpoint);
}
