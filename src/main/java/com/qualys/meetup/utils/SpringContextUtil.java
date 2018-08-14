package com.qualys.meetup.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by ardas on 8/13/2018.
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static Object getBean(Class serviceName) {
		return applicationContext.getBean(serviceName);
	}

	public static Object getBean(String serviceName) {
		return applicationContext.getBean(serviceName);
	}

	public static ObjectMapper getObjectMapper() {
		return applicationContext.getBean(ObjectMapper.class);
	}

}
