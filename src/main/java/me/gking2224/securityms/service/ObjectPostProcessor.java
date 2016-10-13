package me.gking2224.securityms.service;

import org.springframework.stereotype.Component;

@Component
public class ObjectPostProcessor implements org.springframework.security.config.annotation.ObjectPostProcessor<Object> {

    @Override
    public <O> O postProcess(O object) {
        // TODO Auto-generated method stub
        return null;
    }

}
