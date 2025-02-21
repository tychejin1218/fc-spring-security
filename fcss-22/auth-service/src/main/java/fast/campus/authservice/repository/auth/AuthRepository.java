package fast.campus.authservice.repository.auth;

import fast.campus.authservice.domain.user.User;
import fast.campus.authservice.entity.otp.OtpEntity;
import fast.campus.authservice.entity.user.UserEntity;
import fast.campus.authservice.exception.InvalidAuthException;
import fast.campus.authservice.repository.otp.OtpJpaRepository;
import fast.campus.authservice.repository.user.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionOperations;

@Repository
@RequiredArgsConstructor
public class AuthRepository {

  private final OtpJpaRepository otpJpaRepository;
  private final UserJpaRepository userJpaRepository;

  private final TransactionOperations readTransactionOperations;
  private final TransactionOperations writeTransactionOperations;

  /**
   * 새 사용자 생성 메서드
   *
   * @param user 생성하려는 사용자 정보 (도메인 객체)
   * @return 생성된 사용자 정보 (도메인 객체)
   * @throws RuntimeException 사용자가 이미 존재하는 경우 예외를 발생
   */
  public User createNewUser(User user) {
    return writeTransactionOperations.execute(status -> {
      Optional<UserEntity> userOptional = userJpaRepository.findUserEntityByUserId(
          user.getUserId());
      if (userOptional.isPresent()) {
        throw new RuntimeException(String.format("User [%s] already exists", user.getUserId()));
      }

      UserEntity saved = userJpaRepository.save(user.toEntity());
      return saved.toDomain();
    });
  }

  /**
   * userId로 사용자 정보를 가져오는 메서드
   *
   * @param userId 조회하려는 사용자의 ID
   * @return 조회된 사용자 정보 (도메인 객체)
   * @throws InvalidAuthException 사용자를 찾을 수 없는 경우 예외를 발생
   */
  public User getUserByUserId(String userId) {
    return readTransactionOperations.execute(status ->
        userJpaRepository.findUserEntityByUserId(userId)
            .orElseThrow(InvalidAuthException::new)
            .toDomain());
  }

  /**
   * 사용자 ID를 통해 OTP를 가져오는 메서드
   *
   * @param userId OTP를 조회하려는 사용자 ID
   * @return 조회된 OTP 코드
   * @throws RuntimeException 사용자를 찾을 수 없는 경우 예외를 발생
   */
  public String getOtp(String userId) {
    return readTransactionOperations.execute(
        status -> otpJpaRepository.findOtpEntityByUserId(userId)
            .orElseThrow(
                () -> new RuntimeException(String.format("User [%s] does not exist", userId)))
            .getOtpCode());
  }

  /**
   * 사용자 ID를 기준으로 OTP를 삽입하거나 기존 데이터를 갱신하는 메서드
   *
   * @param userId OTP를 삽입/갱신하려는 사용자 ID
   * @param newOtp 새로 설정하려는 OTP 코드
   */
  public void upsertOtp(String userId, String newOtp) {
    writeTransactionOperations.executeWithoutResult(status -> {
      Optional<OtpEntity> optOptional = otpJpaRepository.findOtpEntityByUserId(userId);

      if (optOptional.isPresent()) {
        optOptional.get().renewOtp(newOtp);
      } else {
        otpJpaRepository.save(new OtpEntity(userId, newOtp));
      }
    });
  }
}
