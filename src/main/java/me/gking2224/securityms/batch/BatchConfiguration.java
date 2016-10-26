package me.gking2224.securityms.batch;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Profile("batch")
@Import({BatchScheduler.class})
public class BatchConfiguration {

}
