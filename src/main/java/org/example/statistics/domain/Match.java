package org.example.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Match")
public class Match implements Serializable {
	@Id
	private String id;

	private String serverEndpoint;

	private LocalDateTime timestamp;

	private Map<String, Integer> playerScores;
}
