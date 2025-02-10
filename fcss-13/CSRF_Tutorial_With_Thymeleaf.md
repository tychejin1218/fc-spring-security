# Spring Security와 Thymeleaf CSRF 동작 정리

CSRF 보호는 Spring Security와 Thymeleaf 통합을 통해 제공됩니다. 아래는 관련 정보와 예제 코드입니다.

---

## **CSRF 정보는 어디서 설정되나요?**

**Spring Security의 CSRF Token 생성 및 관리:**
- Spring Security는 요청마다 CSRF 토큰을 생성하고 관리합니다.
- CSRF 검사는 SecurityFilterChain의 **CsrfFilter**에 의해 수행됩니다.

**CSRF Token 노출:**
- Spring Security는 **CsrfTokenRepository**를 사용하여 토큰을 저장합니다.
    - Default Repository: `HttpSessionCsrfTokenRepository` (서버 세션에 토큰 저장).
- 클라이언트 요청으로 토큰을 전달하는 방법:
    - 세션을 통한 저장.
    - 쿠키를 활용하여 전송.

**Thymeleaf에서 사용:**
- Thymeleaf 템플릿 내에서 **${_csrf}** 객체를 통해 CSRF 관련 정보를 사용할 수 있습니다.
- 예:
  ```html
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
  ```

---

## **CSRF 토큰의 동작 과정**

1. **토큰 생성 및 저장:**
    - CSRF 토큰은 `CsrfFilter`에 의해 생성됩니다.
    - 기본적으로 `HttpSessionCsrfTokenRepository`에 저장됩니다.
2. **HTML 페이지 바인딩:**
    - Spring Security는 `CsrfToken` 객체를 자동으로 Spring MVC의 모델 속성에 바인딩합니다.
    - **Thymeleaf** 템플릿에서 CSRF 데이터를 사용할 수 있도록 제공합니다.
3. **요청 시 유효성 검사:**
    - 클라이언트가 전송한 CSRF 토큰과 서버의 토큰을 비교하여 유효성을 확인합니다.

---

## **CSRF 토큰 저장소 종류**

1. **`HttpSessionCsrfTokenRepository` (기본값):**
    - CSRF 토큰을 서버 세션에 저장합니다.
2. **`CookieCsrfTokenRepository`:**
    - 클라이언트 쿠키에 CSRF 토큰을 저장.
3. **`LazyCsrfTokenRepository`:**
    - 요청이 필요할 때만 CSRF 토큰을 생성합니다.

---

## **예제 코드**

### 1. `SecurityConfig.java`

```java
package fast.campus.fcss13.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
    userDetailsManager.createUser(User.withUsername("danny.kim")
        .password("12345")
        .build());
    return userDetailsManager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(c -> c.anyRequest().authenticated());
    httpSecurity.formLogin(c -> c.defaultSuccessUrl("/main", true));
    return httpSecurity.build();
  }
}
```

---

### 2. `MainController.java`

```java
package fast.campus.fcss13.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MainController {

  @GetMapping("/main")
  public String main() {
    return "main.html";
  }

  @PostMapping("/add")
  public String add(@RequestParam("name") String name) {
    log.info("POST /add. name={}", name);
    return "main.html";
  }
}
```

---

### 3. HTML 템플릿 (`main.html`)

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Main Page</title>
</head>
<body>
    <form action="/add" method="post" th:action="@{/add}" th:method="post">
        <span>Name:</span>
        <span><input type="text" name="name"/></span>
        <span><button type="submit">add</button></span>
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    </form>
</body>
</html>
```

---

## **동작 설명**

### **1. 로그인 페이지**
- 애플리케이션 실행 후 `/login` 페이지로 이동하여 사용자 인증.
    - **Username**: `danny.kim`
    - **Password**: `12345`

### **2. Main 페이지 (`/main`)**
- 로그인 성공 시 `/main`으로 리다이렉트됩니다.
- 화면에는 이름을 입력하고 전송할 수 있는 폼이 표시됩니다.

### **3. 데이터 전송 (POST 요청 `/add`)**
- 사용자가 입력한 이름 데이터를 `/add`로 POST 요청.
- 요청 시 CSRF 토큰 데이터도 함께 전송되어 서버에서 유효성을 검증합니다.
- 서버 로그 예제:

---

## **요약**
- Spring Security는 CSRF 보호를 활성화하여, 포괄적인 웹 애플리케이션 보안을 제공합니다.
- **Thymeleaf**와의 통합을 통해 CSRF 토큰을 쉽게 관리하고 HTML 폼에서 사용할 수 있습니다.

---
