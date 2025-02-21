package fast.campus.authservice.repository.otp;

import fast.campus.authservice.entity.otp.OtpEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpJpaRepository extends JpaRepository<OtpEntity, Integer> {

  /**
   * userId로 OtpEntity를 조회하는 메서드
   *
   * @param userId OTP를 조회하려는 사용자의 ID
   * @return 조회된 OtpEntity를 Optional로 래핑하여 반환
   */
  Optional<OtpEntity> findOtpEntityByUserId(String userId);
}
