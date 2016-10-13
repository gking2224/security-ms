package me.gking2224.securityms.client;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

public interface WebSecurityConfigurer {

    void configure(WebSecurity web) throws Exception;

}
