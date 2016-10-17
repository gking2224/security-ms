package me.gking2224.securityms.batch;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import me.gking2224.common.batch.CommonBatchConfiguration;

@Profile("batch")
@Import({BatchScheduler.class, CommonBatchConfiguration.class})
public class BatchConfiguration {

}
