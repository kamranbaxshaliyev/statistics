package org.example.statistics.service.report;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final MatchRepository matchRepository;
	private final PlayerRepository playerRepository;
	private final ServerRepository serverRepository;

	@Override
	public List<Match> getRecentMatches(Integer count) {
		List<Match> matches = StreamSupport.stream(matchRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparing(Match::getTimestamp).reversed())
				.limit(count)
				.toList();

		return matches;
	}

	@Override
	public List<Player> getBestPlayers(Integer count) {
		List<Player> players = StreamSupport.stream(playerRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparingInt(Player::getTotalScore).reversed())
				.limit(count)
				.toList();

		return players;
	}

	@Override
	public List<Server> getPopularServers(Integer count) {
		List<Server> servers = StreamSupport.stream(serverRepository.findAll().spliterator(), false)
				.sorted(Comparator.comparingInt((Server s) ->
						s.getMatchIds() != null ? s.getMatchIds().size() : 0
				).reversed())
				.limit(count)
				.toList();

		return servers;
	}
}
