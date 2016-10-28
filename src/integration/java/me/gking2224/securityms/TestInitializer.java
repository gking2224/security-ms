package me.gking2224.securityms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import me.gking2224.common.EnvironmentExtender;
import me.gking2224.common.web.ConfigurableWebEnvironmentImplementation;

public class TestInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

    @Override
    public void initialize(GenericApplicationContext applicationContext) {
        Set<String> profiles = new HashSet<String>(
                Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()));
        ConfigurableWebEnvironmentImplementation env = new ConfigurableWebEnvironmentImplementation(
                "securityms", "ci", profiles);
        applicationContext.setEnvironment(env);
        new EnvironmentExtender(env).extendEnvironmentWithAnnotatedProperties("me.gking2224");
        env.registerEnvironmentPropertiesAsBeans(applicationContext);
    }

}
