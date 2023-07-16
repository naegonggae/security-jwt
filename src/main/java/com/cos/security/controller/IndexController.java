package com.cos.security.controller;

import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bc;

	@GetMapping({"", "/"})
	public String index() {
		return "index";
	}

	@GetMapping("user")
	public String user() {
		return "user";
	}

	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}

	@GetMapping("/manager")
	public String manager() {
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
}
