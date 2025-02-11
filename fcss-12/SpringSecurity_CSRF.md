# CSRF란?

## Cross-Site Request Forgery (사이트 간 요청 위조)

웹 애플리케이션에서 발생할 수 있는 취약점 중 하나로, **사용자의 의도와는 무관하게 공격자가 의도한 행동을 수행하여 보안을 위협**하거나 수정/삭제 등의 작업을 하는 공격
방법입니다.

- 공격 난이도가 높지 않아 흔히 사용됩니다.

---

# 스프링 시큐리티에서의 CSRF

## 기본 CSRF 보호

스프링 시큐리티는 기본적으로 CSRF 보호를 활성화합니다. HTTP **POST** 엔드포인트 호출 시 CSRF 보호가 적용되며, 비활성화하지 않으면 POST 요청은 바로 호출할
수 없습니다.

---

# CSRF 보호가 작동하는 방식

## 예시 환경: 파일 저장 및 관리 시스템

1. 시스템은 새 파일을 추가하거나 기존 파일을 삭제하는 기능을 제공합니다.
2. 사용자는 출처를 알 수 없는 이메일의 링크를 클릭하여 빈 페이지가 열립니다.
3. 이후, 페이지는 다른 웹페이지로 리다이렉션 되고 모든 파일이 삭제된 것을 발견합니다.
4. 원인:

- 파일 관리 작업(추가/삭제)은 시스템에 로그인을 해야 가능합니다.
- **공격자는 사용자가 이미 로그인을 완료했음을 악용**해, 사용자의 세션 인증 정보를 기반으로 파일 삭제 엔드포인트를 호출합니다.
- 서버 입장에서는 "이미 인증된 사용자의 요청"이라고 판단하여 공격임을 알아채지 못합니다.

---

# CSRF 공격 방지 방법

1. 엔드포인트마다 역할에 맞게 HTTP 메서드(HTTP GET, POST, DELETE 등)를 구분합니다.
2. **GET, HEAD, TRACE, OPTIONS** 요청을 제외한 다른 요청들은 반드시 CSRF 보호를 통해 동작하도록 설정합니다.

---

# CSRF 보호의 동작 흐름

1. 클라이언트가 **HTTP GET 요청**을 보냅니다.
2. 서버는 고유한 **CSRF 토큰**을 생성하여 클라이언트에 반환합니다.
3. 클라이언트는 **POST 요청** 등의 요청을 보낼 때 이전에 받은 CSRF 토큰을 함께 전달해 유효성을 증명합니다.
4. 서버는 전달된 CSRF 토큰이 유효한 경우에만 요청을 허용합니다.

---

# CSRF Filter

스프링 시큐리티의 `CsrfFilter`를 통해 CSRF 보호를 적용할 수 있습니다.

## 주요 구성 요소

`CsrfFilter`는 **OncePerRequestFilter를 확장**한 클래스로 구현되어 있으며, 다음 구성 요소를 포함하고 있습니다:

- **RequestMatcher**
- **CsrfTokenRequestHandler**

## 동작 방식

1. `GET`, `HEAD`, `TRACE`, `OPTIONS` 메서드는 CSRF 보호 없이 모두 허용합니다.
2. 기타 메서드(POST, DELETE 등)의 경우, **CSRF 토큰이 포함된 헤더가 있는지 확인**합니다.

- 헤더에 올바른 토큰 값이 없거나 헤더 자체가 없다면, 요청을 거부하고 **403 Forbidden** 응답을 반환합니다.

3. CSRF 토큰은 문자열로 제공되며, 주요 요청에는 이 토큰이 반드시 헤더에 포함되어야 합니다.

---

# CSRF 토큰 관리

`CsrfTokenRepository`를 사용하여 CSRF 토큰의 생성을 관리하고 저장할 수 있습니다.

- **토큰 생성 방식**: UUID 형태로 생성
- **토큰 저장 방식**: HTTP 세션에 저장

---

# CSRF 토큰 값을 확인하는 방법

`CsrfFilter`에서 생성된 CSRF 토큰은 HTTP 요청의 **`_csrf` 속성**에 추가됩니다.  
이를 알기 위해선 다음과 같은 로그 필터를 커스텀 추가하여 확인할 수 있습니다.

## 1. CSRF 토큰 로깅 필터

```java

@Slf4j
@Component
public class CsrfTokenLogger implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    CsrfToken csrf = (CsrfToken) servletRequest.getAttribute("_csrf");
    log.info("csrf token={}", csrf.getToken());
    filterChain.doFilter(servletRequest, servletResponse);
  }

}
```

## 2. 스프링 시큐리티 설정에서 필터 연결

```java

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  // 생략

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.httpBasic(Customizer.withDefaults());

    httpSecurity.addFilterAfter(csrfTokenLogger, CsrfFilter.class);

    return httpSecurity.build();
  }
}
```

## 적용 방법

1. `GET` 방식의 엔드포인트를 호출하여 CSRF 토큰 값을 생성합니다.
2. 이후 생성된 토큰을 확인하여, 이를 **POST 요청** 등 CSRF 보호가 필요한 요청에 포함시키면 됩니다.

--- 
