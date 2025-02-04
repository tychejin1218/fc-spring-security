package fast.campus.fcss07.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 사용자 인증을 처리하는 AuthenticationProvider 구현 클래스
 * <p>
 * username과 password를 검증하여 인증을 처리하며, 특정 알고리즘(SCRYPT, BCRYPT)을 지원
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationProviderService implements AuthenticationProvider {

  private final JpaUserDetailService jpaUserDetailService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final SCryptPasswordEncoder sCryptPasswordEncoder;

  /**
   * 사용자 인증 처리
   *
   * @param authentication 인증 요청 정보 (username, password 등)
   * @return 인증 성공 시 {@link UsernamePasswordAuthenticationToken} 반환
   * @throws AuthenticationException 인증 실패 시 예외 발생
   */
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    CustomUserDetails user = (CustomUserDetails) jpaUserDetailService.loadUserByUsername(username);

    return switch (user.getUser().getAlgorithm()) {
      case BCRYPT -> checkPassword(user, password, bCryptPasswordEncoder);
      case SCRYPT -> checkPassword(user, password, sCryptPasswordEncoder);
    };
  }

  /**
   * 해당 Authentication 타입을 지원하는지 확인
   *
   * @param authentication 지원 여부를 확인할 Authentication 타입
   * @return 지원 여부 (true/false)
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  /**
   * 사용자 비밀번호 검증
   *
   * @param user            사용자 정보
   * @param rawPassword     입력된 비밀번호
   * @param passwordEncoder 비밀번호 암호화 알고리즘
   * @return 인증 성공 시 {@link UsernamePasswordAuthenticationToken} 반환
   * @throws BadCredentialsException 비밀번호가 맞지 않을 시 발생
   */
  private Authentication checkPassword(
      CustomUserDetails user,
      String rawPassword,
      PasswordEncoder passwordEncoder) {

    if (passwordEncoder.matches(rawPassword, user.getPassword())) {
      return new UsernamePasswordAuthenticationToken(
          user.getUsername(),
          user.getPassword(),
          user.getAuthorities()
      );
    }

    throw new BadCredentialsException("Bad credentials");
  }
}
