package fast.campus.movieservice.config;

import fast.campus.movieservice.security.filter.InitialAuthenticationFilter;
import fast.campus.movieservice.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Spring Security 설정을 위한 구성 클래스.
 *
 * <p>이 클래스는 애플리케이션의 HTTP 요청에 대한 보안 정책을 정의하며,
 * 인증 및 권한 부여를 관리하기 위해 Spring Security 필터 체인을 설정합니다.</p>
 *
 * <p>추가적으로, 자체적으로 정의한 필터(`InitialAuthenticationFilter`, `JwtAuthenticationFilter`)를
 * Spring Security 필터 체인의 특정 위치에 추가합니다.</p>
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final InitialAuthenticationFilter initialAuthenticationFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * Spring Security 필터 체인을 구성
   *
   * <p>기본적인 Spring Security 설정 외에 커스텀 인증 필터를 추가합니다.
   * 아래와 같은 보안 정책이 적용됩니다:</p>
   * <ul>
   *   <li>CSRF 보호 비활성화 (현재 상태에서는 필요 없다고 가정)</li>
   *   <li>InitialAuthenticationFilter를 BasicAuthenticationFilter 앞에 추가</li>
   *   <li>JwtAuthenticationFilter를 BasicAuthenticationFilter 뒤에 추가</li>
   *   <li>모든 요청에 대해 인증이 필요하도록 설정</li>
   * </ul>
   *
   * @param httpSecurity Spring Security의 HTTP 설정을 정의하는 객체
   * @return 구성된 SecurityFilterChain 객체
   * @throws Exception 보안 설정 중 발생할 수 있는 예외
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    httpSecurity.addFilterBefore(initialAuthenticationFilter, BasicAuthenticationFilter.class);
    httpSecurity.addFilterAfter(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().authenticated());

    return httpSecurity.build();
  }
}
