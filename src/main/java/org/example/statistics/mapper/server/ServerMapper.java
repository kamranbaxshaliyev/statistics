package org.example.statistics.mapper.server;

import org.example.statistics.domain.Server;
import org.example.statistics.dto.server.ServerStatsDto;
import org.example.statistics.utils.HelperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = HelperUtils.class)
public interface ServerMapper {

	@Mapping(target = "matchCount", source = "server")
	ServerStatsDto toServerStatsDto(Server server);
}
