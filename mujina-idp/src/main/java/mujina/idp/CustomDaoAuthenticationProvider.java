/*package mujina.idp;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import mujina.api.IdpConfiguration;

public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

  private final IdpConfiguration idpConfiguration;

  public CustomDaoAuthenticationProvider(IdpConfiguration idpConfiguration) {
    this.idpConfiguration = idpConfiguration;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Authentication auth = super.authenticate(authentication);
		if (StringUtils.isEmpty(auth.getPrincipal())) {
			throw new InvalidAuthenticationException("Principal may not be empty");
		}
		FederatedUserAuthenticationToken authenticationToken = new FederatedUserAuthenticationToken(
				auth.getPrincipal(), auth.getCredentials(), auth.getAuthorities());

		return authenticationToken.clone();
	}
}
*/