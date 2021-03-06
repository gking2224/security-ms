package me.gking2224.securityms.client;

import static me.gking2224.common.client.jms.CommonMessagingConfiguration.TOPIC_LISTENER_CONTAINER_FACTORY;
import static me.gking2224.securityms.client.SecurityServiceClient.KEEP_TOKEN_ALIVE_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_EXPIRED_TOPIC;
import static me.gking2224.securityms.client.SecurityServiceClient.TOKEN_INVALIDATED_TOPIC;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.JmsListener;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import me.gking2224.common.client.EnvironmentProperties;
import me.gking2224.common.client.WebConfigurationOptions;
import me.gking2224.securityms.client.web.SecurityErrorHandler;

@EnableWebSecurity
@ComponentScan(basePackages="me.gking2224.securityms.client")
@EnvironmentProperties(value="props:/security.properties", name="common-security", prefix="security")
public class CommonSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    @Autowired TokenProcessingFilter tokenFilter;
    @Autowired(required=false) WebSecurityConfigurer webSecurityConfigurer;
    @Autowired(required=false) HttpSecurityConfigurer httpSecurityConfigurer;
    @Autowired SecurityErrorHandler errorHandler;
    @Autowired NonHtmlBasicAuthenticationEntryPoint authEntryPoint;
    @Autowired SecurityServiceClient securityServiceClient;

    @Autowired private WebConfigurationOptions options;
    @Autowired private CorsConfigurationSource corsConfig;
    
    @Bean("csrfTokenStore")
    public Store<String, CsrfToken> csrfTokenStore() {
        return new MapStore<String, CsrfToken>();
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
                return securityServiceClient.authenticate((String)a.getPrincipal(), (String)a.getCredentials());
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
        http.cors().configurationSource(corsConfig);
        http.csrf().disable();
        http.httpBasic().authenticationEntryPoint(authEntryPoint);
    }

    @Bean
    public FilterRegistrationBean tokenProcessingFilterBean() {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(tokenFilter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
    
    @JmsListener(destination = TOKEN_INVALIDATED_TOPIC, containerFactory=TOPIC_LISTENER_CONTAINER_FACTORY)
    public void tokenInvalidated(ObjectMessage message) throws JMSException {
        TokenInvalidatedMessage m = (TokenInvalidatedMessage)message.getObject();
        securityServiceClient.tokenInvalidated(m.getToken(), m.getException());
    }
    
    @JmsListener(destination = TOKEN_EXPIRED_TOPIC, containerFactory=TOPIC_LISTENER_CONTAINER_FACTORY)
    public void tokenExpired(ObjectMessage message) throws JMSException {
        TokenExpiredMessage m = (TokenExpiredMessage)message.getObject();
        securityServiceClient.tokenExpired(m.getToken());
    }

    @Bean
    @ConditionalOnMissingBean(CorsConfigurationSource.class)
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new LoggingCorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(options.getAllowedCorsOrigins()));
        configuration.setAllowedMethods(Arrays.asList(options.getAllowedCorsMethods()));
        configuration.setAllowedHeaders(Arrays.asList(options.getAllowedRequestHeaders()));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean(KEEP_TOKEN_ALIVE_TOPIC)
    public Topic keepTokenAlive() {
        return new ActiveMQTopic(KEEP_TOKEN_ALIVE_TOPIC);
    }
    
    @Bean(TOKEN_INVALIDATED_TOPIC)
    public Topic tokenInvalidatedTopic() {
        return new ActiveMQTopic(TOKEN_INVALIDATED_TOPIC);
    }
    
    @Bean(TOKEN_EXPIRED_TOPIC)
    public Topic tokenExpiredTopic() {
        return new ActiveMQTopic(TOKEN_EXPIRED_TOPIC);
    }
}
