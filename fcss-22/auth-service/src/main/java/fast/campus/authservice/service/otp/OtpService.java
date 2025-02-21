package fast.campus.authservice.service.otp;

import fast.campus.authservice.repository.auth.AuthRepository;
import fast.campus.authservice.util.OtpCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {

  private final AuthRepository authRepository;

  /**
   * 사용자가 입력한 OTP와 저장된 OTP를 비교하여 인증
   *
   * @param userId OTP를 확인하려는 사용자의 ID
   * @param sourceOtp 사용자가 입력한 OTP 코드
   * @return OTP가 일치하면 true, 일치하지 않으면 false
   */
  public boolean checkOtp(String userId, String sourceOtp) {
    String targetOtp = authRepository.getOtp(userId);
    return targetOtp.equals(sourceOtp);
  }

  /**
   * 새로운 OTP를 생성하고 데이터베이스에 갱신
   *
   * @param userId OTP를 새로 갱신하려는 사용자의 ID
   * @return 새로 생성된 OTP 코드
   */
  public String renewOtp(String userId) {
    String newOtp = OtpCodeUtil.generateOtpCode(); // OTP 코드 생성
    authRepository.upsertOtp(userId, newOtp); // OTP 저장 또는 업데이트
    return newOtp;
  }
}
