package com.apps.miaowu.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Existing resources under res are not intercepted; a missing path such as /res/asdasd should just 404, but it is intercepted

        registry.addInterceptor(loginInterceptor).excludePathPatterns("/res/**", "/page/**");
    }

    
}