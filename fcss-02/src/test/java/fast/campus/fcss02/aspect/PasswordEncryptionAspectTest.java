package fast.campus.fcss02.aspect;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import fast.campus.fcss02.controller.request.HelloRequestBody;
import fast.campus.fcss02.service.EncryptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * <p>AOP 기반 비밀번호 암호화 테스트</p>
 *
 * <ul>
 *   <li>MockitoExtension을 활용하여 @Mock 객체 사용</li>
 *   <li>HelloRequestBody의 "password" 필드 초기값 설정</li>
 *   <li>EncryptService의 encrypt 메서드가 호출되면 "encrypted" 값을 반환하도록 설정</li>
 *   <li>암호화 결과로 password 필드의 값이 "encrypted"로 변경되는지 검증</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class PasswordEncryptionAspectTest {

  PasswordEncryptionAspect aspect;

  @Mock
  EncryptService encryptService;

  @BeforeEach
  void setup() {
    aspect = new PasswordEncryptionAspect(encryptService);
  }

  @Test
  void test() {

    // Given
    HelloRequestBody requestBody = new HelloRequestBody("id", "password");
    when(encryptService.encrypt(any())).thenReturn("encrypted");

    // When
    aspect.fieldEncryption(requestBody);

    // Then
    assertThat(requestBody.getPassword()).isEqualTo("encrypted");
  }
}
