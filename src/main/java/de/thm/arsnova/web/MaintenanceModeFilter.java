package de.thm.arsnova.web;

import de.thm.arsnova.services.StatusService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MaintenanceModeFilter extends OncePerRequestFilter {
	private StatusService statusService;

	public MaintenanceModeFilter(final StatusService statusService) {
		this.statusService = statusService;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse, final FilterChain filterChain) throws ServletException, IOException {
		if (statusService.isMaintenanceActive()) {
			httpServletResponse.setStatus(503);
			return;
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
