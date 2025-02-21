# 실전: 인증서버 구현

## 1. 인증 서버의 역할
인증 서버는 다음과 같은 역할을 수행합니다:
- **사용자 자격 증명**: ID/PW를 기반으로 클라이언트를 인증
- **OTP 생성 및 저장**: OTP(1회용 패스워드)를 생성해 데이터베이스에 저장하여 인증 과정에 활용

---

## 2. 시스템 흐름

### 주요 구성 요소:
- **클라이언트**
- **비즈니스 로직 서버**
- **인증 서버**
- **자격 증명 데이터베이스**

### 요청 흐름:
1. **클라이언트**가 `/login` 엔드포인트 호출
    - 사용자 ID/PW를 함께 전달
2. **인증 서버**의 `/user/auth` 엔드포인트 호출
    - 데이터베이스에서 사용자 정보를 검색 및 인증
3. 인증 성공 시, OTP를 생성하여 전달

---

## 3. 엔드포인트 설계

다음의 **3개의 엔드포인트**를 구현합니다:

### 1. `POST /api/v1/users`
- **역할**: 사용자 추가
- **설명**: 새로운 사용자를 데이터베이스에 저장하는 엔드포인트

### 2. `POST /api/v1/users/auth`
- **역할**: 사용자 인증
- **설명**: 사용자가 입력한 ID/PW를 데이터베이스와 비교하여 인증
    - 인증 성공 시, 사용자에게 OTP(1회용 패스워드)를 전달

### 3. `POST /api/v1/otp/check`
- **역할**: OTP 확인
- **설명**: 특정 사용자에게 발급된 OTP가 유효한지 확인

---

## 4. 클래스 설계
- 일반적인 **Controller-Service-Repository** 흐름으로 작성:
    1. **Controller**: HTTP 요청을 처리
    2. **Service**: 비즈니스 로직 수행
    3. **Repository**: 데이터베이스에 접근하여 데이터 저장/조회

---

## 5. 데이터베이스와 테이블 설계

### 테이블 생성 목적:
- 사용자 ID/PW를 **저장**
- 사용자별 OTP를 **관리**

### 테이블 설계:
1. **Users 테이블**
    - 사용자 정보를 저장
    - 사용자 ID와 PASSWORD를 포함

2. **Otp 테이블**
    - 사용자별 OTP를 관리
    - `user_id`를 기준으로 **1:1 관계**

### 두 테이블 간의 관계:
- 두 테이블은 `user_id` 기준으로 연결됩니다 (**1:1 관계**).

---

## 6. schema.sql
스프링 애플리케이션이 시작될 때, 테이블이 생성될 수 있도록 설정합니다.

### 테이블 스키마:

#### Users 테이블:
```sql
CREATE TABLE IF NOT EXISTS `sample`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(45) NOT NULL,
  `password` TEXT NOT NULL,
  PRIMARY KEY (`id`)
);
```

#### OTP 테이블:
```sql
CREATE TABLE IF NOT EXISTS `sample`.`otp` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(45) NOT NULL,
  `otp_code` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`)
);
```

---

## 7. application.yml
데이터베이스 관련 설정을 작성합니다:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sample
    username: <DB_USERNAME>
    password: <DB_PASSWORD>
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

---

## 8. docker-compose.yml
로컬 환경에서 도커를 활용하여 MySQL 데이터베이스를 구동합니다.

### 예제:
```yaml
version: '3.8'
services:
  db:
    image: mysql:latest
    container_name: mysql-container
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: <ROOT_PASSWORD>
      MYSQL_DATABASE: sample
```

---

## 9. 비밀번호 관리: `BCryptPasswordEncoder`
- 데이터베이스에 저장되는 비밀번호는 **BCryptPasswordEncoder**를 활용해 **해싱 처리**를 진행합니다.
- **특징**: 간단하게 적용 가능하며 보안성이 일반적으로 높음.
- 주의: 이 프로젝트는 안전한 PasswordEncoder의 학습 목적은 아니므로 본격적인 보안 적용은 제외됩니다.
```java
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
