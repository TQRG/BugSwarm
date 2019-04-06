/*
 * This file is part of ARSnova Backend.
 * Copyright (C) 2012-2017 The ARSnova Team
 *
 * ARSnova Backend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ARSnova Backend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.thm.arsnova.controller.v2;

import de.thm.arsnova.controller.AbstractController;
import de.thm.arsnova.entities.Statistics;
import de.thm.arsnova.services.StatisticsService;
import de.thm.arsnova.web.CacheControl;
import de.thm.arsnova.web.DeprecatedApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Allows retrieval of several statistics such as the number of active users.
 */
@RestController("v2StatisticsController")
@Api(value = "/statistics", description = "Statistics API")
@RequestMapping("/v2/statistics")
public class StatisticsController extends AbstractController {

	@Autowired
	private StatisticsService statisticsService;

	@ApiOperation(value = "Retrieves global statistics",
			nickname = "getStatistics")
	@RequestMapping(method = RequestMethod.GET, value = "/")
	@CacheControl(maxAge = 60, policy = CacheControl.Policy.PUBLIC)
	public Statistics getStatistics() {
		return statisticsService.getStatistics();
	}

	@ApiOperation(value = "Retrieves the amount of all active users",
			nickname = "countActiveUsers")
	@DeprecatedApi
	@Deprecated
	@RequestMapping(method = RequestMethod.GET, value = "/activeusercount", produces = MediaType.TEXT_PLAIN_VALUE)
	public String countActiveUsers() {
		return String.valueOf(statisticsService.getStatistics().getActiveUsers());
	}

	@ApiOperation(value = "Retrieves the amount of all currently logged in users",
			nickname = "countLoggedInUsers")
	@DeprecatedApi
	@Deprecated
	@RequestMapping(method = RequestMethod.GET, value = "/loggedinusercount", produces = MediaType.TEXT_PLAIN_VALUE)
	public String countLoggedInUsers() {
		return String.valueOf(statisticsService.getStatistics().getLoggedinUsers());
	}

	@ApiOperation(value = "Retrieves the total amount of all sessions",
			nickname = "countSessions")
	@DeprecatedApi
	@Deprecated
	@RequestMapping(method = RequestMethod.GET, value = "/sessioncount", produces = MediaType.TEXT_PLAIN_VALUE)
	public String countSessions() {
		return String.valueOf(statisticsService.getStatistics().getOpenSessions()
				+ statisticsService.getStatistics().getClosedSessions());
	}
}
