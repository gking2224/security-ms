package me.gking2224.securityms;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import me.gking2224.common.test.CommonTestConfiguration;
import me.gking2224.securityms.db.DatabaseConfiguration;
import me.gking2224.securityms.db.EmbeddedDatabaseConfiguration;

@ComponentScan({"me.gking2224.securityms.model", "me.gking2224.securityms.service"})
@EnableJpaRepositories("me.gking2224.securityms.db.jpa")
@Import({DatabaseConfiguration.class, EmbeddedDatabaseConfiguration.class, CommonTestConfiguration.class})
public class TestConfiguration {

}
