
package com.dexxf.iris.auth.adapter.in.handler;

import com.dexxf.iris.auth.application.command.OAuthSignInCommand;
import com.dexxf.iris.auth.application.error.OAuthSignInError;
import com.dexxf.iris.auth.application.port.in.OAuthSignInUseCase;
import com.dexxf.iris.auth.application.result.OAuthSignInResult;
import com.dexxf.iris.auth.infrastructure.config.properties.DeviceIdCookieProperties;
import com.dexxf.iris.auth.infrastructure.config.properties.JwtCookieProperties;
import com.dexxf.iris.auth.infrastructure.config.properties.RefreshCookieProperties;
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
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthSignInUseCase signInUseCase;
    private final String successUrl;
    private final DeviceIdCookieProperties deviceCookie;
    private final JwtCookieProperties jwtCookie;
    private final RefreshCookieProperties refreshCookie;
    private final long jwtExpirationMs;

    public OAuth2SuccessHandler(
            OAuthSignInUseCase signInUseCase,
            @Value("${app.auth.oauth-success-url}") String successUrl,
            DeviceIdCookieProperties deviceCookie,
            JwtCookieProperties jwtCookie,
            RefreshCookieProperties refreshCookie,
            @Value("${security.jwt.expiration-ms}") long jwtExpirationMs
    ) {
        this.signInUseCase = signInUseCase;
        this.successUrl = successUrl;
        this.deviceCookie = deviceCookie;
        this.jwtCookie = jwtCookie;
        this.refreshCookie = refreshCookie;
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
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth provider did not provide a user ID and email");
            return;
        }

        String deviceId = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("device_id".equals(cookie.getName())) {
                    deviceId = cookie.getValue();
                    break;
                }
            }
        }

        boolean isNewDevice = deviceId == null || deviceId.isBlank();

        if (isNewDevice) {
            deviceId = UUID.randomUUID().toString();
        }

        Result<OAuthSignInResult, OAuthSignInError> result =
                signInUseCase.signIn(
                        new OAuthSignInCommand(
                                registrationId,
                                providerId,
                                email,
                                deviceId,
                                request.getHeader("User-Agent"),
                                request.getRemoteAddr()
                        )
                );

        if (!result.success()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth sign-in could not be completed");
            return;
        }

        OAuthSignInResult signInResult = result.data();
        int jwtTtlSeconds = (int) Math.min(Integer.MAX_VALUE, Math.max(0, jwtExpirationMs / 1000));

        long refreshTtlSeconds = Math.max(0, Duration.between(Instant.now(), signInResult.refreshTokenExpiresAt()).getSeconds());

        if (isNewDevice) {
            addCookie(response, "device_id", deviceId,
                    deviceCookie.secure(),
                    deviceCookie.httpOnly(),
                    deviceCookie.domain(),
                    deviceCookie.path(),
                    deviceCookie.sameSite(),
                    deviceCookie.maxAge());
        }

        addCookie(response, "jwt", signInResult.jwt(),
                jwtCookie.secure(),
                jwtCookie.httpOnly(),
                jwtCookie.domain(),
                jwtCookie.path(),
                jwtCookie.sameSite(),
                jwtTtlSeconds);

        addCookie(response, "refresh_token", signInResult.refreshToken(),
                refreshCookie.secure(),
                refreshCookie.httpOnly(),
                refreshCookie.domain(),
                refreshCookie.path(),
                refreshCookie.sameSite(),
                (int) Math.min(Integer.MAX_VALUE, refreshTtlSeconds));

        getRedirectStrategy().sendRedirect(request, response, successUrl);
    }

    private void addCookie(HttpServletResponse response, String name, String value, boolean secure, boolean httpOnly, String domain, String path, String sameSite, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setAttribute("SameSite", sameSite);
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }
}
