package com.cos.security.config;

import com.cos.security.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터 체인에 등록됩니다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secure 어노테이션 활성화, preAuthorize, postAuthorize 어노테이션 활성화
public class SecurityConfig {

	private final PrincipalOauth2UserService principalOauth2UserService;

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
				.defaultSuccessUrl("/") // /loginForm 으로 로그인 하면 메인 페이지로 이동하게 함
				// 근데 컨트롤러로 다른 페이지 가게 설정해놓으면 설정한 곳으로 보내줄게 ex. user
				.and()
				.oauth2Login()
				.loginPage("/loginForm") // 이렇게 해주면 404 안뜸 // 구글로그인이 완료된 뒤에 후처리가 필요해야하는데 그냥 로그인됨...
				.userInfoEndpoint()
				.userService(principalOauth2UserService); // 회원가입이 완료되면 <코드>를 받지 않고 <엑세스 토큰 + 사용자프로필정보>를 받는다



		return httpSecurity.build();
	}
}
// 1.코드받기(인증) 2.엑세스토큰 받음(권한) 3.사용자프로필 정보를 가져오고 4-1.그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
// 4-2 만약 쇼핑몰이라 기본정보(이메일, 이름, 아이디, 전화번호) 외에 부가정보(집주소, 회원등급)가 필요하면 별도의 회원가입창으로 이동해서 진행