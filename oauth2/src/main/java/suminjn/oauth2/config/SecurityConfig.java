// Spring Security 설정 클래스
// OAuth2(Google, Kakao, Naver, Apple 등) 로그인을 지원하며, 사용자 인증 성공 시 사용자 정보를 JSON으로 반환합니다.
package suminjn.oauth2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import suminjn.oauth2.service.CustomOAuth2UserService;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (API 서버 등에서는 보통 비활성화)
                .csrf(csrf -> csrf.disable())
                // 경로별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll() // 로그인 페이지는 모두 허용
                        .anyRequest().authenticated()) // 그 외는 인증 필요
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 커스텀 로그인 페이지 경로
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)) // 사용자 정보 처리 서비스
                        .successHandler(successHandler(objectMapper())) // 로그인 성공 시 사용자 정보 JSON 반환
                );

        return http.build();
    }

    /**
     * ObjectMapper 빈 등록
     * - 자바 객체(Map 등)를 JSON 문자열로 변환할 때 사용
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * OAuth2 로그인 성공 시 사용자 정보를 JSON으로 반환하는 핸들러
     * - user.getAttributes()에 담긴 모든 정보를 JSON으로 변환해 응답
     */
    @Bean
    public AuthenticationSuccessHandler successHandler(ObjectMapper objectMapper) {
        return (request, response, authentication) -> {
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = user.getAttributes();

            // 사용자 정보 전체를 JSON 문자열로 변환
            String body = objectMapper.writeValueAsString(attributes);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            PrintWriter writer = response.getWriter();
            writer.println(body);
            writer.flush();
        };
    }
}
