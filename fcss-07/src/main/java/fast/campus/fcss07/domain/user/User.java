package fast.campus.fcss07.domain.user;

import fast.campus.fcss07.domain.Authority;
import fast.campus.fcss07.domain.EncryptionAlgorithm;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

  private String username;
  private String password;
  private EncryptionAlgorithm algorithm;
  private List<Authority> authorities;
}
