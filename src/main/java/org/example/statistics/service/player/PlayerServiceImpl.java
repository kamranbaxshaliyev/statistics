package org.example.statistics.service.player;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.example.statistics.mapper.player.PlayerMapper;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

	private final PlayerRepository playerRepository;

	private final MatchRepository matchRepository;

	private final PlayerMapper playerMapper;

	@Override
	public PlayerStatsDto getStats(String name) {
		Player player = playerRepository.findById(name).orElse(null);

		if (player == null) {
			return null;
		}

		PlayerStatsDto playerStatsDto = playerMapper.toPlayerStatsDto(player);

		if (player.getMatchIds() != null && !player.getMatchIds().isEmpty()) {
			List<Match> recentMatches = player.getMatchIds().stream()
					.map(matchRepository::findById)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.toList();

			playerStatsDto.setRecentMatches(recentMatches);
		}
		else {
			playerStatsDto.setRecentMatches(List.of());
		}

		return playerStatsDto;
	}
}
