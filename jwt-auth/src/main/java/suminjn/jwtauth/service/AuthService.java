package suminjn.jwtauth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import suminjn.jwtauth.dto.TokenResponse;
import suminjn.jwtauth.entity.Member;
import suminjn.jwtauth.entity.MemberRole;
import suminjn.jwtauth.repository.MemberRepository;
import suminjn.jwtauth.util.JwtUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;

  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  // 회원가입
  public String register(String username, String password, MemberRole role) {
    if (memberRepository.existsByUsername(username)) {
      throw new RuntimeException("이미 존재하는 사용자명입니다");
    }

    String encodedPassword = passwordEncoder.encode(password);
    Member member = Member.builder()
        .username(username)
        .password(encodedPassword)
        .memberRole(role)
        .build();
    memberRepository.save(member);

    return "회원가입이 완료되었습니다";
  }

  // 로그인
  public TokenResponse login(String username, String password) {
    Optional<Member> memberOpt = memberRepository.findByUsername(username);

    if (memberOpt.isEmpty()) {
      throw new RuntimeException("사용자명 또는 비밀번호가 올바르지 않습니다");
    }

    Member member = memberOpt.get();

    if (!passwordEncoder.matches(password, member.getPassword())) {
      throw new RuntimeException("사용자명 또는 비밀번호가 올바르지 않습니다");
    }

    // AccessToken과 RefreshToken 생성
    String accessToken = jwtUtil.generateAccessToken(username, member.getMemberRole().getValue());
    String refreshToken = jwtUtil.generateRefreshToken(username, member.getMemberRole().getValue());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  // 사용자 정보 조회
  public Member getMemberByUsername(String username) {
    return memberRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
  }

  // RefreshToken으로 AccessToken 재발급
  public TokenResponse refreshAccessToken(String refreshTokenStr) {
    try {
      // RefreshToken 검증
      if (!jwtUtil.validateRefreshToken(refreshTokenStr, jwtUtil.extractUsername(refreshTokenStr))) {
        throw new RuntimeException("유효하지 않은 리프레시 토큰입니다");
      }
      
      // RefreshToken에서 사용자 정보 추출
      String username = jwtUtil.extractUsername(refreshTokenStr);
      String role = jwtUtil.extractRole(refreshTokenStr);
      
      // 새로운 AccessToken 생성 (RefreshToken의 role 정보 사용)
      String newAccessToken = jwtUtil.generateAccessToken(username, role);
      
      return TokenResponse.builder()
          .accessToken(newAccessToken)
          .refreshToken(refreshTokenStr) // 기존 RefreshToken 재사용
          .build();
    } catch (Exception e) {
      throw new RuntimeException("토큰 갱신에 실패했습니다: " + e.getMessage());
    }
  }

  // 로그아웃 (클라이언트 측에서 토큰 삭제 권장)
  public void logout(String username) {
    // JWT는 stateless하므로 서버에서 별도의 로그아웃 처리는 불필요
    // 클라이언트에서 토큰을 삭제하도록 안내
  }
}
