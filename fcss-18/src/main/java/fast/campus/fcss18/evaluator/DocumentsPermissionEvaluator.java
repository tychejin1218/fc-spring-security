package fast.campus.fcss18.evaluator;

import fast.campus.fcss18.domain.Document;
import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 문서(Document)에 대한 사용자 권한을 평가하는 클래스입니다.
 *
 * <p>Spring Security의 {@link PermissionEvaluator} 인터페이스를 구현하여,
 * 특정 사용자(Authentication)가 대상 객체(Document)에 대해 권한(permission)을 가지고 있는지 여부를 판단합니다.</p>
 *
 * <p>권한 평가 로직:</p>
 * <ul>
 *   <li>사용자가 "admin" 권한을 가지는 경우 접근 허용</li>
 *   <li>또는, 문서의 담당자(owner)가 현재 인증된 사용자의 이름과 동일한 경우 접근 허용</li>
 *   <li>위 조건을 만족하지 않으면 접근 거부</li>
 * </ul>
 */
@Component
public class DocumentsPermissionEvaluator implements PermissionEvaluator {

  /**
   * 사용자가 특정 {@link Document}에 대해 주어진 권한을 가지고 있는지 평가합니다.
   *
   * @param authentication     현재 인증된 사용자의 정보를 포함하는 {@link Authentication} 객체
   * @param targetDomainObject 평가 대상 객체 (문서 객체)
   * @param permission         요청된 권한 (예: "ROLE_admin")
   * @return 사용자가 권한을 가지고 있으면 {@code true}, 그렇지 않으면 {@code false}
   */
  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    Document document = (Document) targetDomainObject; // 평가 대상 문서 객체
    String p = (String) permission; // 요청된 권한 문자열 (예: "ROLE_admin")

    // 사용자에게 요청된 권한이 있는지 확인 (admin 권한 확인)
    boolean isAdmin = authentication.getAuthorities()
        .stream()
        .anyMatch(each -> each.getAuthority().equals(p));

    // admin 권한이 있거나, 문서의 담당자(owner)가 사용자 이름과 같으면 권한 허용
    return isAdmin || document.getOwner().equals(authentication.getName());
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    return false;
  }
}
