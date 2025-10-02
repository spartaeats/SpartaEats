package com.sparta.sparta_eats.global.infrastructure.config.security;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.addBasenames("messages.commons", "messages.errors", "messages.validations");
		ms.setDefaultEncoding("UTF-8");
		ms.setUseCodeAsDefaultMessage(true); // 메시지 코드가 없으면 코드 자체를 메시지로 사용
		return ms;
	}
}
