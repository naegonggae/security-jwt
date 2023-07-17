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
		System.out.println("getClientRegistration = " + userRequest.getClientRegistration()); // getClientRegistrationID 로 어떤 OAuth 로 로그인했는지 알수 있음
		System.out.println("getAccessToken = " + userRequest.getAccessToken());
		System.out.println("getTokenValue = " + userRequest.getAccessToken().getTokenValue());
		System.out.println("userRequest = " + super.loadUser(userRequest).getAttributes());
		// getAttributes 정보로 강제 회원가입 진행 // 다음강의에 회원가입을 강제로 진행해볼 예정인데 이미 로그인됐는데...

		OAuth2User oAuth2User = super.loadUser(userRequest);
		return super.loadUser(userRequest);
		// 구글 로그인 버튼클릭 -> 로그인 창 -> 로그인완료 -> code 를 리턴(OAuth-client 라이브러리가 받아줌) -> AccessToken 요청
		// 요청 받은것 까지가 userRequest 정보 -> loadUser 함수 -> 구글로부터 회원프로필정보 받아줌
	}
}
