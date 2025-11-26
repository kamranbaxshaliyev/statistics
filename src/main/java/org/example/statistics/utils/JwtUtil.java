package org.example.statistics.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

	private final String SECRET = "secretKey";
	private final Algorithm algorithm = Algorithm.HMAC256(SECRET);
	private final long EXPIRATION_TIME = 1000 * 60 * 60;

	public String generateToken(String username, String role, String sessionId) {
		return JWT.create()
				.withSubject(username)
				.withClaim("role", role)
				.withClaim("sessionId", sessionId)
				.withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.sign(algorithm);
	}

	public DecodedJWT decode(String token) {
		return JWT.require(algorithm).build().verify(token);
	}

	public boolean isTokenValid(String token) {
		try {
			return !isExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

	public String extractUsername(String token) {
		return decode(token).getSubject();
	}

	public String extractRole(String token) {
		return decode(token).getClaim("role").asString();
	}

	public String extractSessionId(String token) {
		return decode(token).getClaim("sessionId").asString();
	}

	public boolean isExpired(String token) {
		return decode(token).getExpiresAt().before(new Date());
	}

}
