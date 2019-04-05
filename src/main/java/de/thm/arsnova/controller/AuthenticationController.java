package de.thm.arsnova.controller;

import de.thm.arsnova.entities.ClientAuthentication;
import de.thm.arsnova.entities.UserProfile;
import de.thm.arsnova.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	private UserService userService;

	public AuthenticationController(final UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/login/guest")
	public ClientAuthentication loginGuest(@RequestParam final String token) {
		userService.authenticate(new UsernamePasswordAuthenticationToken(token, null),
				UserProfile.AuthProvider.ARSNOVA_GUEST);
		return userService.getCurrentClientAuthentication();
	}

	@PostMapping("/login/registered")
	public ClientAuthentication loginRegistered(@RequestParam final String username, @RequestParam final String password) {
		final String loginId = username.toLowerCase();
		userService.authenticate(new UsernamePasswordAuthenticationToken(loginId, password),
				UserProfile.AuthProvider.ARSNOVA);
		return userService.getCurrentClientAuthentication();
	}
}
