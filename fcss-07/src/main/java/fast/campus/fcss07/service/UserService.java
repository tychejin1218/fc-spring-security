package fast.campus.fcss07.service;

import fast.campus.fcss07.domain.user.CreateUser;
import fast.campus.fcss07.domain.user.User;
import fast.campus.fcss07.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  /**
   * 새로운 사용자를 등록
   *
   * @param create 등록할 사용자 정보를 담은 {@link CreateUser} 객체
   * @return 등록된 사용자 username
   * @throws RuntimeException 해당 username이 이미 존재하는 경우
   */
  public String register(CreateUser create) {
    // username 중복 여부를 확인하고, 중복된 경우에는 예외를 던짐
    if (userRepository.userExists(create.getUsername())) {
      throw new RuntimeException(String.format("User [%s] already exists", create.getUsername()));
    }
    // 정상적으로 사용자 데이터를 저장한 후, 등록된 사용자의 username을 반환
    return userRepository.create(create).getUsername();
  }

  /**
   * username으로 사용자 정보를 조회
   *
   * @param username 조회할 사용자의 username
   * @return 찾은 사용자 정보를 담은 {@link User} 객체
   */
  public User getUserByUsername(String username) {
    return userRepository.getUserByUsername(username);
  }
}
