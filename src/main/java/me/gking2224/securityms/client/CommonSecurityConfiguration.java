package me.gking2224.securityms.client;

import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;

import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import me.gking2224.securityms.common.SecurityErrorHandler;

@EnableWebSecurity
@ComponentScan({"me.gking2224.securityms.client", "me.gking2224.securityms.common"})
@PropertySource(value="/security.properties", ignoreResourceNotFound=true)
public class CommonSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    @Value("${securityService.host}")
    private String host;
    
    @Value("${securityService.port}")
    private int port;
    
    @Value("${securityService.context}")
    private String context;
    
    @Autowired
    private SecurityServiceClient serviceClient;

    @Autowired
    private TokenProcessingFilter tokenFilter;
    
    @Autowired(required=false)
    private WebSecurityConfigurer webSecurityConfigurer;
    
    @Autowired(required=false)
    private HttpSecurityConfigurer httpSecurityConfigurer;
    
    @Autowired
    private SecurityErrorHandler errorHandler;

    @Autowired
    private NonHtmlBasicAuthenticationEntryPoint authEntryPoint;

    @Bean
    @DependsOn(KEEP_TOKEN_ALIVE_TOPIC)
    public SecurityServiceClient securityClient(
            final RestTemplate restTemplate,
            @Autowired @Qualifier("pubSubTemplate") final JmsTemplate pubSubTemplate,
            @Qualifier(KEEP_TOKEN_ALIVE_TOPIC) Topic keepTokenAliveTopic
    ) {
        SecurityServiceClient client = new SecurityServiceClient();
        client.setHost(host);
        client.setPort(port);
        client.setContext(context);
        client.setRestTemplate(restTemplate);
        client.setJmsTemplate(pubSubTemplate);
        client.setKeepTokenAliveTopic(keepTokenAliveTopic);
        return client;
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    private AuthenticationProvider authenticationProvider() {
        return new AuthenticationProvider() {

            @Override
            public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
                
                UsernamePasswordAuthenticationToken a = (UsernamePasswordAuthenticationToken)authentication;
                return serviceClient.authenticate((String)a.getPrincipal(), (String)a.getCredentials());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
            
        };
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
        if (webSecurityConfigurer != null) {
            webSecurityConfigurer.configure(web);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(tokenFilter, BasicAuthenticationFilter.class);
        http.authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .and().exceptionHandling().accessDeniedHandler(errorHandler);

        if (httpSecurityConfigurer != null) {
            httpSecurityConfigurer.configure(http);
        }
        http.cors();
        http.httpBasic().authenticationEntryPoint(authEntryPoint);
    }
    
    @Bean
    public FilterRegistrationBean tokenProcessingFilterBean(final TokenProcessingFilter filter) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
    
    @Bean(KEEP_TOKEN_ALIVE_TOPIC)
    public Topic keepTokenAlive() {
        return new ActiveMQTopic(KEEP_TOKEN_ALIVE_TOPIC);
    }
}
