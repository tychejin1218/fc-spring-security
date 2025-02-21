package fast.campus.authservice.service.user;

import fast.campus.authservice.domain.user.User;
import fast.campus.authservice.exception.InvalidAuthException;
import fast.campus.authservice.repository.auth.AuthRepository;
import fast.campus.authservice.service.encryption.EncryptService;
import fast.campus.authservice.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final EncryptService encryptService;
  private final OtpService otpService;
  private final AuthRepository authRepository;

  /**
   * 새로운 사용자를 생성
   *
   * @param userId   생성하려는 사용자의 ID
   * @param password 생성하려는 사용자의 비밀번호
   * @return 생성된 사용자 정보
   */
  public User createNewUser(String userId, String password) {
    return authRepository.createNewUser(new User(userId, password));
  }

  /**
   * 사용자를 인증하고, 인증 성공 시 새로운 OTP를 생성하여 반환
   *
   * @param userId   인증하려는 사용자의 ID
   * @param password 인증하려는 사용자의 비밀번호
   * @return 새로운 OTP 코드
   * @throws InvalidAuthException 비밀번호가 일치하지 않을 경우 예외 발생
   */
  public String auth(String userId, String password) {
    User user = authRepository.getUserByUserId(userId);
    if (encryptService.matches(password, user.getPassword())) {
      // 비밀번호가 일치하면 OTP를 갱신하고 반환
      return otpService.renewOtp(userId);
    }

    // 비밀번호가 일치하지 않으면 예외 발생
    throw new InvalidAuthException();
  }
}
