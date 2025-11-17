package org.example.statistics.mapper.player;

import org.example.statistics.domain.Player;
import org.example.statistics.dto.player.PlayerStatsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

	@Mapping(target = "winRate", expression = "java(player.getWinRate() + \"%\")")
	@Mapping(target = "recentMatches", ignore = true)
	PlayerStatsDto toPlayerStatsDto(Player player);
}
