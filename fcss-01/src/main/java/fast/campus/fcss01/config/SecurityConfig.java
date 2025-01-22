package fast.campus.fcss01.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 기본 설정을 커스터마이징하는 클래스
 *
 * <ul>
 *   <li>UserDetailsService: 사용자 관리</li>
 *   <li>PasswordEncoder: 비밀번호 관리</li>
 * </ul>
 *
 * <p>AuthenticationProvider는 인증 기능을 제공하며,
 * 사용자 관리와 비밀번호 관리를 각각 UserDetailsService와 PasswordEncoder에 위임합니다.</p>
 */
@Configuration
public class SecurityConfig {

  /**
   * 사용자 정보를 메모리에 저장하기 위한 UserDetailsService 설정
   *
   * @return InMemoryUserDetailsManager를 사용한 사용자 정보 관리 객체
   */
  @Bean
  public UserDetailsService userDetailsService() {
    InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();

    UserDetails danny = User.builder()
        .username("danny")
        .password("password")
        .build();
    inMemoryUserDetailsManager.createUser(danny);
    return inMemoryUserDetailsManager;
  }

  /**
   * PasswordEncoder 설정
   *
   * <p>커스터마이징된 UserDetailsService를 사용할 경우 PasswordEncoder도 명시적으로 설정해야 합니다.</p>
   *
   * @return BCryptPasswordEncoder를 사용한 비밀번호 암호화 객체
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    // NoOpPasswordEncoder: 비밀번호에 암호화를 적용하지 않고 평문 그대로 사용
    //return NoOpPasswordEncoder.getInstance();
    return new BCryptPasswordEncoder();
  }

  /**
   * 엔드포인트 보안 설정
   *
   * <p>SecurityFilterChain을 정의하여 요청 인증 방식을 설정합니다.</p>
   *
   * @param httpSecurity HttpSecurity 객체
   * @return 접근 권한 및 인증 방식이 설정된 SecurityFilterChain 객체
   * @throws Exception 설정 중 발생 가능한 예외
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        // 모든 요청에 대한 인증을 요구
        .authorizeHttpRequests((auth) -> auth.anyRequest().authenticated())

        // permitAll()으로 변경하면 별도의 자격 증명 없이 REST 엔드포인트를 호출할 수 있음
        //.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())

        // HTTP Basic 인증 방식 활성화
        .httpBasic(withDefaults());

    return httpSecurity.build();
  }
}
