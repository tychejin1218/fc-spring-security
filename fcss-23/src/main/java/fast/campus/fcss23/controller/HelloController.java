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
        return auth.map(a -> "Hello, " + a.getName());
    }

    @GetMapping("/hello/3")
    public Mono<String> hello3() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> "Hello, " + auth.getName());
    }

    @GetMapping("/hello/4")
    public Mono<String> hello4() {
        return Mono.just("Hello World! 444");
    }
}
