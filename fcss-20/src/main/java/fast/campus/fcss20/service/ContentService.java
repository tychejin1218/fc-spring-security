package fast.campus.fcss20.service;

import fast.campus.fcss20.domain.Content;
import fast.campus.fcss20.repository.ContentV1Repository;
import fast.campus.fcss20.repository.ContentV2Repository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {

  private final ContentV1Repository contentV1Repository;
  private final ContentV2Repository contentV2Repository;

  public List<Content> findAllByName(String name) {
    return contentV1Repository.findContentsByNameContaining(name);
  }

  public List<Content> findAllByNameWithAuthentication(String name) {
    return contentV2Repository.findContentsByNameContaining(name);
  }
}
