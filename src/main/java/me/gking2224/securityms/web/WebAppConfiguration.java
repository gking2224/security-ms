package me.gking2224.securityms.web;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import me.gking2224.common.web.CommonWebAppConfiguration;
import me.gking2224.common.web.WebConfigurationOptions;

@Profile("web")
@ComponentScan({"me.gking2224.securityms.web"})
@Import(CommonWebAppConfiguration.class)
@EnableWebMvc
public class WebAppConfiguration extends WebMvcConfigurationSupport {
    
    @Bean
    public WebConfigurationOptions getConfig() {
        return new WebConfigurationOptions() {

            @Override
            public String[] getAllowedCorsOrigins() {
                return new String[] {"http://localhost:8080"};
            }

            @Override
            public String[] getAllowedCorsMethods() {
                return null;
            }
            
        };
    }
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
      configurer.enable();
    }

    @Bean
    public ServletRegistrationBean foo() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();   
        dispatcherServlet.setApplicationContext(getApplicationContext());
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/*");
        servletRegistrationBean.setName("dispatcher");
        return servletRegistrationBean;
    }
}
