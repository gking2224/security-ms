package me.gking2224.securityms;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import me.gking2224.common.test.CommonTestConfiguration;
import me.gking2224.securityms.db.DatabaseConfiguration;
import me.gking2224.securityms.db.EmbeddedDatabaseConfiguration;
import me.gking2224.securityms.db.jpa.JpaConfiguration;
import me.gking2224.securityms.security.SecurityConfiguration;

@Import({CommonTestConfiguration.class, DatabaseConfiguration.class, EmbeddedDatabaseConfiguration.class, JpaConfiguration.class, SecurityConfiguration.class})
@ComponentScan({"me.gking2224.securityms.model", "me.gking2224.securityms.service"})
public class TestConfiguration {

}
