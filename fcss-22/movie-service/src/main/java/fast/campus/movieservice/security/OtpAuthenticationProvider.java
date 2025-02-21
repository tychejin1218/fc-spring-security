package fast.campus.movieservice.security;

import fast.campus.movieservice.delegator.AuthenticationDelegator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

  private final AuthenticationDelegator delegator;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String userId = authentication.getName();
    String otp = String.valueOf(authentication.getCredentials());

    // 외부 인증 서비스에 OTP 검증 요청
    boolean result = delegator.restOtp(userId, otp);

    if (result) {
      return new OtpAuthentication(userId, otp);
    }

    throw new BadCredentialsException("Invalid OTP");
  }

  /**
   * 이 AuthenticationProvider가 특정 인증 클래스를 지원하는지 여부를 반환
   *
   * <p>{@link OtpAuthentication} 타입의 인증만 처리하도록 설정합니다.</p>
   *
   * @param authentication 지원 여부를 확인할 인증 클래스.
   * @return {@link OtpAuthentication} 클래스나 하위 클래스일 경우 true, 그렇지 않으면 false.
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return OtpAuthentication.class.isAssignableFrom(authentication);
  }
}
