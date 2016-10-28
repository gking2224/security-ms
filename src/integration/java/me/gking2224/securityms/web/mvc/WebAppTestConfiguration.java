package me.gking2224.securityms.web.mvc;


import org.springframework.context.annotation.Import;

import me.gking2224.common.test.WebAppTestConfigurer;
import me.gking2224.securityms.TestConfiguration;
import me.gking2224.securityms.web.WebAppConfiguration;

@Import({TestConfiguration.class, WebAppConfiguration.class})
public class WebAppTestConfiguration extends WebAppTestConfigurer {
    
}
