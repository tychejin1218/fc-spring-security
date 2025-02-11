package fast.campus.fcss18.repository;

import fast.campus.fcss18.domain.Document;
import java.util.Map;
import org.springframework.stereotype.Repository;

/**
 * {@link Document} 엔티티를 관리하고 조회하는 저장소 클래스입니다.
 *
 * <p>
 * 문서와 담당자를 미리 정의한 간단한 인메모리 데이터베이스 역할을 합니다. 3개의 문서가 존재하며, 각각의 문서에는 담당자가 지정되어 있습니다.
 * </p>
 */
@Repository
public class DocumentRepository {

  // 문서와 각 문서의 담당자를 저장하는 인메모리 데이터
  // - Document A: danny.kim 담당
  // - Document B: steve.kim 담당
  // - Document C: harris.kim 담당
  private Map<String, Document> documents = Map.of(
      "Document A", new Document("danny.kim"),
      "Document B", new Document("steve.kim"),
      "Document C", new Document("harris.kim")
  );

  /**
   * 문서 이름으로 문서를 조회합니다.
   *
   * @param name 조회하려는 문서의 이름
   * @return 주어진 이름에 해당하는 {@link Document} 객체를 반환하며, 해당 이름의 문서가 없는 경우 {@code null}을 반환합니다.
   */
  public Document findDocumentByName(String name) {
    return documents.get(name);
  }
}
