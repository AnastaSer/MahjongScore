package com.mahjong.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                // разрешить все эндпоинты
                .allowedOrigins("*")              // разрешить с любых источников
                .allowedMethods("*")              // все HTTP методы (GET, POST...)
                .allowedHeaders("*")              // все заголовки
                .allowCredentials(false);         // без cookies (иначе нельзя "*")
    }
}
