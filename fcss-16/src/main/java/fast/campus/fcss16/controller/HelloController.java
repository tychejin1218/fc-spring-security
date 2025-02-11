package fast.campus.fcss16.controller;

import fast.campus.fcss16.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

  private final HelloService helloService;

  /**
   * /api/v1/hello 요청을 처리하는 엔드포인트입니다.
   * <p>
   * 반환값:
   * <ul>
   *   <li>정상 작동 시: "Hello, {사용자 이름}"</li>
   *   <li>Access Denied 등 보안 예외 발생 시: 예외 메시지 반환</li>
   * </ul>
   * <p>
   * 주의 사항:
   * <ul>
   *   <li>보안 관련 문제가 발생할 경우, HTTP 응답 상태는 200 OK로 나타납니다.</li>
   *   <li>이유는 try-catch 블럭 내에서 예외를 처리하고 메시지를 반환하기 때문입니다.</li>
   * </ul>
   *
   * @return String 결과 메시지
   */
  @GetMapping("/api/v1/hello")
  public String hello() {
    try {
      return "Hello, " + helloService.getName();
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
