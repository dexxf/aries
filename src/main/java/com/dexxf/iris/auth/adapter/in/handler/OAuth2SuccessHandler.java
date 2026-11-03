package com.dexxf.iris.auth.adapter.in.handler;

import com.dexxf.iris.auth.application.command.OAuthSignInCommand;
import com.dexxf.iris.auth.application.error.OAuthSignInError;
import com.dexxf.iris.auth.application.port.in.OAuthSignInUseCase;
import com.dexxf.iris.auth.application.result.OAuthSignInResult;
import com.dexxf.iris.common.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuthSignInUseCase signInUseCase;
    private final String successUrl;
    private final boolean secureCookies;
    private final String cookieSameSite;
    private final long jwtExpirationMs;

    public OAuth2SuccessHandler(OAuthSignInUseCase signInUseCase,
                                @Value("${app.auth.oauth-success-url}") String successUrl,
                                @Value("${app.auth.cookie-secure}") boolean secureCookies,
                                @Value("${app.auth.cookie-same-site}") String cookieSameSite,
                                @Value("${security.jwt.expiration-ms}") long jwtExpirationMs) {
        this.signInUseCase = signInUseCase;
        this.successUrl = successUrl;
        this.secureCookies = secureCookies;
        this.cookieSameSite = cookieSameSite;
        this.jwtExpirationMs = jwtExpirationMs;
        setDefaultTargetUrl(successUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)
                || !(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth sign-in could not be completed");
            return;
        }

        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        String providerId = oauthUser.getName();
        Object emailAttribute = oauthUser.getAttributes().get("email");
        String email = emailAttribute instanceof String value ? value : null;
        if (providerId == null || providerId.isBlank() || email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "OAuth provider did not provide a user ID and email");
            return;
        }

        Result<OAuthSignInResult, OAuthSignInError> result = signInUseCase.signIn(
                new OAuthSignInCommand(registrationId, providerId, email));
        if (!result.success()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth sign-in could not be completed");
            return;
        }

        int jwtTtlSeconds = (int) Math.min(Integer.MAX_VALUE, Math.max(0, jwtExpirationMs / 1000));
        addCookie(response, "jwt", result.data().jwt(), "/", jwtTtlSeconds);
        getRedirectStrategy().sendRedirect(request, response, successUrl);
    }

    private void addCookie(HttpServletResponse response, String name, String value, String path, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookies);
        cookie.setPath(path);
        cookie.setAttribute("SameSite", cookieSameSite);
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }
}
