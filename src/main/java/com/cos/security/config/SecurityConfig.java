package com.cos.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// 비밀번호 암호화 빈등록
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// 문법이 많이 변하고 있음
		httpSecurity.csrf().disable();
		httpSecurity.authorizeHttpRequests()
				.requestMatchers("/user/**").authenticated() // 인증만 되면 들어갈 수 있는 주소
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // 인증, 권한 둘다 있어야함
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().permitAll() // 나머지는 전부 권한없음
				.and().formLogin().loginPage("/loginForm") // 권한이 필요한 user, admin, manager 로 가면 loginForm 으로 가도록 설정
				//.usernameParameter("username2") // PrincipalDetailService 에 username 을 다른걸로 하고 싶다면 이렇게 설정
				.loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행
				// 위에 꺼 하면 controller 에 /login 안만들어도됨
				.defaultSuccessUrl("/"); // /loginForm 으로 로그인 하면 메인 페이지로 이동하게 함
				// 근데 컨트롤러로 다른 페이지 가게 설정해놓으면 설정한 곳으로 보내줄게 ex. user

		return httpSecurity.build();
	}
}
