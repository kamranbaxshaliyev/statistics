package org.example.statistics.dto.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.statistics.domain.Match;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatsDto {
	private String name;
	private String totalScore;
	private int matchesPlayed;
	private String winRate;
	private List<Match> recentMatches;
}
