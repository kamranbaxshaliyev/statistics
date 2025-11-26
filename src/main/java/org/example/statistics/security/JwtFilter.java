package org.example.statistics.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.statistics.domain.Session;
import org.example.statistics.repository.SessionRepository;
import org.example.statistics.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final SessionRepository sessionRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		String token = getJwtFromRequest(request);

		if (StringUtils.hasText(token) && jwtUtil.isTokenValid(token)) {
			String username = jwtUtil.extractUsername(token);
			String userType = jwtUtil.extractRole(token);

			String sessionId = jwtUtil.extractSessionId(token);
			Session session = sessionRepository.findByUserName(username).orElse(null);

			if(session == null || !session.getSessionId().equals(sessionId)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Session invalid. Login again.");
				return;
			}

			SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userType);
			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}
}