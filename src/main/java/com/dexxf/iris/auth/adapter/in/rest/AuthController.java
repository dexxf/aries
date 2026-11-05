package com.dexxf.iris.auth.adapter.in.rest;

import com.dexxf.iris.auth.adapter.in.dto.response.RotateRefreshTokenResponse;
import com.dexxf.iris.auth.adapter.in.mapper.RotateRefreshTokenHttpMapper;
import com.dexxf.iris.auth.application.command.RotateRefreshTokenCommand;
import com.dexxf.iris.auth.application.error.RefreshTokenRotationError;
import com.dexxf.iris.auth.application.port.in.RotateRefreshTokenUseCase;
import com.dexxf.iris.auth.application.port.out.JwtTokenGeneratorPort;
import com.dexxf.iris.auth.application.result.RefreshTokenRotationResult;
import com.dexxf.iris.auth.infrastructure.config.properties.JwtCookieProperties;
import com.dexxf.iris.auth.infrastructure.config.properties.RefreshCookieProperties;
import com.dexxf.iris.common.result.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RotateRefreshTokenUseCase rotateRefreshTokenUseCase;
    private final JwtTokenGeneratorPort jwtTokenGeneratorPort;
    private final JwtCookieProperties jwtCookieProperties;
    private final RefreshCookieProperties refreshCookieProperties;
    private final long jwtMaxAge;
    private final long refreshTokenMaxAge;

    public AuthController(
            RotateRefreshTokenUseCase rotateRefreshTokenUseCase,
            JwtTokenGeneratorPort jwtTokenGeneratorPort,
            JwtCookieProperties jwtCookieProperties,
            RefreshCookieProperties refreshCookieProperties,
            @Value("${security.jwt.expiration-ms}") long jwtMaxAge,
            @Value("${security.refresh-token.lifetime-hour:168}") long refreshTokenLifetimeHours
    ) {
        this.rotateRefreshTokenUseCase = rotateRefreshTokenUseCase;
        this.jwtTokenGeneratorPort = jwtTokenGeneratorPort;
        this.jwtCookieProperties = jwtCookieProperties;
        this.refreshCookieProperties = refreshCookieProperties;
        this.jwtMaxAge = jwtMaxAge;
        this.refreshTokenMaxAge = refreshTokenLifetimeHours * 3600;
    }

    @PostMapping("/refresh")
    public ResponseEntity<RotateRefreshTokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = extractCookieFromRequest(request, "device_id");

        if (deviceId == null || deviceId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(RotateRefreshTokenResponse.fail("Device ID is required and must be valid"));
        }

        String plainRefreshToken = extractCookieFromRequest(request, "refresh_token");
        if (plainRefreshToken == null || plainRefreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(RotateRefreshTokenResponse.fail("Refresh token is missing"));
        }

        try {
            String ipAddress = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");

            Result<RefreshTokenRotationResult, RefreshTokenRotationError> rotationResult =
                    rotateRefreshTokenUseCase.execute(
                            new RotateRefreshTokenCommand(plainRefreshToken, deviceId, ipAddress, userAgent)
                    );

            if (!rotationResult.success()) {
                return ResponseEntity.status(RotateRefreshTokenHttpMapper.toStatus(rotationResult.error()))
                        .body(RotateRefreshTokenResponse.fail(
                                RotateRefreshTokenHttpMapper.toMessage(rotationResult.error())
                        ));
            }

            RefreshTokenRotationResult result = rotationResult.data();

            String newJwtToken = jwtTokenGeneratorPort.generateToken(result.authId());

            addJwtCookie(response, newJwtToken);
            addRefreshCookie(response, result.plainRefreshToken());

            return ResponseEntity.ok(
                    RotateRefreshTokenResponse.ok(result.expiresAt())
            );

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(RotateRefreshTokenResponse.fail("Failed to refresh token"));
        }
    }

    private String extractCookieFromRequest(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void addJwtCookie(HttpServletResponse response, String value) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("jwt", value)
                .httpOnly(jwtCookieProperties.httpOnly())
                .secure(jwtCookieProperties.secure())
                .path(jwtCookieProperties.path())
                .sameSite(jwtCookieProperties.sameSite())
                .maxAge(jwtMaxAge / 1000);

        if (jwtCookieProperties.domain() != null && !jwtCookieProperties.domain().isBlank()) {
            builder.domain(jwtCookieProperties.domain());
        }

        ResponseCookie cookie = builder.build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void addRefreshCookie(HttpServletResponse response, String value) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("refresh_token", value)
                .httpOnly(refreshCookieProperties.httpOnly())
                .secure(refreshCookieProperties.secure())
                .path(refreshCookieProperties.path())
                .sameSite(refreshCookieProperties.sameSite())
                .maxAge(refreshTokenMaxAge * 3600);

        if (refreshCookieProperties.domain() != null && !refreshCookieProperties.domain().isBlank()) {
            builder.domain(refreshCookieProperties.domain());
        }

        ResponseCookie cookie = builder.build();

        response.addHeader("Set-Cookie", cookie.toString());
    }



    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}