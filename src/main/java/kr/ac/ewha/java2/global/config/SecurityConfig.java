package kr.ac.ewha.java2.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 해제 (API 서버 필수)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. 기본 로그인 폼 및 HTTP Basic 인증 해제 (중요! 403 에러 해결 핵심)
            // 이걸 안 끄면 우리가 만든 /login 컨트롤러 대신 시큐리티 로그인 페이지가 가로챕니다.
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 3. CORS 허용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 4. 경로별 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 로그인 없이 허용할 경로들
                .requestMatchers(
                        "/api/**", // API 경로
                        "/ws/**", "/ws/chat", "/ws/lobby", // 소켓 경로
                        "/", "/index.html", "/lobby.html", "/signup.html", "/gameroom.html",
                        "/result.html",
                        "/css/**", "/js/**", "/img/**"

                ).permitAll()
                
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            );

        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/api/rooms/**");
    }

    // CORS 설정 (모든 출처 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // setAllowedOriginPatterns("*")는 모든 IP의 접속을 허용합니다.
        configuration.setAllowedOriginPatterns(List.of("*")); 
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}