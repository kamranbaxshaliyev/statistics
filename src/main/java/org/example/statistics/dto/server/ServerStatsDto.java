package org.example.statistics.dto.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerStatsDto {
	private String name;
	private String region;
	private int matchCount;
	private double rating;
}
