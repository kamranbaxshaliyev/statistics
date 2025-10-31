package org.example.statistics.controller.server;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Server", description = "Operations related to servers")
@RequestMapping("/servers")
public interface ServerController
{
	@Operation(
			summary = "Get servers",
			description = "Retrieve servers."
	)
	@GetMapping("/info")
	ResponseEntity<?> getServers();

	@Operation(
			summary = "Get server",
			description = "Retrieve server."
	)
	@GetMapping("/{endpoint}/info")
	ResponseEntity<?> getServer(@PathVariable String endpoint);

	@Operation(
			summary = "Get matches for endpoint on timestamp",
			description = "Retrieve matches for endpoint on timestamp."
	)
	@GetMapping("/{endpoint}/matches/{timestamp}")
	ResponseEntity<?> getMatches(@PathVariable String endpoint, @PathVariable String timestamp);

	@Operation(
			summary = "Get endpoint statistics",
			description = "Retrieve detailed statistics for the specified endpoint."
	)
	@GetMapping("/{endpoint}/stats")
	ResponseEntity<?> getStats(@PathVariable String endpoint);
}
