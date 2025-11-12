package org.example.statistics.service.report;

import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;

import java.util.List;

public interface ReportService {
	List<Match> getRecentMatches(Integer count);

	List<Player> getBestPlayers(Integer count);

	List<Server> getPopularServers(Integer count);
}
