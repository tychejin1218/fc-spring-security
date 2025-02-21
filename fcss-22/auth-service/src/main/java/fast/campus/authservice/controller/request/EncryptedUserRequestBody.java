package fast.campus.authservice.controller.request;

import fast.campus.authservice.annotation.PasswordEncryption;
import java.beans.ConstructorProperties;
import lombok.Getter;

@Getter
public class EncryptedUserRequestBody {

  private final String userId;

  /**
   * 클라이언트에서 전송된 비밀번호 (비밀번호 암호화를 위해 @PasswordEncryption 어노테이션 사용)
   */
  @PasswordEncryption
  private String password;

  @ConstructorProperties({"userId", "password"})
  public EncryptedUserRequestBody(String userId, String password) {
    this.userId = userId;
    this.password = password;
  }
}
