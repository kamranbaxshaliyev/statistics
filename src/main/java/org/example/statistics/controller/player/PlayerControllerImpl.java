package org.example.statistics.controller.player;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerControllerImpl implements PlayerController
{
	@Override
	public ResponseEntity<?> getStats(String name)
	{
		return ResponseEntity.ok("Stats for player: " + name);
	}
}
