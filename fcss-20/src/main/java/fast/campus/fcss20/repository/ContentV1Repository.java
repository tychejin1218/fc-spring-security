package fast.campus.fcss20.repository;

import fast.campus.fcss20.domain.Content;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostFilter;

public interface ContentV1Repository extends JpaRepository<Content, Integer> {

  /**
   * <p>PostFilter 동작 설명:</p>
   * <ul>
   *   <li>filterObject: 반환된 리스트의 각 객체(Content)를 나타냅니다.</li>
   *   <li>authentication.name: 현재 인증된 사용자의 username 값을 참조.</li>
   *   <li>조건: 반환된 Content 데이터 중 owner 필드가 username과 같은 객체만 남깁니다.</li>
   * </ul>
   *
   * <p>주요 특징:</p>
   * <ul>
   *   <li>간단히 적용 가능하지만, 필터링이 애플리케이션 레벨에서 수행되므로 성능 저하가 발생할 수 있습니다.</li>
   *   <li>데이터 규모가 작거나 즉시 필터링이 필요한 경우 적합합니다.</li>
   * </ul>
   */
  @PostFilter("filterObject.owner == authentication.name")
  List<Content> findContentsByNameContaining(String name);
}
