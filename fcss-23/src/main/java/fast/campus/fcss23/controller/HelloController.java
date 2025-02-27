package fast.campus.fcss23.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

  @GetMapping("/hello")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<String> hello() {
    return Mono.just("Hello World! 111");
  }

  @GetMapping("/hello/2")
  public Mono<String> hello2(Mono<Authentication> auth) {
// Mono<Authentication> 객체 주입 스프링 프레임워크가 Mono<Authentication> 을 주입할 수 있음
    return auth.map(a -> "Hello, " + a.getName());
  }

  @GetMapping("/hello/3")
  public Mono<String> hello3() {
// Authentication 객체의 출처
// 리액티브 앱이기 때문에 ThreadLocal 에 의존한 SecurityContext 를 사용할 수 없음
//  - 리액티브 앱을 위한 다른 컨텍스트 홀더 구현체인 ReactiveSecurityContextHolder 를 사용할 수 있음
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(auth -> "Hello, " + auth.getName());
  }

  @GetMapping("/hello/4")
  public Mono<String> hello4() {
    return Mono.just("Hello World! 444");
  }
}
