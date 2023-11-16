package ir.vcx.api.filter.security;

import ir.vcx.util.KeyleadConfiguration;
import ir.vcx.util.ResponseWriterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Created by Sobhan at 8/10/2023 - VCX
 */

//TODO - change webSecurity configuration

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, order = Integer.MIN_VALUE + 1000)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final KeyleadConfiguration keyleadConfiguration;
    private final ResponseWriterUtil responseWriterUtil;

    @Autowired
    public WebSecurity(KeyleadConfiguration keyleadConfiguration, ResponseWriterUtil responseWriterUtil) {
        this.keyleadConfiguration = keyleadConfiguration;
        this.responseWriterUtil = responseWriterUtil;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .anonymous().principal("_ANONYMOUS_")
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/docs",
                        "/docs/**",
                        "/swagger-resources",
                        "/swagger-ui.html",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                ).permitAll()
                .antMatchers("/**").permitAll()
                .and()
                .addFilter(new MyBasicAuthenticationFilter(authenticationManager(), keyleadConfiguration, responseWriterUtil))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }
}
