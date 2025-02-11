package fast.campus.fcss18.service;

import fast.campus.fcss18.domain.Document;
import fast.campus.fcss18.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

/**
 * 문서(Document)에 대한 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * <p>이 클래스는 문서를 검색하고, 특정 사용자 권한에 따라 문서 접근을 제어하는 기능을 제공합니다.</p>
 *
 * <p>Spring Security의 {@link PostAuthorize} 애노테이션을 활용하여, 메서드 실행 후 반환되는 객체에 대한 접근 권한을 확인합니다.</p>
 *
 * @see DocumentRepository
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

  private final DocumentRepository documentRepository;

  /**
   * 주어진 이름에 해당하는 문서를 반환합니다.
   *
   * <p>이 메서드는 {@link PostAuthorize}를 통해 반환된 문서에 대한 접근 권한을 확인합니다.
   *
   * <br>권한 규칙: `hasPermission(returnObject, 'ROLE_admin')`
   * <ul>
   *   <li><strong>첫 번째 파라미터:</strong> Spring Security의 {@code Authentication} 객체 (자동 주입)</li>
   *   <li><strong>두 번째 파라미터:</strong> 반환된 객체 ({@code returnObject}, 즉 {@link Document})</li>
   *   <li><strong>세 번째 파라미터:</strong> 권한 문자열 ('ROLE_admin')</li>
   * </ul>
   * </p>
   *
   * <p>이 규칙에 따라, {@code ROLE_admin} 권한을 보유한 사용자만 반환된 문서에 접근할 수 있습니다.</p>
   *
   * @param name 찾고자 하는 문서의 이름
   * @return 검색된 {@link Document} 객체를 반환합니다. 없을 경우 {@code null}을 반환할 수 있습니다.
   */
  @PostAuthorize("hasPermission(returnObject, 'ROLE_admin')")
  public Document getDocument(String name) {
    return documentRepository.findDocumentByName(name);
  }
}
