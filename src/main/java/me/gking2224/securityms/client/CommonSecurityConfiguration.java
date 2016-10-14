package me.gking2224.securityms.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.client.RestTemplate;

import me.gking2224.securityms.common.SecurityErrorHandler;

@EnableWebSecurity
@ComponentScan({"me.gking2224.securityms.client", "me.gking2224.securityms.common"})
@PropertySource("/security.properties")
public class CommonSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    @Value("${securityService.host}")
    private String host;
    
    @Value("${securityService.port}")
    private int port;
    
    @Value("${securityService.context}")
    private String context;
    
    @Autowired(required=true)
    private SecurityServiceClient serviceClient;

    @Autowired(required=true)
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
    public SecurityServiceClient securityClient(final RestTemplate restTemplate) {
        SecurityServiceClient client = new SecurityServiceClient();
        client.setHost(host);
        client.setPort(port);
        client.setContext(context);
        client.setRestTemplate(restTemplate);
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
        http.addFilterAfter(tokenFilter, SecurityContextPersistenceFilter.class);
        http.authorizeRequests()
            .antMatchers("/public/**").permitAll()
//            .antMatchers(HttpMethod.POST, "/logout").permitAll()
            .and().exceptionHandling().accessDeniedHandler(errorHandler);

        if (httpSecurityConfigurer != null) {
            httpSecurityConfigurer.configure(http);
        }
        http.csrf().disable(); // spring mvc handles csrf
        http.httpBasic().authenticationEntryPoint(authEntryPoint);
    }
    
    @Bean
    public FilterRegistrationBean tokenProcessingFilterBean(final TokenProcessingFilter filter) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
}
