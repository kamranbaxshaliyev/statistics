package org.example.statistics.service.player;

import org.example.statistics.dto.player.PlayerStatsDto;

public interface PlayerService {
	PlayerStatsDto getStats(String playerName);
}
