package fast.campus.fcss18.controller;

import fast.campus.fcss18.domain.Document;
import fast.campus.fcss18.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 문서(Document) 관련 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>이 컨트롤러는 클라이언트로부터 문서 이름을 입력받아 해당 문서를 반환합니다. 요청을 처리할 때,
 * 인증 정보를 기반으로 문서 접근 권한을 확인합니다.</p>
 */
@RestController
@RequiredArgsConstructor
public class DocumentController {

  private final DocumentService documentService;

  /**
   * 주어진 이름에 해당하는 문서를 반환합니다.
   *
   * <p>
   * 이 엔드포인트는 인증된 사용자의 요청에 따라 동작하며, 요청에서 문서 이름을 전달받아 문서를 조회합니다.
   * </p>
   *
   * @param name 클라이언트가 요청한 문서의 이름
   * @return 요청된 이름에 해당하는 {@link Document} 객체
   */
  @GetMapping("/api/v1/document/{name}")
  public Document getDocument(@PathVariable String name) {
    return documentService.getDocument(name);
  }

  // 테스트
  //테스트를 해봐야 할 것
  //1) danny 는 모든 문서에 접근이 가능한가?
  //2) steve 는 본인 문서만 접근이 가능한가?

  // 테스트 1. danny 는 모든 문서에 접근이 가능한가?
  //성공!

  // 테스트 2. steve 는 본인 문서에만 접근이 가능한가?
  //성공!
  //- 나머지 문서에 대해서는 403 FORBIDDEN 발생
}
