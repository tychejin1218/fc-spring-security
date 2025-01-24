# Spring Security - PasswordEncoder 정리

Spring Security를 활용하는 애플리케이션에서 중요한 보안 요소 중 하나는 **비밀번호 처리**입니다. 운영 환경에서는 비밀번호를 평문으로 저장하거나 검증하지 말아야 하며, 안전한 방식으로 암호화와 검증을 수행해야 합니다. 이를 위해 Spring Security는 **PasswordEncoder**라는 인터페이스를 제공합니다. 이번 글에서는 `PasswordEncoder`의 원리, 인터페이스 정의, 주요 구현체 등을 살펴보겠습니다.

---

## 목차
1. [PasswordEncoder란?](#passwordencoder란)
2. [PasswordEncoder 인터페이스 정의](#passwordencoder-인터페이스-정의)
3. [PasswordEncoder 구현 시 주의점](#passwordencoder-구현-시-주의점)
4. [Spring Security의 PasswordEncoder 구현체](#spring-security의-passwordencoder-구현체)
  - [NoOpPasswordEncoder](#1-nooppasswordencoder)
  - [StandardPasswordEncoder](#2-standardpasswordencoder)
  - [Pbkdf2PasswordEncoder](#3-pbkdf2passwordencoder)
  - [BCryptPasswordEncoder](#4-bcryptpasswordencoder)
  - [SCryptPasswordEncoder](#5-scryptpasswordencoder)
  - [DelegatingPasswordEncoder](#6-delegatingpasswordencoder)
5. [PasswordEncoder 구현체 선택 가이드](#passwordencoder-구현체-선택-가이드)
6. [요약](#요약)
7. [참고 자료](#참고-자료)

---

## PasswordEncoder란?

`PasswordEncoder`는 사용자 비밀번호에 대한 암호화와 검증을 수행하는 **Spring Security 인터페이스**입니다.  
인증 프로세스에서 사용자의 암호가 올바른지 검증하거나, 보안을 위해 암호를 안전하게 저장하는 데 사용됩니다.

### PasswordEncoder를 사용하는 주요 이유
1. **비밀번호 암호화**: 비밀번호를 안전하게 저장하고 외부 노출을 방지.
2. **비밀번호 검증**: 데이터베이스에 저장된 암호화된 값과 입력된 비밀번호를 비교.
3. **보안 강화**: 해커가 비밀번호를 쉽게 분석하거나 접근하지 못하도록 안전한 알고리즘 사용.

---

## PasswordEncoder 인터페이스 정의

Spring Security가 제공하는 `PasswordEncoder` 인터페이스에는 암호화 및 검증을 위한 **3가지 메서드**가 정의되어 있습니다.

```java
public interface PasswordEncoder {

  // 원시 비밀번호를 암호화하여 문자열로 반환
  String encode(CharSequence rawPassword);

  // 암호화된 비밀번호와 원시 비밀번호가 일치하는지 검사
  boolean matches(CharSequence rawPassword, String encodedPassword);

  // 비밀번호 재인코딩이 필요한지 판단 (기본적으로 false 반환).
  default boolean upgradeEncoding(String encodedPassword) {
    return false;
  }
}
```

---

## PasswordEncoder 구현 시 주의점

### 주요 사항
1. **단방향 암호화 준수**: 비밀번호는 복호화할 수 없는 방식으로 저장해야 합니다.
  - 예: `BCrypt`, `PBKDF2`, `SCrypt`.
2. **무작위성 추가(Salt)**: 동일한 입력 값에 대해 다른 암호화 결과를 생성하도록 설계.
3. **강결합된 검증**: `encode` 함수의 결과는 반드시 `matches` 함수로 검증 가능해야 합니다.

---

## Spring Security의 PasswordEncoder 구현체

스프링 시큐리티는 **다양한 비밀번호 처리 방식**을 구현한 PasswordEncoder 구현체를 제공합니다.

---

### 1. NoOpPasswordEncoder
- 비밀번호를 **암호화하지 않고 그대로 평문으로 반환**합니다.
- 보안성이 없으므로 테스트와 학습 목적으로만 사용되며, **운영 환경에서는 절대 사용하면 안 됩니다**.
- `encode` 메서드는 `rawPassword`를 그대로 문자열로 변환해 반환합니다.
- 
#### 주요 코드

```java
@Deprecated
public final class NoOpPasswordEncoder implements PasswordEncoder {
  
  // 생략
  public String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }
}
```

---

### 2. StandardPasswordEncoder
- **SHA-256 알고리즘과 Salt값**을 사용해 비밀번호를 암호화합니다.
- 현재는 `Deprecated` 상태이며, 보안 표준 변화로 인해 더 이상 사용하지 않는 것이 좋습니다.
- 내부적으로 Salt 추가를 통해 동일한 입력값이라도 출력값이 다르게 나타나도록 처리합니다.
 
#### 주요 코드

```java
@Deprecated
public final class StandardPasswordEncoder implements PasswordEncoder {
  
  // 생략
  private String encode(CharSequence rawPassword, byte[] salt) {
    byte[] digest = this.digest(rawPassword, salt);
    return new String(Hex.encode(digest));
  }
}
```

---

### 3. Pbkdf2PasswordEncoder
- **PBKDF2(Password-Based Key Derivation Function 2)** 알고리즘을 사용하여 연산 비용(반복 횟수)을 증가시킴으로써 **brute-force 공격 방지**에 특화된 암호화 구현체입니다.
- 설정 가능한 반복 횟수와 해싱 길이를 통해 보안을 더욱 강화할 수 있으며, 운영 환경에서도 널리 사용됩니다.
- Salt를 활용하여 안전한 단방향 암호화를 제공합니다.

#### 주요 코드

```java
public class Pbkdf2PasswordEncoder implements PasswordEncoder {
    
    // 생략
    private byte[] encode(CharSequence rawPassword, byte[] salt) {
      try {
        PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(), EncodingUtils.concatenate(new byte[][]{salt, this.secret}), this.iterations, this.hashWidth);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(this.algorithm);
        return EncodingUtils.concatenate(new byte[][]{salt, skf.generateSecret(spec).getEncoded()});
      } catch (GeneralSecurityException var5) {
        GeneralSecurityException ex = var5;
        throw new IllegalStateException("Could not create hash", ex);
      }
    }
}
```

---

### 4. BCryptPasswordEncoder
- 가장 널리 사용되는 구현체로, **bcrypt 단방향 암호화 알고리즘**을 기반으로 동작합니다.
- **랜덤 Salt**를 자동으로 추가해 동일한 비밀번호에도 매번 다른 암호화 값을 생성하므로 보안성이 뛰어납니다.
- 높은 보안성과 효율성을 동시에 제공하므로 스프링 애플리케이션의 **운영 환경에서 가장 추천됩니다.

#### 주요 코드

```java
public class BCryptPasswordEncoder implements PasswordEncoder {
  
  // 생략
  public String encode(CharSequence rawPassword) {
    if (rawPassword == null) {
      throw new IllegalArgumentException("rawPassword cannot be null");
    } else {
      String salt = this.getSalt();
      return BCrypt.hashpw(rawPassword.toString(), salt);
    }
  }
}
```

---

### 5. SCryptPasswordEncoder
- **SCrypt 알고리즘**은 메모리와 CPU 자원을 많이 소비하여 brute-force 공격 방지에 탁월합니다.
- 메모리와 CPU 집약적인 환경에서 암호화를 수행하므로 고도의 보안이 요구되는 시스템에서 적합합니다.
- Salt 추가와 강력한 암호화 제공으로 암호 정보 보호를 강화합니다.

#### 주요 코드

```java
public class SCryptPasswordEncoder implements PasswordEncoder {
  
  // 생략
  public String encode(CharSequence rawPassword) {
    return this.digest(rawPassword, this.saltGenerator.generateKey());
  }
}
```

---

### 6. DelegatingPasswordEncoder
- 여러 암호화 방식을 **유연하게 관리**하는 구현체로, 다양한 `PasswordEncoder`를 통합하여 멀티 알고리즘을 지원합니다.
- 특정 구현체를 사용한 암호화 방식의 ID가 Prefix로 결과에 포함되며, 이를 기반으로 복호화 방식을 자동으로 선택합니다.
- 기존 시스템의 암호화 호환성을 유지해야 하거나 여러 알고리즘을 혼합해서 사용해야 할 때 적합합니다.
 
#### 주요 코드

```java
public final class DelegatingPasswordEncoder implements PasswordEncoder {
    
  // 생략
  public String encode(CharSequence rawPassword) {
    String var10000 = this.idPrefix;
    return var10000 + this.idForEncode + this.idSuffix + this.passwordEncoderForEncode.encode(rawPassword);
  }
}
```

---

## 요약
- `PasswordEncoder`는 Spring Security에서 비밀번호 암호화를 처리하는 인터페이스입니다.
- 운영 환경에서 사용하기에 가장 추천되는 구현체는 **`BCryptPasswordEncoder`**입니다.
- 더 강력한 보안이 요구되거나 CPU/메모리 자원 활용이 중요한 경우 **`SCryptPasswordEncoder`**를 사용할 수 있습니다.
- 여러 알고리즘 간 유연한 전환이나 호환성이 필요한 경우에는 **`DelegatingPasswordEncoder`**를 고려하세요.

---

## 참고 자료
- [Spring Security 공식 문서: PasswordEncoder](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/password-encoder.html)
