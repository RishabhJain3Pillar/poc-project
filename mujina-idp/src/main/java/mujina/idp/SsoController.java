package mujina.idp;

import mujina.api.IdpConfiguration;
import mujina.idp.service.User;
import mujina.idp.service.UserRepository;
import mujina.saml.SAMLAttribute;
import mujina.saml.SAMLPrincipal;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Controller
public class SsoController {

  @Autowired
  private SAMLMessageHandler samlMessageHandler;

  @Autowired
  private IdpConfiguration idpConfiguration;
  
  @Autowired
  private UserRepository userRepository;

  @GetMapping("/SingleSignOnService")
  public void singleSignOnServiceGet(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, MarshallingException, SignatureException, MessageEncodingException, ValidationException, SecurityException, MessageDecodingException, MetadataProviderException, ServletException {
    doSSO(request, response, authentication, false);
  }

  @PostMapping("/SingleSignOnService")
  public void singleSignOnServicePost(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, MarshallingException, SignatureException, MessageEncodingException, ValidationException, SecurityException, MessageDecodingException, MetadataProviderException, ServletException {
    doSSO(request, response, authentication, true);
  }

  @SuppressWarnings("unchecked")
  private void doSSO(HttpServletRequest request, HttpServletResponse response, Authentication authentication, boolean postRequest) throws ValidationException, SecurityException, MessageDecodingException, MarshallingException, SignatureException, MessageEncodingException, MetadataProviderException, IOException, ServletException {
    SAMLMessageContext messageContext = samlMessageHandler.extractSAMLMessageContext(request, response, postRequest);
    AuthnRequest authnRequest = (AuthnRequest) messageContext.getInboundSAMLMessage();

    String assertionConsumerServiceURL = idpConfiguration.getAcsEndpoint() != null ? idpConfiguration.getAcsEndpoint() : authnRequest.getAssertionConsumerServiceURL();
    List<SAMLAttribute> attributes = attributes(authentication);

    SAMLPrincipal principal = new SAMLPrincipal(
      authentication.getName(),
      attributes.stream()
        .filter(attr -> "urn:oasis:names:tc:SAML:1.1:nameid-format".equals(attr.getName()))
        .findFirst().map(attr -> attr.getValue()).orElse(NameIDType.UNSPECIFIED),
      attributes,
      authnRequest.getIssuer().getValue(),
      authnRequest.getID(),
      assertionConsumerServiceURL,
      messageContext.getRelayState());

    samlMessageHandler.sendAuthnResponse(principal, response);
  }

  @SuppressWarnings("unchecked")
  private List<SAMLAttribute> attributes(Authentication authentication) {
    String uid = authentication.getName();
    Map<String, List<String>> result = new HashMap<>(idpConfiguration.getAttributes());
    User user = userRepository.getUserByUsername(uid);

    Optional<Map<String, List<String>>> optionalMap = Optional.of(new TreeMap<>());
    		/*= idpConfiguration.getUsers().stream()
      .filter(user -> user.getPrincipal().equals(uid))
      .findAny()
      .map(FederatedUserAuthenticationToken::getAttributes);*/
    optionalMap.ifPresent(result::putAll);

    //See SAMLAttributeAuthenticationFilter#setDetails
    Map<String, String[]> parameterMap = (Map<String, String[]>) authentication.getDetails();
    parameterMap.forEach((key, values) -> {
      result.put(key, Arrays.asList(values));
    });

    //Check if the user wants to be persisted
    if (parameterMap.containsKey("persist-me") && "on".equalsIgnoreCase(parameterMap.get("persist-me")[0])) {
      result.remove("persist-me");
      FederatedUserAuthenticationToken token = new FederatedUserAuthenticationToken(
        uid,
        authentication.getCredentials(),
        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
      token.setAttributes(result);
      //idpConfiguration.getUsers().removeIf(existingUser -> existingUser.getPrincipal().equals(uid));
      //idpConfiguration.getUsers().add(token);
    }

    //Provide the ability to limit the list attributes returned to the SP
    /*.map(entry -> entry.getKey().equals("urn:mace:dir:attribute-def:uid") ?
    new SAMLAttribute(entry.getKey(), singletonList(uid)) :
    new SAMLAttribute(entry.getKey(), entry.getValue()))*/
		return result.entrySet().stream()//.filter(entry -> !entry.getValue().stream().allMatch(StringUtils::isEmpty))
				.map(entry -> {
					SAMLAttribute samlAttribute = null;
					switch (entry.getKey()) {
					
					case "urn:mace:dir:attribute-def:uid":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(uid));
						break;
					case "urn:mace:dir:attribute-def:givenName":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getGivenName()));
						break;
					case "urn:mace:dir:attribute-def:cn":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getCustomerName()));
						break;
					case "urn:mace:dir:attribute-def:sn":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getLastName()));
						break;
					case "urn:mace:dir:attribute-def:displayName":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getDisplayName()));
						break;
					case "urn:mace:dir:attribute-def:mail":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getEmail()));
						break;
					case "urn:mace:terena.org:attribute-def:schacHomeOrganization":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getSchacHomeOrganization()));
						break;
					case "urn:mace:dir:attribute-def:eduPersonPrincipalName":
						samlAttribute = new SAMLAttribute(entry.getKey(), singletonList(user.getEduPersonPrincipalName()));
						break;
					default :
						samlAttribute = new SAMLAttribute("",Collections.EMPTY_LIST);
					}
					return samlAttribute;
				}).collect(toList());
  }

}
