package fast.campus.fcss06.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 인증 실패 시 응답을 설정하는 CustomEntryPoint 클래스
 *
 * <p>
 * AuthenticationEntryPoint 인터페이스를 구현하여 인증 실패 처리 로직을 정의.
 * </p>
 *
 * <ul>
 *   <li>commence() 메소드는 인증 실패 시 호출되며, 다음 파라미터를 제공받음:
 *     <ul>
 *       <li>HttpServletRequest: 요청 정보</li>
 *       <li>HttpServletResponse: 응답 정보</li>
 *       <li>AuthenticationException: 발생한 예외 정보</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>
 * 응답 설정:
 * </p>
 * <ul>
 *   <li>응답 헤더에 "message" 추가 (`addHeader` 메서드 사용).</li>
 *   <li>HTTP 상태 코드를 `401 Unauthorized`로 설정 (`sendError` 메서드 사용).</li>
 * </ul>
 *
 * <p>
 * 사용 방법:
 * </p>
 * <ul>
 *   <li>CustomEntryPoint 클래스를 생성하고 AuthenticationEntryPoint 인터페이스를 상속.</li>
 *   <li>commence() 메소드를 재정의하여 응답 설정.</li>
 * </ul>
 */
public class CustomEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    // 인증 실패 시 응답 헤더 설정
    response.addHeader("message", "Hello World");

    // 401 Unauthorized 상태 코드 설정
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }
}
