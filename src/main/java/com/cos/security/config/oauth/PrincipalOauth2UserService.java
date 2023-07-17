package com.cos.security.config.oauth;

import com.cos.security.config.auth.PrincipalDetails;
import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

//	@Autowired
//	BCryptPasswordEncoder bCryptPasswordEncoder; // 순환 참조문제
	private final UserRepository userRepository;

	// 구글로 받은 userRequest 데이터에 대한 후처리되는 함수
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.

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

		// 회원가입을 강제로 진행
		String provider = userRequest.getClientRegistration().getClientId(); // google
		String providerId = oAuth2User.getAttribute("sub");
		String username = provider + "_" + providerId; // google_1231289385
		String password = "겟인데어";
		String email = oAuth2User.getAttribute("email");
		String role = "ROLE_USER";

		User userEntity = userRepository.findByUsername(username);
		if (userEntity == null) {
			System.out.println("구글로그인이 최초입니다");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		} else {
			System.out.println("구글로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되었습니다.");
		}
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); // authentication 객체안에 들어감
		// 일반 로그인이면 User 만들고 있겠지만, Oauth 로그인은 맵으로 user 와 attributes 를 들고 있음
		//return super.loadUser(userRequest);

		// 구글 로그인 버튼클릭 -> 로그인 창 -> 로그인완료 -> code 를 리턴(OAuth-client 라이브러리가 받아줌) -> AccessToken 요청
		// 요청 받은것 까지가 userRequest 정보 -> loadUser 함수 -> 구글로부터 회원프로필정보 받아줌
	}
}
