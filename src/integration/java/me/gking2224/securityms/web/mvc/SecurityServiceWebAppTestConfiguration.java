package me.gking2224.securityms.web.mvc;


import org.springframework.context.annotation.Import;

import me.gking2224.common.test.WebAppTestConfigurer;
import me.gking2224.securityms.SecurityMicroServiceTestConfiguration;

@Import({SecurityMicroServiceTestConfiguration.class})
@org.springframework.test.context.web.WebAppConfiguration
public class SecurityServiceWebAppTestConfiguration extends WebAppTestConfigurer {
    
}
