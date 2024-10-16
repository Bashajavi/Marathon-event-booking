package com.config;

import javax.sql.DataSource;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Configuration
public class SecurityConfig {
 
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/register","/dashboard","/manage-contacts","/edit-contact/{id}","/delete-contact/{id}","add-contact","manage-posters","manage-associates","manage-partners","manage-sponsors","manage-gallery","add-sponsor","edit-associate","add-associate","add-poster","add-partner","add-image","/delete-sponsor/{id}","/edit-sponsor/{id}","/edit-partner/{id}","delete-partner/{id}","/edit-poster/{id}","delete-poster/{id}","/edit-associate/{id}","delete-associate/{id}","/edit-image/{id}","delete-image/{id}","manage-participants","add-participant","/edit-participant/{id}","/delete-participant/{id}","/mail-participant","/contact-us").authenticated() // Modify authenticated URLs
                .requestMatchers("/","/services","/aboutus","/contactus","/results","/css/**","/images/**","/5k-leaderboard","/10k-leaderboard","/21k-leaderboard","/42k-leaderboard").permitAll()
        )
        .formLogin(formLogin -> formLogin
            .loginPage("/login") // Specify custom login page URL
            .permitAll()
            .defaultSuccessUrl("/dashboard") // Specify the URL to redirect to upon successful login
        )
        .logout(logout -> logout
            .logoutUrl("/logout") // Specify logout URL
            .logoutSuccessUrl("/") // Specify the URL to redirect to upon successful logout
            .permitAll()
        )
        .httpBasic(Customizer.withDefaults())       
        .csrf(csrf -> csrf.disable()); // Disable CSRF protection
        
        return http.build();
    }
    
//    @Bean
//    public UserDetailsService userDetailsService(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }    

       
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/dashboard"); // Redirect to /profile after successful login
        return handler;
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        SimpleUrlLogoutSuccessHandler handler = new SimpleUrlLogoutSuccessHandler();
        handler.setDefaultTargetUrl("/"); // Redirect to /welcome after successful logout
        return handler;
    }
    
//   @Bean
//   public PasswordEncoder passwordEncoder() {
//    	return NoOpPasswordEncoder.getInstance();
//  }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
