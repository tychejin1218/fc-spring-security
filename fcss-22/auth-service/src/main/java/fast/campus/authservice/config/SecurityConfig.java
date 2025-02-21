package fast.campus.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig는 애플리케이션의 Spring Security 설정을 정의하는 클래스
 *
 * <p>비밀번호 인코딩과 HTTP 요청에 대한 필터 체인을 설정합니다.</p>
 */
@Configuration
public class SecurityConfig {

  /**
   * >비밀번호 암호화를 위해 BCryptPasswordEncoder를 Bean으로 등록
   * <p>BCrypt 암호화는 단방향 암호화 방식으로 안전한 비밀번호 관리에 사용됩니다.</p>
   *
   * @return PasswordEncoder 구현체인 BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Spring Security의 보안 필터 체인을 설정하는 Bean
   *
   * @param httpSecurity HttpSecurity 객체를 통해 필터 체인 설정
   * @return 설정된 SecurityFilterChain
   * @throws Exception 설정 과정 중 발생할 수 있는 예외
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    // CSRF(Cross-Site Request Forgery) 보호를 비활성화
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    // 모든 HTTP 요청을 인증 없이 허용
    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().permitAll());
    return httpSecurity.build();
  }
}
