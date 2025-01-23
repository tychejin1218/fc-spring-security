package fast.campus.fcss03.config;

import fast.campus.fcss03.user.InMemoryUserDetailsService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

  /**
   * 사용자 인증을 관리하기 위한 {@link UserDetailsService}를 정의합니다.
   * <p>
   * 이 메서드는 3명의 임시 사용자 데이터를 메모리에 저장하는 {@link InMemoryUserDetailsService}를 빈으로 등록합니다.
   * <p>
   * 사용자 이름(아이디):비밀번호
   * <ul>
   *   <li>danny.kim:12345</li>
   *   <li>steve.kim:23456</li>
   *   <li>harris.kim:34567</li>
   * </ul>
   *
   * @return UserDetailsService (InMemoryUserDetailsService 구현체)
   */
  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails danny = User.withUsername("danny.kim")
        .password("12345")
        .build();
    UserDetails steve = User.withUsername("steve.kim")
        .password("23456")
        .build();
    UserDetails harris = User.withUsername("harris.kim")
        .password("34567")
        .build();
    List<UserDetails> users = List.of(danny, steve, harris);
    return new InMemoryUserDetailsService(users);
  }

  /**
   * 비밀번호를 암호화/검증하는 {@link PasswordEncoder}를 정의합니다.
   * <p>
   * 여기서는 단순한 비밀번호 검증을 위해 Spring Security에서 제공하는 {@link NoOpPasswordEncoder}를 사용합니다. 이
   * PasswordEncoder는 입력된 비밀번호를 암호화하지 않고 검증합니다.
   * </p>
   * <b>주의:</b>
   * - 이 설정은 암호화가 적용되지 않으므로 프로덕션 환경에서는 절대로 사용하지 말아야 합니다.
   *
   * @return PasswordEncoder (NoOpPasswordEncoder 인스턴스)
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
