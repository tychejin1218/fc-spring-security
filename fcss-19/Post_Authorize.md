# 사후 필터링 적용

## 개요
이전 시간에는 `@PreAuthorize`, `@PostAuthorize`를 활용하여 권한 부여 규칙을 적용하는 방법을 학습했습니다.

- **규칙에 따라 호출을 제한**하거나,
- 호출은 허용하지만 **결과값 반환을 제한**할 수 있습니다.
- **스프링에서 사용하는 표현식**을 활용하거나, `PermissionEvaluator`의 `hasPermission` 메서드를 구현해 활용할 수 있습니다.

### 사전 및 사후 필터링 활용 시점

1. **메서드 파라미터가 특정 규칙을 따르는지 확인**하고 싶을 때.
2. **결과값에서 승인된 부분만 받아야 하는 경우.**

- **사전 필터링 (`@PreFilter`)**: 메서드 호출 전에 매개 변수의 값을 필터링.
- **사후 필터링 (`@PostFilter`)**: 메서드 호출 후 반환된 값을 필터링.

---

## 사후 필터링 적용

### 사후 필터링은 어느 상황에서 사용될까?
ReactJS로 구현된 프론트엔드와 Spring으로 구현된 백엔드가 서로 연결된 상황을 가정합니다.

- **프론트엔드에서는 로그인된 사용자에 대한 데이터만 조회**해야 합니다.
- 하지만 **어떤 데이터가 존재하는지는 프론트엔드가 알지 못합니다.**
- 따라서, 로그인 정보를 백엔드로 전달하면 **백엔드에서 필터링된 결과를 반환하는 방식**으로 처리합니다.

#### 예시
로그인 정보: `"danny.kim"`

- 요청 데이터:
    ```json
    [
      {"owner": "danny.kim"},
      {"owner": "steve.kim"},
      {"owner": "harris.kim"}
    ]
    ```
- 백엔드가 필터링 결과를 반환:
    ```json
    [
      {"owner": "danny.kim"}
    ]
    ```

---

## 구현 방법

### ContentService

`@PostFilter`를 활용하여 반환되는 컬렉션 객체에서 `owner` 값이 인증된 사용자의 이름과 동일한 객체만 필터링하여 반환합니다.

```java
@Service
public class ContentService {

    @PostFilter("filterObject.owner == authentication.name")
    public List<Content> searchContentsV2() {
        List<Content> allContents = new ArrayList<>();
        allContents.add(new Content("최강야구", "danny.kim"));
        allContents.add(new Content("오펜하이머", "danny.kim"));
        allContents.add(new Content("돌풍", "danny.kim"));
        allContents.add(new Content("나는솔로", "danny.kim"));
        allContents.add(new Content("눈물의여왕", "steve.kim"));
        allContents.add(new Content("태어난 김에 세계일주", "steve.kim"));
        allContents.add(new Content("이태원 클라쓰", "steve.kim"));
        return allContents;
    }
}
```

---

### ContentController

`GET /api/v2/contents` 엔드포인트를 생성하여 필터링된 데이터를 반환합니다.

```java
@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/api/v2/contents")
    public List<Content> getContentsV2() {
        return contentService.searchContentsV2();
    }
}
```

---

### SecurityConfig 설정

`InMemoryUserDetailsManager`를 활용하여 두 개의 사용자 (`danny.kim`, `steve.kim`)를 생성합니다.

- **비밀번호는 평문으로 처리**하기 위해 `NoOpPasswordEncoder`를 사용합니다.

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
            User.withUsername("danny.kim")
                .password("12345")
                .authorities("READ")
                .build()
        );

        manager.createUser(
            User.withUsername("steve.kim")
                .password("12345")
                .authorities("READ")
                .build()
        );

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }
}
```

---

### 콘텐츠 도메인 클래스

```java
import lombok.Getter;

@Getter
public class Content {
    private final String name;  // 영화/드라마 이름
    private final String owner; // 사용자 이름

    public Content(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }
}
```

---

### 결과

- **엔드포인트:** `GET /api/v2/contents`
- 인증된 사용자(`danny.kim`)로 요청.
- 백엔드에서 필터링된 콘텐츠만 반환.

```json
[
  {"name": "최강야구", "owner": "danny.kim"},
  {"name": "오펜하이머", "owner": "danny.kim"},
  {"name": "돌풍", "owner": "danny.kim"},
  {"name": "나는솔로", "owner": "danny.kim"}
]
```
