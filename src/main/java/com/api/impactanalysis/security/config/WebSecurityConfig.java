package com.api.impactanalysis.security.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.api.impactanalysis.common.Constants;
import com.api.impactanalysis.security.RestAuthenticationEntryPoint;
import com.api.impactanalysis.security.auth.matchers.SkipPathRequestMatcher;
import com.api.impactanalysis.security.auth.matchers.extractor.HeaderTokenExtractor;
import com.api.impactanalysis.security.auth.providers.CredentialsBasedAuthenticationProvider;
import com.api.impactanalysis.security.auth.providers.JwtAuthenticationProvider;
import com.api.impactanalysis.security.filters.CredentialsBasedProcessingFilter;
import com.api.impactanalysis.security.filters.CustomCorsFilter;
import com.api.impactanalysis.security.filters.JwtTokenAuthenticationProcessingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationSuccessHandler successHandler;
    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AuthenticationFailureHandler failureHandler;
    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Autowired
    private CredentialsBasedAuthenticationProvider credentialsBasedAuthenticationProvider;
    @Autowired
    private HeaderTokenExtractor tokenExtractor;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(List<String> pathsToSkip, String pattern)
            throws Exception {
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
        JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider);
        auth.authenticationProvider(credentialsBasedAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().exceptionHandling().authenticationEntryPoint(
                this.authenticationEntryPoint).and().headers().frameOptions().disable().and().sessionManagement().sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS).and().authorizeRequests().antMatchers(
                                Constants.PERMITTED_END_POINTS.toArray(
                                        new String[Constants.PERMITTED_END_POINTS.size()])).permitAll().and().authorizeRequests().antMatchers(
                                                Constants.API_ROOT_URL).authenticated() // By Default all are protected.
                .and().addFilterBefore(new CustomCorsFilter(), UsernamePasswordAuthenticationFilter.class).addFilterBefore(
                        buildCredentialsBasedProcessingFilter(Constants.AUTHENTICATION_END_POINT),
                        UsernamePasswordAuthenticationFilter.class).addFilterBefore(
                                buildJwtTokenAuthenticationProcessingFilter(Constants.PERMITTED_END_POINTS, Constants.API_ROOT_URL),
                                UsernamePasswordAuthenticationFilter.class);
    }

    protected CredentialsBasedProcessingFilter buildCredentialsBasedProcessingFilter(String authEntryPoint) throws Exception {
        CredentialsBasedProcessingFilter filter = new CredentialsBasedProcessingFilter(authEntryPoint, successHandler, failureHandler, objectMapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }
}
