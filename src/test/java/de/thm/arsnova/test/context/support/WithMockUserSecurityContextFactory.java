package de.thm.arsnova.test.context.support;

import de.thm.arsnova.entities.UserProfile;
import de.thm.arsnova.security.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockUser> {
	@Override
	public SecurityContext createSecurityContext(final WithMockUser withMockUser) {
		String loginId = StringUtils.hasLength(withMockUser.loginId()) ? withMockUser.loginId() : withMockUser.value();
		UserProfile userProfile = new UserProfile(withMockUser.authProvider(), loginId);
		userProfile.setId(!withMockUser.userId().isEmpty() ? withMockUser.userId() : loginId);
		User user = new User(userProfile, Arrays.stream(withMockUser.roles())
				.map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList()));
		Authentication authentication = new UsernamePasswordAuthenticationToken(user, withMockUser.password(), user.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);

		return context;
	}
}
