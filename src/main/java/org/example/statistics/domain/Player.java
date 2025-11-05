package org.example.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("Player")
public class Player {
	@Id
    private String name;

	private int totalScore;

	private int matchesPlayed;

	private int winRate;

	private List<String> matchIds;
}
