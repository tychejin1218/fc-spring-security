package fast.campus.fcss15.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

//  @CrossOrigin(origins = "http://localhost:8080")
  @PostMapping("/test")
  public String test() {
    log.info("test method called");
    return "hello";
  }
}
