package fast.campus.fcss07.security;

import fast.campus.fcss07.domain.user.User;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 사용자 인증 및 권한 처리를 위한 UserDetails 구현 클래스
 * <p>
 * 사용자 정보를 포함하며, Spring Security의 인증 및 권한 로직에서 사용
 * </p>
 */
@Getter
public class CustomUserDetails implements UserDetails {

  private final User user;

  /**
   * 사용자 권한 정보를 초기화
   *
   * @param user 사용자 데이터
   */
  public CustomUserDetails(User user) {
    this.user = user;
  }

  /**
   * 사용자의 권한 반환
   *
   * @return GrantedAuthority 목록
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities().stream().map(each -> new SimpleGrantedAuthority(each.getName()))
        .toList();
  }

  /**
   * 사용자 비밀번호 반환
   *
   * @return 사용자 비밀번호
   */
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  /**
   * 사용자 이름 반환
   *
   * @return 사용자 username
   */
  @Override
  public String getUsername() {
    return user.getUsername();
  }
}
