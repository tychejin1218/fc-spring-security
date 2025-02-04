package fast.campus.fcss11.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  /**
   * <p>
   *   <ol>
   *     <li>헤더가 없이 요청을 하게 되면 400 Bad Request 가 발생함</li>
   *     <li>Request-Id 헤더를 추가하면 200 OK 응답이 반환되며 “Hello, Spring Security!” 가 반환됨</li>
   *   </ol>
   * </p>
   *
   * @return 설명
   */
  @GetMapping("/api/v1/hello")
  public String hello() {
    return "Hello, Spring Security!";
  }
}
