package com.api.impactanalysis.security.config;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryDatabaseConfig {
	@Bean
	public ServletRegistrationBean<WebServlet> h2servletRegistration() {
		ServletRegistrationBean<WebServlet> registration = new ServletRegistrationBean<>(new WebServlet());
		registration.addUrlMappings("/console/*");
		return registration;
	}
}
