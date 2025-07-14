package suminjn.jwtauth.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import suminjn.jwtauth.entity.Member;
import suminjn.jwtauth.entity.MemberRole;
import suminjn.jwtauth.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    // 기본 사용자 생성 (테스트용)
    if (!memberRepository.existsByUsername("admin")) {
      Member admin = Member.builder()
          .username("admin")
          .password(passwordEncoder.encode("admin123"))
          .memberRole(MemberRole.ADMIN)
          .build();
      memberRepository.save(admin);
      System.out.println("관리자 계정이 생성되었습니다: 사용자명=admin, 비밀번호=admin123");
    }

    if (!memberRepository.existsByUsername("user")) {
      Member user = Member.builder()
          .username("user")
          .password(passwordEncoder.encode("user123"))
          .memberRole(MemberRole.USER)
          .build();
      memberRepository.save(user);
      System.out.println("일반 사용자 계정이 생성되었습니다: 사용자명=user, 비밀번호=user123");
    }
  }
}
