package me.gking2224.securityms.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Profile("web")
@ComponentScan({"me.gking2224.securityms.web"})
@EnableWebMvc
public class WebAppConfiguration extends WebMvcConfigurationSupport {
}
