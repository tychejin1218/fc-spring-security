package fast.campus.fcss16.controller;

import fast.campus.fcss16.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private final HelloService helloService;

    @GetMapping("/api/v1/hello")
    public String hello() {
        try {
            return "Hello, " + helloService.getName();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
