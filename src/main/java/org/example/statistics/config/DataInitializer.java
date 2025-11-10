package org.example.statistics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Player;
import org.example.statistics.domain.Server;
import org.example.statistics.repository.PlayerRepository;
import org.example.statistics.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

	private final ObjectMapper objectMapper;
	private final ServerRepository serverRepository;
	private final PlayerRepository playerRepository;

	@Value("${data.init.servers}")
	private Resource dataServers;

	@Value("${data.init.players}")
	private Resource dataPlayers;

	@PostConstruct
	public void initData() throws IOException {
		if (serverRepository.count() > 0 || playerRepository.count() > 0) {
			System.out.println("Redis already has data, skipping init.");
			return;
		}

		System.out.println("Initializing data from JSON...");
		List<Server> servers = objectMapper.readValue(
				dataServers.getInputStream(),
				objectMapper.getTypeFactory().constructCollectionType(List.class, Server.class)
		);
		serverRepository.saveAll(servers);

		List<Player> players = objectMapper.readValue(
				dataPlayers.getInputStream(),
				objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class)
		);
		playerRepository.saveAll(players);

		System.out.println("Data initialization completed.");
	}
}
