package fast.campus.fcss18.domain;

import lombok.Getter;

/**
 * 문서(Document)를 나타내는 클래스입니다.
 *
 * <p>
 * 각 문서는 담당자(owner) 정보를 유지하며, 이 정보를 통해 문서 접근 권한을 설정하고 관리할 수 있습니다.
 * </p>
 */
@Getter
public class Document {

  private final String owner;

  public Document(String owner) {
    this.owner = owner;
  }
}
