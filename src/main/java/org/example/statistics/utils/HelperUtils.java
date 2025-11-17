package org.example.statistics.utils;

import org.example.statistics.domain.Server;
import org.springframework.stereotype.Component;

@Component
public class HelperUtils {

	public static int getServerMatchCount(Server server) {
		return server.getMatchIds() != null ? server.getMatchIds().size() : 0;
	}
}
