package com.dexxf.iris.auth.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cookie.device")
public record DeviceIdCookieProperties(
        boolean secure,
        boolean httpOnly,
        String domain,
        String path,
        String sameSite,
        int maxAge
) {}