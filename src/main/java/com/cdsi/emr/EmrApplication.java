package com.cdsi.emr;

import java.util.TimeZone; //
import javax.annotation.PostConstruct; //
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.cdsi.emr.fileupload.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(FileStorageProperties.class)
public class EmrApplication extends SpringBootServletInitializer {
	
	// This method runs once the application context is initialized
    @PostConstruct
    public void init() {
        // Setting the default JVM timezone to Manila
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EmrApplication.class);
    }
    
	public static void main(String[] args) {
		SpringApplication.run(EmrApplication.class, args);
	}

}
