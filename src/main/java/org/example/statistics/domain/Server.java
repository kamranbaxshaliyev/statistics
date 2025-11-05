package org.example.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Server")
public class Server implements Serializable
{
	@Id
	private String endpoint;

	private String name;

	private String region;

	private List<String> matchIds;

	private double rating;
}
