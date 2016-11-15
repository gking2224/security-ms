package me.gking2224.securityms.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass=true)
@ComponentScan(basePackages="me.gking2224.securityms.aop")
public class AopConfiguration {
    
}
