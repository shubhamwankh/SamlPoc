package com.example.SamlTesting;

//import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
//import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfiguration {


//    @Bean
//    Saml2AuthenticationRequestResolver resolver(RelyingPartyRegistrationRepository repo) {
//        return new OpenSaml4AuthenticationRequestResolver(repo);
//    }


    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());

        RelyingPartyRegistrationResolver relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(this.repository);

        Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());


        http.authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())

                .saml2Login(saml2 -> saml2
                        .relyingPartyRegistrationRepository(repository)
                        .authenticationManager(new ProviderManager(authenticationProvider)))

//                .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class)
                .saml2Logout(withDefaults())
        ;

        return http.build();
    }

    @Autowired
    private RelyingPartyRegistrationRepository repository;

//    @PostConstruct
//    public void test(){
//        System.out.println(repository);
//    }


//    @Bean
//    public RelyingPartyRegistrationRepository relyingPartyRegistrations() {
//        RelyingPartyRegistration registration = RelyingPartyRegistrations
//                .fromMetadataLocation("https://dev-17729161.okta.com/app/exkkfo143htDBx2aV5d7/sso/saml/metadata")
//                .assertingPartyDetails(builder -> builder.singleSignOnServiceLocation("/login/saml2/sso/okta"))
//                .registrationId("Testing")
//                .build();
//        return new InMemoryRelyingPartyRegistrationRepository(registration);
//    }

    private Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {

        Converter<ResponseToken, Saml2Authentication> delegate =
                OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

        return (responseToken) -> {
            Saml2Authentication authentication = delegate.convert(responseToken);
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            List<String> groups = principal.getAttribute("groups");
            Set<GrantedAuthority> authorities = new HashSet<>();
            if (groups != null) {
                groups.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
            } else {
                authorities.addAll(authentication.getAuthorities());
            }
            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
        };
    }
}