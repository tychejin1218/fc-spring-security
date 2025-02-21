package fast.campus.authservice.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class OtpCodeUtil {

  /**
   * 6자리의 OTP를 생성하는 메서드.
   *
   * <p>OTP는 100000에서 999999 사이의 정수로 구성되며, 보안성을 위해 {@link SecureRandom#getInstanceStrong()} 메서드를
   * 사용합니다.</p>
   *
   * @return 생성된 6자리 OTP 코드
   * @throws RuntimeException {@link NoSuchAlgorithmException} 발생 시 실행 시점 예외로 래핑하여 던집니다.
   */
  public static String generateOtpCode() {
    try {
      SecureRandom secureRandom = SecureRandom.getInstanceStrong();
      int value = secureRandom.nextInt(900000) + 100000;
      return String.valueOf(value);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
