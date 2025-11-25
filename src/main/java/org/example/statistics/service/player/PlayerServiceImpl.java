package org.example.statistics.service.player;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Player;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.example.statistics.exception.EntityNotFoundException;
import org.example.statistics.mapper.player.PlayerMapper;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.PlayerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

	private final PlayerRepository playerRepository;

	private final MatchRepository matchRepository;

	private final PlayerMapper playerMapper;

	@Override
	public PlayerStatsDto getStats(String playerName) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		Optional<Player> optionalPlayer = playerRepository.findById(playerName);

		if(optionalPlayer.isEmpty() || !optionalPlayer.get().getName().equals(username)) {
			throw new EntityNotFoundException("Bad request");
		}

		Player player = optionalPlayer.get();
		PlayerStatsDto playerStatsDto = playerMapper.toPlayerStatsDto(player);

		if (!CollectionUtils.isEmpty(player.getMatchIds())) {
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
