package me.gking2224.securityms.web.mvc;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.gking2224.common.test.WebAppTestConfigurer;
import me.gking2224.securityms.TestConfiguration;
import me.gking2224.securityms.web.WebAppConfiguration;

@ComponentScan({"me.gking2224.securityms.model", "me.gking2224.securityms.service"})
@Import({WebAppConfiguration.class, TestConfiguration.class})
public class WebAppTestConfiguration extends WebAppTestConfigurer {
}
