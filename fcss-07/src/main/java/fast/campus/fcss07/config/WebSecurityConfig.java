package fast.campus.fcss07.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 * <p>
 * 애플리케이션 보안을 구성하기 위해 인증 매니저, 필터 체인, 보안 커스터마이저를 설정
 * </p>
 *
 * <p>
 * <b>@EnableWebSecurity</b>
 * <ol>
 *   <li>Spring Security를 활성화하고, 기본 보안 설정을 커스터마이징할 수 있도록 지원</li>
 *   <li>WebSecurityConfigurerAdapter 대신 컴포넌트 기반 설정으로 보안을 구성</li>
 * </ol>
 * </p>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  /**
   * AuthenticationManager Bean을 생성
   * <p>
   * Spring Security의 기본 인증 관리를 위한 {@link AuthenticationManager}를 생성하고 반환
   * </p>
   *
   * @param authenticationConfiguration Authentication 설정 객체
   * @return AuthenticationManager 객체
   * @throws Exception 인증 매니저 생성 시 예외가 발생할 수 있음
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  /**
   * SecurityFilterChain Bean을 생성
   * <p>
   * HTTP 요청에 대한 보안 필터 체인을 구성
   *   <ol>
   *     <li>기본적으로 폼 로그인 방식을 사용하며, 로그인 성공 후 "/main" 페이지로 리다이렉트</li>
   *     <li>모든 요청에 대해 인증을 요구</li>
   *   </ol>
   * </p>
   *
   * @param http {@link HttpSecurity} 객체
   * @return SecurityFilterChain 객체
   * @throws Exception 보안 설정 중 예외가 발생할 수 있음
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // 로그인 성공 시 "/main"으로 이동
    http.formLogin(h -> h.defaultSuccessUrl("/main", true));

    // 모든 요청 인증 요구
    http.authorizeHttpRequests(h -> h.anyRequest().authenticated());

    return http.build();
  }

  /**
   * WebSecurityCustomizer Bean을 생성
   * <p>
   * 인증 제외 경로를 정의:
   * <ol>
   *     <li>"/api/v1/**" 경로는 인증없이 접근 가능</li>
   *     <li>추후 특정 경로나 요청을 추가하고 싶다면 여기에 규칙을 확장 가능</li>
   * </ol>
   * </p>
   *
   * @return WebSecurityCustomizer 객체
   */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return web -> {
      // 인증 제외 경로 정의
      web.ignoring().requestMatchers("/api/v1/**");
    };
  }
}
