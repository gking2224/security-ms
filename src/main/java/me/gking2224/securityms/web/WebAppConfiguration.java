package me.gking2224.securityms.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@Profile("web")
@ComponentScan({"me.gking2224.securityms.web", "me.gking2224.securityms.client.web"})
public class WebAppConfiguration {
}
