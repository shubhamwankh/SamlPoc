//package com.example.SamlTesting;
//
//
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class SamlSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request,
//                                        HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//        DefaultSaml2AuthenticatedPrincipal principal = (DefaultSaml2AuthenticatedPrincipal) authentication.getPrincipal();
//
//    }
//}
