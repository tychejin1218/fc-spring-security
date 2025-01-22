package fast.campus.fcss09.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/v1/hello")
    public String hello(Authentication authentication) {
        return "hello, " + authentication.getName();
    }

    @GetMapping("/api/v1/hi")
    public String hi(Authentication authentication) {
        return "hi, " + authentication.getName();
    }

    @GetMapping("/api/v1/hola")
    public String hola(Authentication authentication) {
        return "hola, " + authentication.getName();
    }
}
