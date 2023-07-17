package com.cos.security.config.auth;

// 시큐리티가 /login 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되면 시큐리티 session 을 만들어줌 (security ContextHolder 에 세션 정보를 저장 key 값을 가짐) 시큐리티가 가지고 있는 세션임
// 오브젝트 -> Authentication 타입의 객체가 들어갈 수 있음
// Authentication 안에 user 정보가 있어야함
// user 오브젝트 타입 -> userDetails 타입

import com.cos.security.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

// Security Session -> Authentication -> userDetails(PrincipalDetails)
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
	// UserDetails, OAuth2User 두 가지 타입을 PrincipalDetails 타입으로 묶어서 시큐리티 세션에 저장, 각각 메서드들은 오버라이드해서 사용

	private User user; // composition
	private Map<String, Object> attributes;

	// 일반 로그인 생성자
	public PrincipalDetails(User user) {
		this.user = user;
	}

	// Oauth 로그인 생성자
	public PrincipalDetails(User user, Map<String, Object> attributes) { //attributes 를 토대로 user 객체를 만들어야함
		this.user = user;
		this.attributes = attributes;
	}

	// 해당 user 의 권한을 리턴하는 곳

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>(); // GrantedAuthority 타입으로 맞춰줘
		collect.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole(); // 이게 권한임
			}
		});
		return collect;
	}
	// password 리턴

	@Override
	public String getPassword() {
		return user.getPassword();
	}
	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	// 비밀번호 유효기간 만료되었는지

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		// 사이트에서 1년동안 로그인을 안하면 휴먼유저로 분류하기로 함 이런경우 사용됨
		// 현재시간 - 마지막으로 로그인한 시간 > 1년 -> false
		return true;
	}

	// Oauth 메서드
	// - {sub=113676134780663010177,
	// - name=이상훈, given_name=상훈, family_name=이,
	// - picture=https://lh3.googleusercontent.com/a/AAcHTtfc9NCakKqJIH5R1j1obxOWqNZ25twNNE7lGe-HTchd=s96-c,
	// - [email=tkdtkd975@gmail.com](mailto:email=tkdtkd975@gmail.com),
	// - email_verified=true, locale=ko}
	@Override
	public String getName() {
		String sub = (String) attributes.get("sub");
		return sub; // 별로 안중요하고 쓰지도 않음
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
