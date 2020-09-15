package mujina.api;

import lombok.Getter;
import lombok.Setter;
import mujina.idp.FederatedUserAuthenticationToken;
import mujina.idp.service.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@Component
public class IdpConfiguration extends SharedConfiguration {

  private String defaultEntityId;
  private Map<String, List<String>> attributes = new TreeMap<>();
  //private List<FederatedUserAuthenticationToken> users = new ArrayList<>();
  private String acsEndpoint;
  private AuthenticationMethod authenticationMethod;
  private AuthenticationMethod defaultAuthenticationMethod;
  private final String idpPrivateKey;
  private final String idpCertificate;
  
  //@Autowired
  //private UserRepository userRepository;

  @Autowired
  public IdpConfiguration(JKSKeyManager keyManager,
                          @Value("${idp.entity_id}") String defaultEntityId,
                          @Value("${idp.private_key}") String idpPrivateKey,
                          @Value("${idp.certificate}") String idpCertificate,
                          @Value("${idp.auth_method}") String authMethod) {
    super(keyManager);
    this.defaultEntityId = defaultEntityId;
    this.idpPrivateKey = idpPrivateKey;
    this.idpCertificate = idpCertificate;
    this.defaultAuthenticationMethod = AuthenticationMethod.valueOf(authMethod);
    reset();
  }

  @Override
  public void reset() {
    setEntityId(defaultEntityId);
    resetAttributes();
    resetKeyStore(defaultEntityId, idpPrivateKey, idpCertificate);
    //resetUsers();
    setAcsEndpoint(null);
    setAuthenticationMethod(this.defaultAuthenticationMethod);
    setSignatureAlgorithm(getDefaultSignatureAlgorithm());
  }

/*  private void resetUsers() {
    users.clear();
    users.addAll(Arrays.asList(
      new FederatedUserAuthenticationToken("admin", "secret", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority("ROLE_ADMIN"))),
      new FederatedUserAuthenticationToken("user", "secret", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))));
  }*/

  private void resetAttributes() {
    attributes.clear();
    putAttribute("urn:mace:dir:attribute-def:uid", "");
    putAttribute("urn:mace:dir:attribute-def:cn", "");
    putAttribute("urn:mace:dir:attribute-def:givenName", "");
    putAttribute("urn:mace:dir:attribute-def:sn", "");
    putAttribute("urn:mace:dir:attribute-def:displayName", "");
    putAttribute("urn:mace:dir:attribute-def:mail", "");
    putAttribute("urn:mace:terena.org:attribute-def:schacHomeOrganization", "");
    putAttribute("urn:mace:dir:attribute-def:eduPersonPrincipalName", "");
  }

  private void putAttribute(String key, String... values) {
    this.attributes.put(key, Arrays.asList(values));
  }

}
