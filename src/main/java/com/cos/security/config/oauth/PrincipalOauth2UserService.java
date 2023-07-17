package com.cos.security.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	// 구글로 받은 userRequest 데이터에 대한 후처리되는 함수
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("userRequest = " + userRequest);
		// userRequest = org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest@4c50babc
		System.out.println("getClientRegistration = " + userRequest.getClientRegistration());
		System.out.println("getAccessToken = " + userRequest.getAccessToken());
		System.out.println("getTokenValue = " + userRequest.getAccessToken().getTokenValue());
		System.out.println("userRequest = " + super.loadUser(userRequest).getAttributes());
		// getAttributes 정보로 강제 회원가입 진행 // 다음강의에 회원가입을 강제로 진행해볼 예정인데 이미 로그인됐는데...
		return super.loadUser(userRequest);
	}
}
