package me.gking2224.securityms.aop;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import me.gking2224.common.aop.CommonAopConfiguration;

@EnableAspectJAutoProxy(proxyTargetClass=true)
@Import(CommonAopConfiguration.class)
public class AopConfiguration {
    
}
