package fast.campus.movieservice.security;

import fast.campus.movieservice.delegator.AuthenticationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

  private final AuthenticationDelegator delegator;

  /**
   * 사용자 인증을 처리
   *
   * <p>입력받은 사용자 ID와 비밀번호를 {@link AuthenticationDelegator}를 통해
   * 외부 서비스로 검증 요청을 보낸 후, 인증 성공 시 {@link UsernamePasswordAuthenticationToken}을 반환합니다.</p>
   *
   * @param authentication 사용자가 입력한 인증 정보
   * @return 인증에 성공했을 경우, {@link UsernamePasswordAuthenticationToken} 반환
   * @throws AuthenticationException 인증 실패 시 예외를 발생
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    // 사용자 ID 및 비밀번호 가져오기
    String userId = authentication.getName();
    String password = String.valueOf(authentication.getCredentials());

    // 외부 인증 서비스 호출
    delegator.restAuth(userId, password);

    // 인증 성공 시 인증 객체 반환
    return new UsernamePasswordAuthenticationToken(userId, password);
  }

  /**
   * 이 AuthenticationProvider가 특정 인증 토큰 클래스를 지원하는지 여부를 반환
   *
   * <p>이 메서드는 Spring Security가 인증 작업을 위임할 수 있는 AuthenticationProvider를 선택하는 데 사용됩니다.</p>
   *
   * @param authentication 지원 여부를 확인할 인증 클래스
   * @return {@link UsernamePasswordAuthentication} 클래스 또는 하위 클래스일 경우 <code>true</code>, 그렇지 않으면 <code>false</code>
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthentication.class.isAssignableFrom(authentication);
  }
}
