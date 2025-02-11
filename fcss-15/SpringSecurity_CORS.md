# CORS (Cross-Origin Resource Sharing) 정리

## 1. CORS의 필요성
- 브라우저는 기본적으로 동일 출처(Same-Origin)에서만 요청을 허용.
  - 예: `www.example.com` → `api.example.com` 요청 시 **교차 도메인 요청**으로 간주하여 기본적으로 차단.
  - 교차 도메인 요청을 허용하려면 **CORS** 정책을 설정해야 함.

- **CORS가 필요한 경우**
  - 백엔드(Spring Boot)와 프론트엔드(React, Vue)가 별도의 애플리케이션으로 개발된 경우.
  - 프론트엔드(예: `example.com`)에서 백엔드(예: `api.example.com`) 호출 시 거절될 수 있음.

---

## 2. CORS 작동 방식
- 브라우저는 교차 도메인 요청이 발생하면 **HTTP OPTIONS**를 통해 사전 요청(Preflight)을 보내 서버의 허용 여부를 확인.
- 서버가 응답에 특정 헤더를 포함해야 요청이 허용됨:
  - `Access-Control-Allow-Origin`: 접근 가능한 출처 지정.
  - `Access-Control-Allow-Methods`: 허용되는 HTTP 메서드 지정 (예: GET, POST).
  - `Access-Control-Allow-Headers`: 허용되는 HTTP 헤더 제한.

---

## 3. CORS 문제 발생 예시
- **상황**
  - 프론트엔드에서 `localhost:8080` 실행 후, `127.0.0.1:8080`에 요청.
  - 브라우저는 `localhost`와 `127.0.0.1`을 다른 출처로 간주.
- **결과**
  - 요청이 서버에 도달하더라도, 브라우저는 응답을 수락하지 않음.
  - 에러: `Access-Control-Allow-Origin` 헤더가 응답에 없어 브라우저에서 차단.

---

## 4. CORS 해결 방법

### (1) **`@CrossOrigin` 어노테이션 활용**
- **특정 엔드포인트**에 CORS 정책을 직접 추가:
  ```java
  @CrossOrigin(origins = "http://localhost:8080")
  @PostMapping("/api/test")
  public String testEndpoint() {
      return "hello";
  }
  ```
- **장점**: 간단히 특정 엔드포인트에만 적용 가능.
- **단점**: 반복적인 코드 작성이 필요하며, 모든 엔드포인트에 적용하기 어려움.

---

### (2) **`CorsConfigurer` 설정 활용**
- **전역 설정**으로 모든 컨트롤러 및 요청에 대해 CORS 허용 가능:
  ```java
  @Configuration
  public class SecurityConfig {
      @Bean
      public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
          httpSecurity.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
              .authorizeHttpRequests(c -> c.anyRequest().permitAll()) // 인증 없이 허용
              .cors(c -> { 
                  CorsConfigurationSource source = request -> {
                      CorsConfiguration config = new CorsConfiguration();
                      config.setAllowedOrigins(List.of("*")); // 모든 출처 허용
                      config.setAllowedMethods(List.of("*")); // 모든 HTTP 메서드 허용
                      return config;
                  };
                  c.configurationSource(source); 
              });
          return httpSecurity.build();
      }
  }
  ```
- **장점**: 관리가 용이하며, 반복되는 코드를 줄일 수 있음.

---

## 5. 예제 코드

### **`SecurityConfig`**
```java
package fast.campus.fcss15.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable); // CSRF 비활성화
        httpSecurity.authorizeHttpRequests(c -> c.anyRequest().permitAll()); // 모든 요청 인증 허용

        // CORS 설정
        httpSecurity.cors(c -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("*")); // 모든 출처 허용 (*)
                config.setAllowedMethods(List.of("*")); // 모든 HTTP 메서드 허용
                return config;
            };
            c.configurationSource(source);
        });

        return httpSecurity.build();
    }
}
```

---

### **`MainController` (MVC 컨트롤러)**
```java
package fast.campus.fcss15.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String main() {
        return "main.html"; // main.html 반환
    }
}
```

---

### **`TestController` (`@RestController`)**
```java
package fast.campus.fcss15.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {
    @PostMapping("/test")
    public String test() {
        log.info("test method called");
        return "hello"; // "hello" 반환
    }
}
```

---

### **main.html**
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <script>
        const http = new XMLHttpRequest();
        const url = 'http://127.0.0.1:8080/test'; // 교차 출처 호출
        http.open("POST", url);
        http.send();

        http.onreadystatechange = (e) => {
            document.getElementById("output")
                .innerHTML = http.responseText; // 응답 결과 출력
        }
    </script>
</head>
<body>
    <div id="output">
        <!-- 응답 결과가 여기에 표시됨 -->
    </div>
</body>
</html>
```

---

## 6. 실행 방법
1. 위의 모든 파일을 프로젝트에 작성합니다.
  - Java 파일(`SecurityConfig`, `MainController`, `TestController`)은 패키지에 넣어 컴파일.
  - HTML 파일(`main.html`)은 `resources/templates` 디렉토리에 저장.
2. 로컬 환경에서 애플리케이션을 실행 후, **http://localhost:8080** 접속.
3. 브라우저 화면에서 자바스크립트를 통해 API 요청이 실행되고, 결과 "hello"가 표시됨.

---

## 7. 요약
- **CORS란?**
  - 다른 출처에서 서버 자원을 요청할 수 있도록 제한을 완화하는 정책.
- **Spring에서 CORS 허용하는 방법**
  1. 엔드포인트에 `@CrossOrigin` 사용.
  2. 전역 설정에서 `CorsConfigurer` 활용.
- 전역 설정 방식이 유지보수 관점에서 더 유리.
