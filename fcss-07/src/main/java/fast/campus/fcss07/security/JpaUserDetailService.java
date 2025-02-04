package fast.campus.fcss07.security;

import fast.campus.fcss07.domain.user.User;
import fast.campus.fcss07.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security에서 제공하는 사용자 인증용 UserDetailsService 구현 클래스
 * <p>
 * 주어진 username을 기준으로 사용자 정보를 로드하며, 사용자 데이터를 기반으로 {@link CustomUserDetails}를 생성
 * </p>
 */
@Service
@RequiredArgsConstructor
public class JpaUserDetailService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * username으로 사용자 데이터를 로드
   *
   * @param username 인증 처리 시 사용할 username
   * @return 해당 username과 연결된 사용자 정보를 담은 {@link UserDetails}
   * @throws UsernameNotFoundException 사용자가 존재하지 않을 때 발생
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User userByUsername = userRepository.getUserByUsername(username);
    return new CustomUserDetails(userByUsername);
  }
}
