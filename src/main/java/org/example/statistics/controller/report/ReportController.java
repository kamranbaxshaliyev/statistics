package org.example.statistics.controller.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Report", description = "Operations related to game reports")
@RequestMapping("/reports")
public interface ReportController {

	@Operation(summary = "Get recent matches", description = "Retrieve recent matches. Optionally limit by count.")
	@GetMapping("/recent-matches")
	ResponseEntity<?> getRecentMatches(@RequestParam(defaultValue = "5") Integer count);

	@Operation(summary = "Get best players", description = "Retrieve best players. Optionally limit by count.")
	@GetMapping("/best-players")
	ResponseEntity<?> getBestPlayers(@RequestParam(defaultValue = "5") Integer count);

	@Operation(summary = "Get popular servers", description = "Retrieve popular servers. Optionally limit by count.")
	@GetMapping("/popular-servers")
	ResponseEntity<?> getPopularServers(@RequestParam(defaultValue = "5") Integer count);
}
