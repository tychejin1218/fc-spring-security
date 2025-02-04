package fast.campus.fcss07.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

/**
 * Spring Security 설정 클래스
 * <p>
 * 애플리케이션의 비밀번호 암호화를 위해 BCrypt와 SCrypt 알고리즘 기반의 Encoder를 Bean으로 등록
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

  /**
   * BCryptPasswordEncoder Bean을 생성
   *
   * <p>
   * BCrypt 알고리즘 기반의 비밀번호 암호화 기능을 제공, 연산 비용(Cost Factor)은 기본값(10)을 사용
   * </p>
   *
   * @return BCryptPasswordEncoder 객체
   */
  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * SCryptPasswordEncoder Bean을 생성
   *
   * <p>
   * SCrypt 알고리즘은 메모리와 연산 복잡성을 높여 더욱 안전한 비밀번호 암호화가 가능
   * </p>
   *
   * <p>
   * 생성자 매개변수를 통해 알고리즘의 동작 방식을 세부 설정
   * <ul>
   *     <li>CPU 비용 인자: 10</li>
   *     <li>병렬화 비용 인자: 1</li>
   *     <li>메모리 비용 인자: 1</li>
   *     <li>블록 크기(라운드): 1</li>
   *     <li>해시 길이: 1</li>
   * </ul>
   * </p>
   *
   * @return SCryptPasswordEncoder 객체
   */
  @Bean
  public SCryptPasswordEncoder sCryptPasswordEncoder() {
    return new SCryptPasswordEncoder(10, 1, 1, 1, 1);
  }
}
