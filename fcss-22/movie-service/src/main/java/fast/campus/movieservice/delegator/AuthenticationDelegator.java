package fast.campus.movieservice.delegator;

import fast.campus.movieservice.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 인증 관련 HTTP 요청을 외부 인증 서비스에 위임하는 클래스.
 *
 * <p>주로 사용자 인증(아이디/비밀번호)과 OTP(일회용 비밀번호) 검증 작업을 담당하며,
 * RestTemplate을 활용해 외부 API와 통신합니다.</p>
 */
@Component
@RequiredArgsConstructor
public class AuthenticationDelegator {

  private final RestTemplate restTemplate;

  @Value("${base-url.auth-service}")
  private String authServiceBaseUrl;

  /**
   * 사용자 ID와 비밀번호를 사용하여 외부 인증 서비스에 인증 요청을 보냄
   *
   * @param userId   사용자 ID
   * @param password 사용자 비밀번호
   */
  public void restAuth(String userId, String password) {
    String url = authServiceBaseUrl + "/users/auth";

    User user = User.builder()
        .userId(userId)
        .password(password)
        .build();

    restTemplate.postForEntity(url, new HttpEntity<>(user), Void.class);
  }

  /**
   * 사용자 ID와 OTP(일회용 비밀번호)를 사용하여 외부 인증 서비스에 OTP 검증 요청을 보냄
   * <p>응답으로 boolean 값을 반환받으며 OTP 검증 결과를 나타냅니다.</p>
   *
   * @param userId 사용자 ID
   * @param otp    사용자 OTP
   * @return OTP 검증 결과가 성공(true)이면 <code>true</code>, 실패(false)이면 <code>false</code>
   */
  public boolean restOtp(String userId, String otp) {
    String url = authServiceBaseUrl + "/otp/check";

    User user = User.builder()
        .userId(userId)
        .otp(otp)
        .build();

    ResponseEntity<Boolean> response = restTemplate.postForEntity(url, new HttpEntity<>(user),
        Boolean.class);
    return Boolean.TRUE.equals(response.getBody());
  }
}
