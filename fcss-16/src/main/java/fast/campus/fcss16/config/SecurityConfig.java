package fast.campus.fcss16.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스.
 */
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

  /**
   * 인메모리 사용자 인증 정보 설정.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

    // 사용자 'danny.kim' 생성 (WRITE 권한 있음)
    manager.createUser(User.withUsername("danny.kim")
        .password("12345")
        .authorities("WRITE")
        .build());

    // 사용자 'steve.kim' 생성 (READ 권한 있음)
    manager.createUser(User.withUsername("steve.kim")
        .password("12345")
        .authorities("READ")
        .build());

    return manager;
  }

  /**
   * NoOpPasswordEncoder 설정 (암호화를 사용하지 않음).
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  /**
   * Security Filter 설정.
   *
   * @param httpSecurity Security 설정 객체
   * @return SecurityFilterChain 객체
   * @throws Exception 설정 중 문제가 발생하면 예외를 던짐
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .authorizeHttpRequests(c -> c.anyRequest().permitAll()) // 모든 요청 허용
        .httpBasic(Customizer.withDefaults()) // HTTP Basic 인증 활성화
        .csrf(AbstractHttpConfigurer::disable); // CSRF 보호 비활성화
    return httpSecurity.build();
  }
}
