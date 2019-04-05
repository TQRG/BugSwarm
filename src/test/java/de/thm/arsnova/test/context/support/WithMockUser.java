package de.thm.arsnova.test.context.support;

import de.thm.arsnova.entities.UserProfile;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(
		factory = WithMockUserSecurityContextFactory.class
)
public @interface WithMockUser {
	String value() default "user";

	UserProfile.AuthProvider authProvider() default UserProfile.AuthProvider.ARSNOVA;

	String loginId() default "";

	String userId() default "";

	String[] roles() default {"USER"};

	String password() default "password";
}
