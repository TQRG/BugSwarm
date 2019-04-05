package org.stagemonitor.web.servlet.filter;

import org.stagemonitor.configuration.ConfigurationOption;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.web.servlet.ServletPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Disables all endpoints under /stagemonitor/*  (except /stagemonitor/public/*)
 * if 'stagemonitor.web.widget.enabled' is set to 'false' unless the
 * the header 'X-Stagemonitor-Show-Widget' is provided with the correct 'stagemonitor.password' as value or
 * stagemonitor.password is set to a empty string. Thus it is possible to deactivate the widget for unauthorized users
 * but still having the option to activate it for authorized users.
 * <p>
 * You can use a browser extenstion like Modify Headers to automatically insert the header on each request to your application.
 * <p>
 * If you deactivate stagemonitor's built in security by setting stagemonitor.password to an empty string, make sure to
 * secure the endpoints otherwise. For example with spring security.
 * <p>
 * For custom control whether the in browser widget should be displayed, set the request attribute
 * 'X-Stagemonitor-Show-Widget' with a Boolean value.
 */
public class StagemonitorSecurityFilter extends AbstractExclusionFilter {

	private final ServletPlugin servletPlugin;
	private final ConfigurationRegistry configuration;

	public StagemonitorSecurityFilter() {
		this(Stagemonitor.getConfiguration());
	}

	public StagemonitorSecurityFilter(ConfigurationRegistry configuration) {
		super(ConfigurationOption.stringsOption().buildWithDefault(Arrays.asList("/stagemonitor/public", "/stagemonitor/configuration")),
				ConfigurationOption.stringsOption().buildWithDefault(Collections.<String>emptyList()));
		this.configuration = configuration;
		this.servletPlugin = configuration.getConfig(ServletPlugin.class);
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (!servletPlugin.isWidgetAndStagemonitorEndpointsAllowed(request, configuration)) {
			// let's pretend as if stagemonitor is not there to not unnecessarily leak information
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		chain.doFilter(request, response);
	}
}
