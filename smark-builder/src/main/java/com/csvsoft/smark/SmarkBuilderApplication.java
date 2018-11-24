package com.csvsoft.smark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
//@Configuration
/*@ComponentScan(basePackages = {
        "com.csvsoft.smark",
        "com.csvsoft.smark.ui"})*/
public class SmarkBuilderApplication extends SpringBootServletInitializer {
    private static Class<SmarkBuilderApplication> applicationClass = SmarkBuilderApplication.class;


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        SpringApplicationBuilder appBuilder = builder.sources(applicationClass);
        return appBuilder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SmarkBuilderApplication.class, args);
    }
}

