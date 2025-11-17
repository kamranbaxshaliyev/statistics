package org.example.statistics.service.report;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.example.statistics.utils.HelperUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.example.statistics.utils.HelperUtils.getServerMatchCount;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final MatchRepository matchRepository;
	private final PlayerRepository playerRepository;
	private final ServerRepository serverRepository;

	@Override
	public List<Match> getRecentMatches(Integer count) {
		return StreamSupport.stream(matchRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparing(Match::getTimestamp).reversed())
				.limit(count)
				.toList();
	}

	@Override
	public List<Player> getBestPlayers(Integer count) {
		return StreamSupport.stream(playerRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparingInt(Player::getTotalScore).reversed())
				.limit(count)
				.toList();
	}

	@Override
	public List<Server> getPopularServers(Integer count) {
		return StreamSupport.stream(serverRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparingInt(HelperUtils::getServerMatchCount).reversed())
				.limit(count)
				.toList();
	}
}
