package me.gking2224.securityms.client;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface HttpSecurityConfigurer {

    void configure(HttpSecurity http) throws Exception;

}
