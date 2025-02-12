# 사전 및 사후 필터링

## 개요
이전 시간에는 `@PreAuthorize`, `@PostAuthorize`를 활용하여 권한 부여 규칙을 적용하는 방법에 대해 학습했음.

- 규칙에 따라 **아예 호출을 제한**하거나,
- 호출은 허용하지만 **결과값 반환을 제한**할 수 있음.
- **스프링에서 사용하는 표현식**을 활용하거나, `PermissionEvaluator`의 `hasPermission` 메서드를 구현하여 활용 가능.

### 사전 및 사후 필터링 활용 시점

1. **메서드 파라미터가 특정 규칙을 따르는지 확인**하고 싶을 때.
2. **결과값에서 승인된 부분만 받아야** 하는 경우.

- **사전 필터링 (PreFiltering):** 프레임워크가 메서드 호출 전에 매개 변수의 값을 필터링.
- **사후 필터링 (PostFiltering):** 프레임워크가 메서드 호출 후 반환된 값을 필터링.

---

## 사전 필터링

### 특징
- 파라미터나 반환 결과값이 규칙을 준수하지 않아도 호출은 발생함.
  - 예외를 던지지 않음.
  - 조건에 맞지 않는 파라미터나 반환 값은 필터링함.
- **컬렉션** 또는 **배열**에서만 활용 가능.

### 장점
- **비즈니스 로직**과 **권한 부여 규칙**을 분리 가능.
- 예를 들어, 인증된 사용자가 소유한 특정 데이터만 처리하는 기능이 여러 위치에서 호출되는 경우:
  - **방법 1:** 클라이언트에게 인증된 데이터만 처리하도록 요청 → 부적절.
  - **방법 2:** 비즈니스 로직에 인증 로직 포함 → 책임의 분리 필요.
  - **방법 3:** **사전 필터링 (@PreFilter)** 활용 → 권한 부여 규칙 분리 가능.

---

## 예시: 영화/드라마 조회 API

### 요구사항
1. 사용자가 "좋아요"를 누른 영화/드라마 목록만 조회 가능.
2. 메서드는 어디서든 호출 가능.

---

### SecurityConfig 설정

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // danny.kim 사용자 추가
        manager.createUser(
            User.withUsername("danny.kim")
                .password("12345")
                .authorities("READ")
                .build()
        );
        // steve.kim 사용자 추가
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

### 영화/드라마 객체 도메인 (Content)

```java
import lombok.Getter;

@Getter
public class Content {
    private final String name;  // 좋아요를 누른 영화/드라마 이름
    private final String owner; // 좋아요를 누른 사용자

    public Content(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }
}
```

---

### ContentController 구현

#### 엔드포인트 생성 (GET `/api/v1/contents`)
- `danny`와 `steve`가 좋아요를 누른 목록 추가.

```java
@RestController
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/api/v1/contents")
    public List<Content> getContents() {
        List<Content> allContents = new ArrayList<>();
        allContents.add(new Content("최강야구", "danny.kim"));
        allContents.add(new Content("오펜하이머", "danny.kim"));
        allContents.add(new Content("돌풍", "danny.kim"));
        allContents.add(new Content("나는솔로", "danny.kim"));
        allContents.add(new Content("눈물의여왕", "steve.kim"));
        allContents.add(new Content("태어난 김에 세계일주", "steve.kim"));
        allContents.add(new Content("이태원 클라쓰", "steve.kim"));
        return contentService.searchContents(allContents);
    }

    @GetMapping("/api/v2/contents")
    public List<Content> getContentsV2() {
        return contentService.searchContentsV2();
    }
}
```

---

### ContentService 구현

#### @PreFilter 사용

```java
@Service
public class ContentService {

    @PreFilter("filterObject.owner == authentication.name")
    public List<Content> searchContents(List<Content> contents) {
        return contents;
    }
}
```

---

### 주의사항
- **사전 필터링은 컬렉션**의 데이터 추가/삭제를 기반으로 하기 때문에 **MutableList**여야 함.
- 만약 `ArrayList` 선언 시 데이터와 함께 초기화하면 일반 `List`가 되고, **`UnsupportedOperationException`** 발생.
- 예를 들어, 아래와 같은 방식은 오류 발생:
    ```java
    List<Content> contents = List.of(new Content("sample", "user"));
    ```
