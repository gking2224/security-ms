package me.gking2224.securityms.db;

import java.util.Collections;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import me.gking2224.common.db.embedded.DefaultEmbeddedDatabaseOptions;
import me.gking2224.common.db.embedded.EmbeddedDatabaseOptions;

@Profile("embedded")
@ComponentScan({"me.gking2224.securityms.db"})
@Import(me.gking2224.common.db.embedded.EmbeddedDatabaseConfiguration.class)
public class EmbeddedDatabaseConfiguration {
    
    @Bean
    public EmbeddedDatabaseOptions getOptions() {
        return new DefaultEmbeddedDatabaseOptions() {
            
            @Override
            public Map<String,String> getDatabaseOptions() {
                return Collections.singletonMap("transformedBitIsBoolean", "true");
            }
        };
    }
}
