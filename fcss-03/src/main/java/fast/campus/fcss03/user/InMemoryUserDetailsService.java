package fast.campus.fcss03.user;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 인메모리 기반 {@link UserDetailsService} 구현체.
 * <p>
 * 이 클래스는 Spring Security에서 사용자 인증 처리를 위해 {@link UserDetailsService} 인터페이스를 구현한 인메모리 사용자 저장소를
 * 제공합니다.
 * <p>
 * 사용자 데이터는 {@link UserDetails} 객체의 목록으로 관리되며, 사용자 이름(username)을 통해 검색됩니다.
 */
public class InMemoryUserDetailsService implements UserDetailsService {

  private final List<UserDetails> users;

  /**
   * {@link InMemoryUserDetailsService} 생성자.
   *
   * @param users 사용자 데이터를 포함하는 {@link UserDetails} 목록
   */
  public InMemoryUserDetailsService(List<UserDetails> users) {
    this.users = users;
  }

  /**
   * 사용자 이름(username)을 기반으로 사용자를 조회합니다.
   * <p>
   * 이 메서드는 지정된 사용자 이름(username)에 해당하는 사용자 데이터를 검색하며, 사용자가 존재하지 않을 경우
   * {@link UsernameNotFoundException}을 던집니다.
   *
   * @param username 조회할 사용자 이름(고유 ID)
   * @return 사용자 이름에 해당하는 {@link UserDetails} 객체
   * @throws UsernameNotFoundException 사용자가 존재하지 않을 경우 예외 발생
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return users.stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
