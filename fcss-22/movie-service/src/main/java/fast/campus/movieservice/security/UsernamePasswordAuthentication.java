package fast.campus.movieservice.security;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UsernamePasswordAuthentication extends UsernamePasswordAuthenticationToken {

  /**
   * 사용자 이름과 비밀번호를 기반으로 인증 객체를 생성
   *
   * @param principal   사용자 이름(주체)
   * @param credentials 사용자 비밀번호(자격 증명)
   */
  public UsernamePasswordAuthentication(Object principal, Object credentials) {
    super(principal, credentials);
  }

  /**
   * 사용자 이름, 비밀번호, 권한 정보를 기반으로 인증 객체를 생성
   *
   * @param principal   사용자 이름(주체)
   * @param credentials 사용자 비밀번호(자격 증명)
   * @param authorities 사용자의 권한 정보
   */
  public UsernamePasswordAuthentication(Object principal, Object credentials,
      Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }
}
