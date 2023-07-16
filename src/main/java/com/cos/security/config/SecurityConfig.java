package com.cos.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// 문법이 많이 변하고 있음
		httpSecurity.csrf().disable();
		httpSecurity.authorizeHttpRequests()
				.requestMatchers("/user/**").authenticated()
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().permitAll() // 나머지는 전부 권한없음
				.and().formLogin().loginPage("/login"); // 권한이 필요한 user, admin, manager 로 가면 login 으로 가도록 설정

		return httpSecurity.build();
	}
}
