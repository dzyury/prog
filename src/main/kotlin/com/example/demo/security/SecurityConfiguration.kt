package com.example.demo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers("/cat/**").permitAll()
                it.requestMatchers(HttpMethod.POST,"/user").permitAll()
                it.anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
//        return BCryptPasswordEncoder(16)
        return NoOpPasswordEncoder.getInstance()
    }
}