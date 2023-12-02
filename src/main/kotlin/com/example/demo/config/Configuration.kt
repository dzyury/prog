package com.example.demo.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
class Configuration {
    @Bean
    fun commandLineRunner(ctx: ApplicationContext): CommandLineRunner {
        return CommandLineRunner {
            println("-----------")
            println("Beans provided by Spring Boot:")
            val beanNames: Array<String> = ctx.beanDefinitionNames
            for ((idx, beanName) in beanNames.sorted().withIndex()) {
                println("${"%4d".format(idx + 1)} $beanName")
            }
            println("-----------")
        }
    }
}