package org.example.statistics.controller.player;

import lombok.RequiredArgsConstructor;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.example.statistics.service.player.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlayerControllerImpl implements PlayerController {

	private final PlayerService playerService;

	@Override
	public ResponseEntity<PlayerStatsDto> getStats(String playerName) {
		return ResponseEntity.ok(playerService.getStats(playerName));
	}
}
