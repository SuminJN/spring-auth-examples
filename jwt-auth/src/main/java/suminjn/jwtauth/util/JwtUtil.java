package suminjn.jwtauth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // JWT 시크릿 키 (실제 운영환경에서는 환경변수로 관리)
    private final String SECRET_KEY = "JWT_SECRET_KEY_EXAMPLE_1234567890";
    
    // AccessToken 유효시간 (15분)
    private final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 15;
    
    // RefreshToken 유효시간 (7일)
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 공통 토큰 생성 메서드
    private String generateToken(String username, String role, String type, long expirationTime) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    // Access Token 생성
    public String generateAccessToken(String username, String role) {
        return generateToken(username, role, "access", ACCESS_TOKEN_EXPIRATION_TIME);
    }

    // Refresh Token 생성 (role 포함)
    public String generateRefreshToken(String username, String role) {
        return generateToken(username, role, "refresh", REFRESH_TOKEN_EXPIRATION_TIME);
    }

    // JWT 토큰에서 사용자명 추출
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // JWT 토큰에서 역할 추출
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // JWT 토큰에서 Claims 추출
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // JWT 토큰에서 토큰 타입 추출
    public String extractTokenType(String token) {
        return extractClaims(token).get("type", String.class);
    }

    // Access Token인지 확인
    public boolean isAccessToken(String token) {
        try {
            return "access".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Refresh Token인지 확인
    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Access Token 유효성 검증 (역할 정보 포함)
    public boolean validateAccessToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && 
                   !isTokenExpired(token) && 
                   isAccessToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Refresh Token 유효성 검증
    public boolean validateRefreshToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && 
                   !isTokenExpired(token) && 
                   isRefreshToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
