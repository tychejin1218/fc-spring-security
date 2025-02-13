package fast.campus.fcss20.repository;

import fast.campus.fcss20.domain.Content;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContentV2Repository extends JpaRepository<Content, Integer> {

  /**
   * <p>Query 동작 설명:</p>
   * <ul>
   *   <li>JPQL 쿼리를 작성하여 데이터베이스에서 필요한 데이터만 조회</li>
   *   <li>?#{authentication.name}: 현재 인증된 사용자의 username을 SpEL로 쿼리에 삽입</li>
   *   <li>WHERE 조건: c.owner가 인증된 사용자의 username과 일치하는 경우만 반환</li>
   * </ul>
   *
   * <p>주요 특징:</p>
   * <ul>
   *   <li>Spring Security Data 의존성이 필요하며, JPQL로 쿼리를 수동 작성해야 합니다.</li>
   *   <li>필터링이 데이터베이스 레벨에서 수행되므로 성능이 우수합니다.</li>
   *   <li>대규모 데이터 또는 성능 최적화가 중요한 경우 적합합니다.</li>
   * </ul>
   */
  @Query("SELECT c FROM Content c WHERE c.name LIKE %:name% AND c.owner=?#{authentication.name}")
  List<Content> findContentsByNameContaining(String name);
}
