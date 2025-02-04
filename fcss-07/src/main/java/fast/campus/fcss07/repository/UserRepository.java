package fast.campus.fcss07.repository;

import fast.campus.fcss07.domain.user.CreateUser;
import fast.campus.fcss07.domain.user.User;
import fast.campus.fcss07.exception.UserNotFoundException;
import fast.campus.fcss07.repository.entity.AuthorityEntity;
import fast.campus.fcss07.repository.entity.UserEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 데이터 액세스를 처리하는 Repository 클래스
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final AuthorityJpaRepository authorityJpaRepository;

  /**
   * username을 기준으로 사용자 정보가 존재하는지 확인
   *
   * @param username 존재 여부를 확인할 사용자의 username
   * @return 사용자 정보가 존재하면 true, 존재하지 않으면 false
   */
  @Transactional(readOnly = true)
  public Boolean userExists(String username) {
    // username을 기준으로 사용자의 존재 여부를 체크
    return userJpaRepository.findUserByUsername(username).isPresent();
  }

  /**
   * username을 기준으로 사용자 정보를 조회
   *
   * @param username 조회할 사용자의 username
   * @return 조회된 사용자 정보를 담은 {@link User} 객체
   * @throws UserNotFoundException 사용자가 존재하지 않을 경우 발생
   */
  @Transactional(readOnly = true)
  public User getUserByUsername(String username) {
    // username으로 DB에서 사용자 데이터를 검색하고, 존재하지 않을 경우 예외를 던짐
    return userJpaRepository.findUserByUsername(username)
        .orElseThrow(UserNotFoundException::new)
        .toUser();
  }

  /**
   * 새로운 사용자 등록
   *
   * @param create 사용자 생성 정보를 담은 {@link CreateUser} 객체
   * @return 등록된 사용자 정보를 담은 {@link User} 객체
   */
  @Transactional
  public User create(CreateUser create) {
    // 사용자 정보를 받아 DB에 저장
    UserEntity user = userJpaRepository.save(UserEntity.newUser(create));
    // 저장된 사용자 데이터와 기본 권한 정보를 함께 처리
    AuthorityEntity authority = authorityJpaRepository.save(new AuthorityEntity("READ", user));
    user.replaceAuthority(List.of(authority));
    return user.toUser();
  }
}
