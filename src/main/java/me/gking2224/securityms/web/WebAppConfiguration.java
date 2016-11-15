package me.gking2224.securityms.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Profile("web")
@ComponentScan({"me.gking2224.securityms.web", "me.gking2224.securityms.client.web"})
@EnableWebMvc
public class WebAppConfiguration {
}
