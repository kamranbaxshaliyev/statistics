package org.example.statistics.service.server;

import lombok.RequiredArgsConstructor;
import org.example.statistics.domain.Match;
import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;
import org.example.statistics.exception.EntityNotFoundException;
import org.example.statistics.mapper.server.ServerMapper;
import org.example.statistics.repository.MatchRepository;
import org.example.statistics.repository.ServerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

	private final ServerRepository serverRepository;
	private final MatchRepository matchRepository;
	private final ServerMapper serverMapper;

	@Override
	public List<Server> getServers() {
		return StreamSupport
				.stream(serverRepository.findAll().spliterator(), false)
				.toList();
	}

	@Override
	public Server getServer(String endpoint) {
		Optional<Server> optionalServer = serverRepository.findById(endpoint);

		if (optionalServer.isEmpty()) {
			throw new EntityNotFoundException("Server with endpoint " + endpoint + " not found");
		}

		return optionalServer.get();
	}

	@Override
	public List<Match> getMatches(String endpoint, String timestamp) {
		return StreamSupport
				.stream(matchRepository.findAll().spliterator(), false)
				.filter(m -> m.getServerEndpoint().equals(endpoint)
						&& m.getTimestamp().toLocalDate().toString().equals(timestamp))
				.collect(Collectors.toList());
	}

	@Override
	public ServerStatsDto getStats(String endpoint) {
		Optional<Server> optionalServer = serverRepository.findById(endpoint);

		if (optionalServer.isEmpty()) {
			throw new EntityNotFoundException("Server with endpoint " + endpoint + " not found");
		}

		return serverMapper.toServerStatsDto(optionalServer.get());
	}
}
