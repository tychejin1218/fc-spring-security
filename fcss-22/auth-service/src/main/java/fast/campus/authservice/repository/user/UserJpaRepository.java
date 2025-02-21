package fast.campus.authservice.repository.user;

import fast.campus.authservice.entity.user.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {

  /**
   * userId로 UserEntity를 조회하는 메서드
   *
   * @param userId 조회하려는 사용자의 ID
   * @return 조회된 UserEntity를 Optional로 래핑하여 반환
   */
  Optional<UserEntity> findUserEntityByUserId(String userId);
}
