package com.example.backend_staffoji_game.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Value("${allowed.emails}")
    private String[] allowedEmails;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();


        if (!Arrays.asList(allowedEmails).contains(email)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();

            }
            response.sendRedirect("/access-denied");
        } else {
            // Redirect to home page if the user's email is in the list of allowed emails
            response.sendRedirect("/swagger-ui/index.html");
        }

    }
}