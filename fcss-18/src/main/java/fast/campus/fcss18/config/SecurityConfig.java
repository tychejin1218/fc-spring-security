package fast.campus.fcss18.config;

import fast.campus.fcss18.evaluator.DocumentsPermissionEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Spring Security 설정 클래스로, 메서드 보안과 사용자 관리, 권한 평가를 설정합니다.
 *
 * <p>이 클래스는 다음과 같은 역할을 수행합니다:</p>
 * <ul>
 *   <li>메서드 보안 활성화 ({@link EnableMethodSecurity})</li>
 *   <li>In-memory 사용자 관리 서비스 설정</li>
 *   <li>커스텀 {@link DocumentsPermissionEvaluator}를 보안 로직에 연결</li>
 *   <li>비밀번호 인코더 설정</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final DocumentsPermissionEvaluator documentsPermissionEvaluator;

  /**
   * {@link MethodSecurityExpressionHandler} Bean을 정의합니다.
   *
   * <p>이 메서드는 {@link DefaultMethodSecurityExpressionHandler}를 생성하고,
   * {@link DocumentsPermissionEvaluator}를 이를 통해 등록합니다.</p>
   *
   * <p><strong>DocumentPermissionEvaluator 설정 방법:</strong></p>
   * <ul>
   *   <li>{@link MethodSecurityExpressionHandler}에 {@link DocumentsPermissionEvaluator}를 설정하면 됩니다.</li>
   *   <li>Spring Security는 기본적으로 {@link DefaultMethodSecurityExpressionHandler}를 제공합니다.</li>
   * </ul>
   *
   * @return 커스텀 권한 평가를 지원하는 {@link MethodSecurityExpressionHandler}
   */
  @Bean
  public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
    DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
    handler.setPermissionEvaluator(documentsPermissionEvaluator);

    return handler;
  }

  /**
   * In-memory 사용자 관리 서비스를 구성합니다.
   *
   * <p>3명의 사용자를 등록합니다:</p>
   * <ul>
   *   <li><strong>danny.kim:</strong> 비밀번호: 12345, 역할: ROLE_admin</li>
   *   <li><strong>steve.kim:</strong> 비밀번호: 12345, 역할: ROLE_user</li>
   *   <li><strong>harris.kim:</strong> 비밀번호: 12345, 역할: ROLE_user</li>
   * </ul>
   *
   * @return In-memory 기반 사용자 관리 서비스 ({@link InMemoryUserDetailsManager})
   */
  @Bean
  public UserDetailsService userDetailsService() {
    InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
    manager.createUser(
        User.withUsername("danny.kim")
            .password("12345")
            .roles("admin")
            .build()
    );
    manager.createUser(
        User.withUsername("steve.kim")
            .password("12345")
            .roles("user")
            .build()
    );
    manager.createUser(
        User.withUsername("harris.kim")
            .password("12345")
            .roles("user")
            .build()
    );
    return manager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
