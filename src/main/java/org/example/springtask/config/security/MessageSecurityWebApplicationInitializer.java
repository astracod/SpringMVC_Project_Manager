package org.example.springtask.config.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

@Order(2)
public class MessageSecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

}
