package com.example.SamlTesting;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.saml2.core.Saml2ParameterNames;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class Saml2WebSsoAuthenticationFilterPostProcessor implements ObjectPostProcessor<Saml2WebSsoAuthenticationFilter> {
	private final RelyingPartyRegistrationRepository registrations;

	public Saml2WebSsoAuthenticationFilterPostProcessor(RelyingPartyRegistrationRepository registrations) {
		this.registrations = registrations;
	}

	@Override
	public Saml2WebSsoAuthenticationFilter postProcess(Saml2WebSsoAuthenticationFilter object) {
		return new Saml2WebSsoAuthenticationFilter(this.registrations) {
			@Override
			protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
				// bypass filter if SAMLResponse not present
				return super.requiresAuthentication(request, response) &&
					request.getParameter(Saml2ParameterNames.SAML_RESPONSE) != null;
			}
		};
	}
}
