package me.gking2224.securityms.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import me.gking2224.securityms.service.ManagementService;

@Profile("batch")
@EnableAsync
@EnableScheduling
public class BatchScheduler {
    
    @Autowired ManagementService mgmtService;
    
    @Scheduled(cron="0 0/5 * * * *")
    public void cleanExpiredTokens()  {
        mgmtService.cleanExpiredTokens();
    }

}
