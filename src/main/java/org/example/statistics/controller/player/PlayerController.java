package org.example.statistics.controller.player;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Player", description = "Operations related to players")
@RequestMapping("/players")
public interface PlayerController
{
	@Operation(
			summary = "Get player statistics",
			description = "Retrieve detailed statistics for the specified player by name."
	)
	@GetMapping("/{playerName}/stats")
	ResponseEntity<PlayerStatsDto> getStats(@PathVariable String playerName);
}
