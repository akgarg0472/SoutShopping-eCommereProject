package com.akgarg.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;


    @Autowired
    public SecurityConfig(UserAuthenticationSuccessHandler userAuthenticationSuccessHandler) {
        this.userAuthenticationSuccessHandler = userAuthenticationSuccessHandler;
    }


    @Bean
    public UserDetailsService getUserDetailService() {
        return new UserDetailsServiceImpl();
    }


    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public StandardPasswordEncoder getStandardPasswordEncoder() {
        return new StandardPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider getDaoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
        daoAuthenticationProvider.setPasswordEncoder(this.getBCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(this.getDaoAuthenticationProvider());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("USER")
                .antMatchers("/seller/**").hasAnyRole("SELLER")
                .antMatchers("/**").permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/authenticate-login")
                .successHandler(userAuthenticationSuccessHandler)
                .failureUrl("/login?error")
                .and()
                .logout()
                .logoutSuccessUrl("/logout-success")
                .and()
                .csrf().disable();
    }
}