package com.dexxf.iris.auth.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie.refresh")
public record RefreshCookieProperties(
        boolean secure,
        boolean httpOnly,
        String domain,
        String path,
        String sameSite
) {
}