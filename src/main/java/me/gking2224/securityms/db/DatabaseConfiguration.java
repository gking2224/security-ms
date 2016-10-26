package me.gking2224.securityms.db;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Profile("!embedded")
@ComponentScan({"me.gking2224.securityms.db"})
public class DatabaseConfiguration {

    @Autowired
    private PlatformTransactionManager txManager;
    
    @Bean
    public DefaultAdvisorAutoProxyCreator proxyCreator() {
        
        DefaultAdvisorAutoProxyCreator rv = new DefaultAdvisorAutoProxyCreator();
        rv.setProxyTargetClass(true);
        return rv;
    }
    @Bean
    public TransactionAttributeSourceAdvisor txAttributeAdvisor(TransactionInterceptor txInterceptor) {
        TransactionAttributeSourceAdvisor rv = new TransactionAttributeSourceAdvisor();
        rv.setTransactionInterceptor(txInterceptor);
        return rv;
    }
    
    @Bean("transactionInterceptor")
    public TransactionInterceptor txInterceptor() {
        
        TransactionInterceptor rv = new TransactionInterceptor();
        rv.setTransactionManager(txManager);
        rv.setTransactionAttributeSource(transactionAttributeSource());
        return rv;
    }
    
    private TransactionAttributeSource transactionAttributeSource() {
        AnnotationTransactionAttributeSource rv = new AnnotationTransactionAttributeSource();
        return rv;
    }
}
