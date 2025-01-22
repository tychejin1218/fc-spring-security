package fast.campus.fcss01.config;

import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 사용자 정의 인증 공급자 (CustomAuthenticationProvider)
 *
 * <p>Spring Security의 AuthenticationProvider를 구현하여 인증 논리를 커스터마이징합니다.</p>
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  /**
   * 사용자 인증 로직을 구현
   *
   * @param authentication 사용자로부터 입력받은 인증 정보 (username, password 등)
   * @return 인증에 성공한 사용자 정보가 포함된 Authentication 객체
   * @throws AuthenticationException 인증 실패 시 발생하는 예외
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = String.valueOf(authentication.getCredentials());

    if ("danny".equals(username) && "password".equals(password)) {
      return new UsernamePasswordAuthenticationToken(username, password, List.of());
    }

    throw new RuntimeException("auth error");
  }

  /**
   * 지원하는 인증 타입을 지정
   *
   * <p>AuthenticationProvider가 처리할 수 있는 Authentication 객체의 타입을 정의합니다.</p>
   *
   * @param authentication 확인할 Authentication 타입
   * @return 지원 여부 (true: 지원, false: 지원하지 않음)
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
