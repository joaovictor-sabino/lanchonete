package com.database.restaurante;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/minha-conta/**").authenticated()
                .anyRequest().permitAll())
            .formLogin(f -> f
                .loginPage("/login")
                .defaultSuccessUrl("/cardapio", false)   // false: volta para a página que ele tentou abrir
                .permitAll())
            .logout(l -> l
                .logoutSuccessUrl("/cardapio"));

        return http.build();
    }
}
