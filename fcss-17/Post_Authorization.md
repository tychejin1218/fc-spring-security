# 사후 권한 부여 (Post-Authorization)

**사전 권한 부여**와 달리, **사후 권한 부여**는 메소드 호출 자체는 조건 여부와 관계없이 허용하지만,  
조건 충족 여부에 따라 결과값 반환을 제한하는 방식입니다.

## 사후 권한 부여의 필요성
사후 권한 부여는 다음과 같은 상황에서 유용합니다:
- 특정 작업이 수행된 후 데이터 결과를 기준으로 권한 충족 여부를 확인할 필요가 있을 때
- 예시: 데이터베이스에서 조회한 데이터 결과를 반환 여부 조건으로 판단해야 하는 엔드포인트

### 동작 방식
1. 메소드 호출은 조건과 관계없이 실행됩니다.
2. 보안 로직은 호출 결과를 기반으로 권한 조건을 검사합니다.
3. 조건을 충족하지 않으면 결과를 반환하지 않고 예외를 던집니다.

---

## 사후 권한 부여 예시

### 그림으로 표현:
BookController <-> 보안 Aspect <-> BookService

- **1.** `BookController`는 직원이 읽은 책의 세부 정보를 검색할 수 있는 엔드포인트를 구현합니다.
- **2.** 직원에게 **읽기 권한**이 있어야 데이터를 반환합니다.
    - 읽기 권한은 **데이터베이스**에서 관리합니다.
    - 보안 Aspect는 서비스 메소드 호출 전 이 정보를 알 수 없기 때문에 호출을 위임합니다.
- **3.** `BookService`는 메소드 결과값을 반환하며, 보안 Aspect는 이를 기반으로 권한을 판단합니다.
- **4.** 읽기 권한이 없는 경우 **예외**를 던집니다.

---

### Employee 클래스
- 각 직원은 다음과 같은 속성을 가지고 있습니다:
    - 이름 (`name`)
    - 직원이 읽은 책 목록 (`books`)
    - 직원의 역할 목록 (`roles`)
- 역할에 따라 해당 직원이 읽은 책을 반환할지 결정해야 합니다.

```java
@Getter
public class Employee {

  private final String name;
  private final List<String> books;
  private final List<String> roles;

  public Employee(String name, List<String> books, List<String> roles) {
    this.name = name;
    this.books = books;
    this.roles = roles;
  }
}
```

---

### BookService 클래스
- `@PostAuthorize`를 적용하여 메소드 호출 후 반환된 객체에서 권한 조건을 확인합니다.
- 조건:
    - 반환되는 객체 (`returnObject`)의 `roles` 필드에 **engineer**가 포함되어 있으면 반환.
- 역할 정보를 쉽게 정의하기 위해 Map을 활용합니다:

```java
@Service
public class BookService {

  private Map<String, Employee> records = Map.of(
    "danny.kim", new Employee("Danny Kim", List.of("book 1"), List.of("product owner")),
    "steve.kim", new Employee("Steve Kim", List.of("book 2"), List.of("engineer"))
  );

  @PostAuthorize("returnObject.roles.contains('engineer')")
  public Employee getBooks(String name) {
    return records.get(name);
  }
}
```

---

### BookController 클래스
- 엔드포인트에서 `name`을 전달받아 Map에서 데이터 조회.
- 반환된 데이터에서 **engineer** 권한 확인 후 반환 여부 결정.

```java
@RestController
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/api/v1/book/{name}")
  public Employee getBook(@PathVariable String name) {
    return bookService.getBooks(name);
  }
}
```

---

## 정리
- **사전 권한 부여:** 메소드 호출 자체를 권한 조건으로 제한 (메소드 실행 이전에 판단)
- **사후 권한 부여:** 메소드 결과를 반환하기 전에 권한 조건 적용 (메소드 실행 이후 데이터 기반 판단)
- `@PostAuthorize`는 메소드 결과값을 기반으로 권한을 판단하며, 전형적인 케이스로는 반환 객체의 특정 프로퍼티를 검사하여 맞지 않으면 예외를 던짐.
