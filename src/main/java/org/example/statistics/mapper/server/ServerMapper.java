package org.example.statistics.mapper.server;

import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServerMapper {

	@Mapping(target = "matchCount",
			expression = "java(server.getMatchIds() != null ? server.getMatchIds().size() : 0)")
	ServerStatsDto toServerStatsDto(Server server);
}
