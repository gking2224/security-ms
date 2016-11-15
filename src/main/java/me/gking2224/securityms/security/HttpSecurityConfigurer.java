package me.gking2224.securityms.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class HttpSecurityConfigurer implements me.gking2224.securityms.client.HttpSecurityConfigurer {
    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.headers()
            .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().disable();
    }
}
