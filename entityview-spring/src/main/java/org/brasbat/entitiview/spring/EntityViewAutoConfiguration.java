package org.brasbat.entitiview.spring;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.ServletForwardingController;

import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.VaadinScanPackagesRegistrar;
import com.vaadin.flow.spring.VaadinScopesConfig;
import com.vaadin.flow.spring.VaadinServletConfiguration;
import com.vaadin.flow.spring.annotation.EnableVaadin;

@Configuration
@ComponentScan(basePackages = "org.brasbat.entitiview.spring")
@PropertySource("classpath:application.entityview.properties")
@EnableVaadin({"org.brasbat"})
public class EntityViewAutoConfiguration
{
	@Bean
	public ServletRegistrationBean frontendServletBean() {
		ServletRegistrationBean bean = new ServletRegistrationBean<>(new VaadinServlet() {
			@Override
			protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
			{
				if (!serveStaticOrWebJarRequest(req, resp)) {
					resp.sendError(404);
				}
			}
		}, "/frontend/*");
		bean.setLoadOnStartup(1);
		return bean;
	}

//	@Bean
//	public SimpleUrlHandlerMapping vaadinRootMapping(@Value("${vaadin.urlMapping}") String rootPath) {
//		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
//		mapping.setOrder(2147483647);
//		mapping.setUrlMap(Collections.singletonMap(rootPath, this.vaadinForwardingController()));
//		return mapping;
//	}
//
//	@Bean
//	public Controller vaadinForwardingController() {
//		ServletForwardingController controller = new ServletForwardingController();
//		controller.setServletName(ClassUtils.getShortNameAsProperty(SpringServlet.class));
//		return controller;
//	}
}
