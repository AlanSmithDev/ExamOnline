package com.sdut.examonline.web.config;

import com.google.common.collect.Lists;
import com.sdut.examonline.web.security.JwtAuthenticationFilter;
import com.sdut.examonline.web.security.JwtAuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final List<String> ALLOWED_PATH = Lists.newArrayList("/swagger-ui.html**",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/**");
    @Autowired
    JwtAuthenticationHandler jwtAuthenticationHandler;

    public static void addAllowedPath(String... paths) {
        ALLOWED_PATH.addAll(Arrays.asList(paths));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(ALLOWED_PATH.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationHandler)
                .accessDeniedHandler(jwtAuthenticationHandler);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //strength取值为4-32，性能越低，默认10，平均消耗210ms左右
        //修改为4后平均耗时10ms左右
        return new BCryptPasswordEncoder(4);
    }
}

