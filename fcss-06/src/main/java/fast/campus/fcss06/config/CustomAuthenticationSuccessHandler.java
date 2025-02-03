package fast.campus.fcss06.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * 로그인 성공 후 기본 url 설정
 *
 * <p>
 * 기본 설정:
 * </p>
 * <ul>
 *   <li>defaultSuccessUrl()을 사용해 기본 리다이렉트 경로를 `/home`으로 설정.</li>
 *   <li>로그인 성공 시, 자동으로 `/home`으로 리다이렉트.</li>
 * </ul>
 *
 * <p>
 * 추가 설정:
 * </p>
 * <ul>
 *   <li>
 *     AuthenticationSuccessHandler 인터페이스를 활용하여 요청자의 권한에 따라 리다이렉트 경로를 동적으로 설정 가능:
 *     <ul>
 *       <li>요청자에게 `READ` 권한이 있으면 `/home`으로 리다이렉트.</li>
 *       <li>그 외의 경우 `/error`로 리다이렉트.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // 요청자의 권한 정보 가져오기
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

    // 권한 중 "READ" 권한 여부 확인
    Optional<? extends GrantedAuthority> read = authorities.stream()
        .filter(a -> a.getAuthority().equals("READ"))
        .findFirst();

    // READ 권한이 있으면 "/home"으로 리다이렉트, 없으면 "/error"로 리다이렉트
    if (read.isPresent()) {
      response.sendRedirect("/home");
    } else {
      response.sendRedirect("/error");
    }
  }
}
