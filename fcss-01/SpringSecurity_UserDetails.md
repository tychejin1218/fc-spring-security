# UserDetails 살펴보기

스프링 시큐리티에서 **사용자 관리와 권한 부여의 핵심**은 바로 `UserDetailsService`, `UserDetailsManager`, 그리고 `GrantedAuthority` 인터페이스에 있습니다.  
이번 글에서는 이 인터페이스들이 어떻게 동작하고, 스프링 시큐리티의 인증 및 권한 부여 시스템에서 어떤 역할을 수행하는지 알아보겠습니다.

## 목차

1. [스프링 시큐리티의 인증 흐름에서의 사용자 관리 부분](#스프링-시큐리티의-인증-흐름에서의-사용자-관리-부분)
2. [사용자 관리를 위한 인터페이스 소개](#사용자-관리를-위한-인터페이스-소개)
3. [UserDetailsService](#1-userdetailsservice)
4. [UserDetailsManager](#2-userdetailsmanager)
5. [GrantedAuthority](#3-이용-권리의-집합-grantedauthority)
6. [UserDetails](#4-userdetails)
7. [사용자 비활성화 기능](#5-사용자-비활성화-기능)
8. [권한의 의미](#6-권한의-의미)
9. [요약](#결론)

## 스프링 시큐리티의 인증 흐름에서의 사용자 관리 부분

### 인증 흐름 설명

스프링 시큐리티에서 인증(Authentication)은 다음 흐름으로 처리됩니다:

1. **인증 필터 (Authentication Filter)**
  - 클라이언트의 요청(Request)을 가로채어 인증 절차를 시작합니다.
  - 요청 정보를 이용해 인증 토큰(Authentication Token)을 생성하고, 해당 토큰을 **인증 관리자(Authentication Manager)**에 전달합니다.
  - 예: `UsernamePasswordAuthenticationFilter`는 ID와 암호 기반 인증을 처리.

2. **인증 관리자 (Authentication Manager)**
  - 인증 과정을 총괄하는 핵심 구성 요소입니다.
  - **인증 공급자(Authentication Provider)**를 통해 실제 인증 로직을 수행합니다.
  - 요청에 해당하는 사용자 정보를 검증하여 성공 여부를 반환합니다.

3. **인증 공급자 (Authentication Provider)**
  - 실제 인증을 처리하는 컴포넌트로, `UserDetailsService` 또는 `UserDetailsManager`를 호출하여 사용자 정보를 조회하고 인증합니다.
  - 사용자 이름 (`username`)과 암호 (`password`), 혹은 다른 인증 요소들을 비교 및 평가합니다.
  - 여러 공급자를 구성해 다양한 인증 방식(e.g., JWT, OAuth2 등)을 처리할 수 있습니다.

---

### 예시: 인증 과정

```plaintext
[요청] -> 인증 필터 -> 인증 관리자 -> 인증 공급자 -> UserDetailsService(사용자 조회)
```

스프링 시큐리티에서 인증은 필터, 관리자, 공급자 간의 협력으로 이루어지며, 이 중 **사용자 관리 부분**은 `UserDetailsService`와 `PasswordEncoder`를 통해 수행됩니다.

---

## 사용자 관리를 위한 인터페이스 소개

- **`UserDetailsService`와 `UserDetailsManager` 인터페이스를 사용하여 사용자 관리를 수행**
    - `UserDetailsService`: 사용자 이름으로 사용자를 검색 (Read).
    - `UserDetailsManager`: 사용자 추가, 수정, 삭제 (Create, Update, Delete).

### SOLID 원칙 적용

- 두 인터페이스의 분리는 **인터페이스 분리 원칙** (Interface Segregation Principle)에 해당함.
- **프레임워크 유연성 향상**:
    - 인증 기능만 필요 → `UserDetailsService`만 구현.
    - CRUD 기능 제공 → `UserDetailsManager`도 구현.

---

## 1. UserDetailsService

`UserDetailsService`는 유저 정보를 조회하는 메소드만 제공하는 간단한 인터페이스입니다.  
`username`을 입력받아, 해당 사용자의 정보를 담은 `UserDetails` 객체를 반환하는 **`loadUserByUsername`** 메서드를 제공합니다.

### 주요 메서드

```java
package org.springframework.security.core.userdetails;

// 사용자 이름으로 사용자 세부 정보를 로드
public interface UserDetailsService {

  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

---

## 2. UserDetailsManager

사용자를 생성, 수정, 삭제, 암호 변경하거나 사용자 존재 여부를 확인하는 등의 CRUD 메소드를 제공하는 인터페이스입니다.  
`UserDetailsService`를 상속받기 때문에, **이 인터페이스를 구현하려면 `loadUserByUsername` 메서드도 구현해야 합니다.**

### 주요 메서드

```java
package org.springframework.security.provisioning;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserDetailsManager extends UserDetailsService {

  void createUser(UserDetails user);

  void updateUser(UserDetails user);

  void deleteUser(String username);

  void changePassword(String oldPassword, String newPassword);

  boolean userExists(String username);
}
```

---

## 3. 이용 권리의 집합: GrantedAuthority

사용자가 수행 가능한 동작 집합을 **권한(Authority)**이라고 합니다.  
예로는 **데이터 조회, 수정, 업로드 권한** 등이 있습니다.  
권한은 `GrantedAuthority`라는 인터페이스를 통해 구현됩니다.

### 주요 메서드

```java
package org.springframework.security.core;

import java.io.Serializable;

public interface GrantedAuthority extends Serializable {

  // 사용자 권한을 나타내는 문자열 반환
  String getAuthority();
}
```

---

## 4. UserDetails

`UserDetails`는 사용자 인증과 권한에 대한 핵심 인터페이스입니다.

### 구조 정의

`UserDetails`는 사용자 정보(이름, 암호, 권한 등)를 캡슐화하며, 인증 및 권한 관리를 수행하는 인터페이스의 기반이 되는 구조입니다.

```java
package org.springframework.security.core.userdetails;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface UserDetails extends Serializable {

  // 사용자의 권한 리스트 반환 (예: ROLE_USER, ROLE_ADMIN 등)
  Collection<? extends GrantedAuthority> getAuthorities();

  // 암호 반환
  String getPassword();

  // 사용자 이름(ID) 반환
  String getUsername();

  // 아래는 계정 상태를 나타내는 기본 메서드들 (필요 시 오버라이드 가능)

  // 계정이 만료되었는지 여부 (기본값: 만료되지 않음)
  default boolean isAccountNonExpired() {
    return true;
  }

  // 계정이 잠겼는지 여부 (기본값: 잠겨 있지 않음)
  default boolean isAccountNonLocked() {
    return true;
  }

  // 자격 증명이 만료되었는지 여부
  default boolean isCredentialsNonExpired() {
    return true;
  }

  // 계정 활성화 여부 (기본값: 활성화됨)
  default boolean isEnabled() {
    return true;
  }
}
```

이 구조는 **인증 공급자(Authentication Provider)**를 통해 호출되는 `UserDetailsService` 또는 `UserDetailsManager` 구현 시 핵심적인 반환 타입으로 사용됩니다. 특히 `UserDetailsManager`는 사용자 관리(CRUD)를 제공하기 위해 `UserDetails` 데이터를 조작하여 계정 활성화, 삭제 등의 작업을 수행합니다.

---

## 5. 사용자 비활성화: 계정 제약 가능

- 사용자 제약 가능:
    - 스프링 시큐리티는 기본적으로 모든 사용자 계정을 활성화하고 제약 없이 허용하는 설정으로 동작합니다.
    - 하지만 필요에 따라 계정 만료, 잠금, 비활성화 등 제약사항을 지정할 수 있습니다.
    - 계정 만료: `isAccountNonExpired()`.
    - 계정 잠금: `isAccountNonLocked()`.
    - 자격 증명 만료: `isCredentialsNonExpired()`.
    - 계정 비활성화: `isEnabled()`.

> **기본값으로 모두 `true` 반환됨** (즉, 제약 없음).  
> 원하는 경우 계정 상태 체크 메서드를 **오버라이드**하여 개인 요구 사항에 맞게 구현하십시오.

---

## 6. 권한의 의미

- **권한 (Authority)**:
    - 사용자에게 허가된 작업의 집합.
    - 애플리케이션 내 역할 관리에 사용.

### 권한이 필요한 예

1. 관리자와 일반 사용자 구분.
2. 특정 메뉴나 데이터를 제한적으로 접근해야 할 때.
3. 조회만 가능한 사용자와 수정 권한도 가진 사용자를 구분.

---

## 7. GrantedAuthority

- **사용자 권한을 나타내는 인터페이스**.
- 구현 방법:
    1. 직접 `GrantedAuthority` 인터페이스 구현.
    2. `SimpleGrantedAuthority` 클래스 사용.

### 권한 선언 예시

```java
// 방법 1: 관리자 권한 설정 (권장: SimpleGrantedAuthority 클래스 사용)
GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");

// 방법 2: 일반 사용자 권한 설정
GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");

// 방법 3: 복수 권한 설정 예
List<GrantedAuthority> authorities = Arrays.asList(
        new SimpleGrantedAuthority("ROLE_USER"),
        new SimpleGrantedAuthority("CAN_VIEW_REPORTS"),
        new SimpleGrantedAuthority("CAN_UPLOAD_FILES")
);
```

---

### 요소 간 관계 정리: 역할 연결

- UserDetailsManager -> UserDetailsService -> UserDetails <- GrantedAuthority

1. `UserDetailsManager`는 `UserDetailsService`를 상속받습니다.
    - 사용자 CRUD와 검색 메서드를 제공.

2. `UserDetailsService`는 ID 기반으로 사용자 정보를 반환합니다.
    - 반환 타입은 `UserDetails` 객체입니다.

3. `UserDetails`는 인증 및 권한 관리의 핵심 정보(Username, Password, GrantedAuthority)를 담습니다.
    - 또한 계정의 상태(활성화 여부 등)를 체크합니다.

4. `GrantedAuthority`는 사용자 권한(예: ROLE_ADMIN, ROLE_USER)을 정의합니다.

---

## 요약

스프링 시큐리티의 인증 흐름과 사용자 권한 관리는 다음 인터페이스를 중심으로 구성됩니다.

1. **`UserDetailsService`**: 사용자 세부 정보를 검색합니다.
2. **`UserDetailsManager`**: 사용자 추가, 삭제 등 CRUD 작업을 제공합니다.
3. **`GrantedAuthority`**: 특정 사용자 권한을 정의합니다.


## 참고 자료
- [Spring Security 공식 문서](https://spring.io/projects/spring-security)
- [Spring Boot와 Security 통합 프로젝트 예제](https://spring.io/guides/gs/securing-web/)
