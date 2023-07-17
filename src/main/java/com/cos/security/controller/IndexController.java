package com.cos.security.controller;

import com.cos.security.config.auth.PrincipalDetails;
import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bc;

	// 일반 로그인
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication, //1번 다운캐스팅을 통해 principalDetails.getUser() 찾기 가능
			@AuthenticationPrincipal PrincipalDetails userDetails) { // 세션정보에 접근 가능 //2번 어노테이션을 통해 principalDetails.getUser() 찾기 가능
		// @AuthenticationPrincipal UserDetails userDetails 근데 PrincipalDetails 에 보면 UserDetails 를 상속받고 있음 -> PrincipalDetails 이렇게 바꿔도 됨
		System.out.println("testLogin==================");

		//1번
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // UserDetails 를 PrincipalDetails 로 다운캐스팅
		System.out.println("principalDetails.getUser() = " + principalDetails.getUser());
		// principalDetails.getUser() = User(id=1, username=qwe, password=$2a$10$ulSs51KNCGAD2jWP4VVlXOMIXXOepv5lw4LhQwER.Vbrfu4Mru.yW, email=123@123, role=ROLE_USER, provider=null, providerId=null, createDate=2023-07-16 23:33:24.705507)

		//System.out.println(authentication.getPrincipal());
		//com.cos.security.config.auth.PrincipalDetails@6b50c96f, Object 타입

		//System.out.println("userDetails = " + userDetails.getUsername());

		//2번
		System.out.println("userDetails = " + userDetails.getUser());
		return "세션정보 확인하기";
	}

	// Oauth 로그인
	@GetMapping("/test/Oauth/login")
	public @ResponseBody String testOathLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User user) { // PrincipalDetails 타입으로 캐스팅 할수 없다고 뜸
		System.out.println("testOauthLogin==================");

		//1번
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // UserDetails 를 OAuth2User 로 다운캐스팅
		System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

		System.out.println("user = " + user.getAttributes());

		return "Oauth 세션정보 확인하기";
	}

	// 세션중에 시큐리티가 관리하는 세션이 있음 -> 내부에 Authentication 객체만 있음 -> DI 를 했기때문에 컨트롤러에서 꺼내올수있음
	// Authentication 객체 안에는 2가지 타입이 들어갈 수 있음 -> UserDetails, Oauth2User
	// Authentication 이 시큐리티 세션에 들어가는 순간 -> 로그인이 된거임
	// UserDetails -> 일반 로그인
	// Oauth2User -> Oauth 로그인
	// 그럼 컨트롤러에서 꺼낼때 마다 구분을 해서 사용을 해야하나? -> PrincipalDetails 가 두 타입을 상속받게 해서 Authentication 에 저장하면 해결
	// 즉, PrincipalDetails 형태로 시큐리티 세션에 저장한다.

	// 회원가입을 하고 UserDetails 로 오던 Oauth2User 로 오던 user 오브젝트가 필요함 -> 근데 두 타입은 user 오브젝트를 포함하고 있지 않음
	// 그래서 PrincipalDetails 클래스를 만들고 두 타입을 상속받음 -> user 를 품어놔서 user 오브젝트 사용가능

	@GetMapping({"", "/"})
	public String index() {
		return "index";
	}

	// Oauth 로그인을 해도 일반로그인을 해도 PrincipalDetails 로 받을 수 있음
	@GetMapping("user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails = " + principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}

	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");

		// 비밀번호 암호화
		String rowPassword = user.getPassword();
		String encPassword = bc.encode(rowPassword);
		user.setPassword(encPassword);

		userRepository.save(user); // 비밀번호가 노출됨
		return "redirect:/loginForm";
	}

	@GetMapping("/joinProc")
	public @ResponseBody String joinProc() {
		return "회원가입이 완료되었습니다";
	}

	@Secured("ROLE_ADMIN") // 권한을 간단히 걸음
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')") // 여러개의 권한을 걸음
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터 정보";
	}
}
