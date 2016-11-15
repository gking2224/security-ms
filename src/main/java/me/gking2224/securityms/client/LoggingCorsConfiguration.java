package me.gking2224.securityms.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.cors.CorsConfiguration;

public class LoggingCorsConfiguration extends CorsConfiguration {


    private static Logger LOG = LoggerFactory.getLogger(LoggingCorsConfiguration.class);

    public String checkOrigin(String requestOrigin) {
        LOG.debug("checkOrigin: {}", requestOrigin);
        return super.checkOrigin(requestOrigin);
    }
}
