package suminjn.jwtauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import suminjn.jwtauth.dto.LoginRequest;
import suminjn.jwtauth.dto.RefreshTokenRequest;
import suminjn.jwtauth.dto.RegisterRequest;
import suminjn.jwtauth.dto.TokenResponse;
import suminjn.jwtauth.entity.Member;
import suminjn.jwtauth.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        try {
            String message = authService.register(request.getUsername(), request.getPassword(), request.getRole());
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        try {
            TokenResponse tokenResponse = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(tokenResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            throw new RuntimeException("로그인에 실패했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(tokenResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            throw new RuntimeException("토큰 갱신에 실패했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        try {
            String username = authentication.getName();
            authService.logout(username);
            Map<String, String> response = new HashMap<>();
            response.put("message", "로그아웃이 완료되었습니다");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Member> getProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            Member member = authService.getMemberByUsername(username);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "안녕하세요 관리자님! 이곳은 보호된 엔드포인트입니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> userEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "안녕하세요 사용자님! 이곳은 보호된 엔드포인트입니다.");
        return ResponseEntity.ok(response);
    }
}
