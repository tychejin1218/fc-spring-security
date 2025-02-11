package fast.campus.fcss16.service;

import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

  // @PreAuthorize 어노테이션을 사용하여 접근 권한을 제어
  // - hasAuthority("WRITE")를 활용하여 사용자에게 WRITE 권한이 있는 경우에만 getName() 메서드가 실행됨

  // 동작 방식:
  // 1. WRITE 권한이 있으면:
  //    -> "danny.kim"을 반환
  // 2. WRITE 권한이 없으면:
  //    -> "danny.kim"을 반환하지 않고 예외를 던짐 (Access Denied)

  /*@PreAuthorize("hasAuthority('WRITE')")
  public String getName() {
    return "danny.kim";
  }*/

  /**
   * 현재 로그인된 사용자 이름과 권한 반환.
   * <p>
   * "WRITE" 권한이 필요합니다.
   *
   * @return 사용자 이름과 권한 목록
   */
  @PreAuthorize("hasAuthority('WRITE')")
  public String getName() {

    // Authentication 객체에서 인증 정보 가져오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 사용자 이름과 권한 목록을 문자열로 변환
    return authentication.getName()
        + " has ["
        + authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","))
        + "] authorities";
  }
}
