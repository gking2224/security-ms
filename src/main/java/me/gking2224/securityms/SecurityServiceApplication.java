package me.gking2224.securityms;


import org.apache.commons.cli.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import me.gking2224.common.AbstractMicroServiceApplication;
import me.gking2224.common.CommonConfiguration;
import me.gking2224.securityms.batch.BatchConfiguration;
import me.gking2224.securityms.db.DatabaseConfiguration;
import me.gking2224.securityms.db.EmbeddedDatabaseConfiguration;
import me.gking2224.securityms.jms.MessagingConfiguration;
import me.gking2224.securityms.security.SecurityConfiguration;
import me.gking2224.securityms.web.WebAppConfiguration;

@Configuration
@ComponentScan(basePackages={"me.gking2224.securityms.service", "me.gking2224.securityms.model", "me.gking2224.securityms.common"})
@Import({
    CommonConfiguration.class,
    DatabaseConfiguration.class, EmbeddedDatabaseConfiguration.class,
    WebAppConfiguration.class,
    BatchConfiguration.class,
    SecurityConfiguration.class,
    MessagingConfiguration.class
})
public class SecurityServiceApplication extends AbstractMicroServiceApplication {

    public static void main(String[] args) throws ParseException {
        SpringApplication app = new Builder(args)
                .appPrefix("securityms")
                .applicationClass(SecurityServiceApplication.class)
                .build();
        if (app != null) app.run(args);
    }
}