package me.gking2224.securityms.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import me.gking2224.securityms.client.CommonSecurityConfiguration;
import me.gking2224.securityms.client.HttpSecurityConfigurer;

@Import(CommonSecurityConfiguration.class)
@Profile("web")
@Configuration
public class WebSecurityConfiguration {
    
    @Bean
    HttpSecurityConfigurer httpSecurityConfigurer() {
        return new HttpSecurityConfigurer() {
            @Override
            public void configure(final HttpSecurity http) throws Exception {
            }
        };
    }
}
