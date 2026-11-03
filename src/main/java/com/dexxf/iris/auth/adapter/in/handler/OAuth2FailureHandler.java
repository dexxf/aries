package com.dexxf.iris.auth.adapter.in.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public OAuth2FailureHandler(@Value("${app.auth.oauth-failure-url}") String failureUrl) {
        setDefaultFailureUrl(failureUrl);
    }
}
