package me.gking2224.securityms.web.mvc;


import org.springframework.context.annotation.Import;

import me.gking2224.common.test.WebAppTestConfigurer;
import me.gking2224.securityms.SecurityMicroServiceTestConfiguration;
import me.gking2224.securityms.web.WebAppConfiguration;

@Import({SecurityMicroServiceTestConfiguration.class, WebAppConfiguration.class})
public class WebAppTestConfiguration extends WebAppTestConfigurer {
    
}
