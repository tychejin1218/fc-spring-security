package fast.campus.fcss02.controller.request;

import fast.campus.fcss02.annotation.CustomEncryption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HelloRequestBody {

  private String id;

  // password 필드에 생성한 CustomEncryption 어노테이션 부여
  @CustomEncryption
  private String password;
}
