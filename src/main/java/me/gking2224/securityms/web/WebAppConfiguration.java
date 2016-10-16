package me.gking2224.securityms.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import me.gking2224.common.web.CommonWebAppConfiguration;

@Profile("web")
@ComponentScan({"me.gking2224.securityms.web"})
@Import(CommonWebAppConfiguration.class)
@EnableWebMvc
public class WebAppConfiguration extends WebMvcConfigurationSupport {
}
