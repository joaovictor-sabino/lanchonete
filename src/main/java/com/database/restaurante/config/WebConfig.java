package com.database.restaurante.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.database.restaurante.service.ArmazenamentoImagemService;

import org.springframework.http.CacheControl;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ArmazenamentoImagemService imagens;

    public WebConfig(ArmazenamentoImagemService imagens) {
        this.imagens = imagens;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/produtos/**")
        .addResourceLocations(imagens.getPasta().toUri().toString())
        .setCacheControl(CacheControl.noCache());   // sempre confere se mudou
    }
}
