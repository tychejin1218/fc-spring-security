# 실전: 요구사항 파악

## 프로젝트 개요
지금까지 배운 내용을 바탕으로 실전 프로젝트를 진행합니다.  
예시 수준에서는 애플리케이션의 개별 기능을 테스트하였다면,  
프로젝트 수준에서는 **클라이언트**, **인증 서버**, **비즈니스 논리 서버**로 분리하여 구현해보겠습니다.

이를 통해 **다단계 인증 방식** (Multi-Factor Authentication, MFA)을 유사하게 구현합니다.
- 2FA (2단계 인증)

---

## 각 구성 요소 별 특징

### 1. 클라이언트
- 백엔드 애플리케이션을 이용하는 **프론트엔드 클라이언트**로, 모바일 앱이거나 ReactJS, VueJS 등의 프론트엔드 프레임워크일 수 있음
- 실제 프로젝트에서는 프론트엔드와의 통합이 중요하지만, 인증 관련이 아니므로 **Postman**으로 호출하여 대체합니다.
- **실무 상황에서 클라이언트 구성 요소는 항상 존재함**을 염두에 둡니다.

---

### 2. 인증 서버
- **사용자를 인증**하는 애플리케이션으로, 사용자 **자격 증명 데이터**를 관리합니다.
- 전달받은 `사용자 ID`와 `PW`를 기반으로 사용자를 인증하여 **OTP (일회용 암호)**를 생성 및 전송합니다.
  > 주의: 이번 프로젝트에서는 SMS를 보내지 않고, **데이터베이스에 저장된 OTP 값을 반환**하여 대체합니다.

---

### 3. 비즈니스 논리 서버
- **클라이언트가 호출할 엔드포인트**를 관리하는 백엔드 애플리케이션입니다.

---

## 클라이언트와 비즈니스 논리 서버의 상호작용

> 비즈니스 논리 서버에서 엔드포인트를 호출하는 흐름:

### 단계 1. **1차 사용자 인증 (ID/PW 기반)**
1. 클라이언트가 `/login` 엔드포인트 호출
  - `사용자 ID`와 `PW`를 전달
2. **성공** 시, **OTP**를 반환받음

---

### 단계 2. **2차 사용자 인증 (OTP 기반)**
1. 클라이언트가 `OTP`와 `사용자 ID`로 `/login` 엔드포인트 호출
2. **성공** 시, **토큰**을 반환받음

---

### 단계 3. **토큰 기반 엔드포인트 호출**
- 비즈니스 논리 서버에서 제공하는 **기타 엔드포인트 호출** 시:
  - **Authorization 헤더**에 `2단계에서 얻은 토큰`을 추가하여 호출

---

## 사용자 식별 과정

### **1. 사용자 ID/PW 기반 인증**
#### 클라이언트 → 비즈니스 논리 서버 → 인증 서버 → 자격 증명 데이터베이스
1. 클라이언트가 `/login` 호출
  - `사용자 ID/PW` 전달
2. 비즈니스 논리 서버가 `/user/auth` 호출
  - `사용자 ID/PW` 전달
3. 인증 서버가 데이터베이스에서 사용자를 **검색 및 인증**
4. 인증 성공 시, **OTP를 전달**

---

### **2. 사용자 ID/OTP 기반 인증**
#### 클라이언트 → 비즈니스 논리 서버 → 인증 서버 → 자격 증명 데이터베이스
1. 클라이언트가 `/login` 호출
  - `사용자 ID/OTP` 전달
2. 비즈니스 논리 서버가 `/otp/check` 호출
  - `사용자 ID/OTP` 전달
3. 인증 서버가 **사용자 ID/OTP 검증**
4. 사용자가 유효한지 확인
5. **유효하다면**, 인증 서버가 **토큰 발급**
6. 이후, 다른 엔드포인트 호출 시 **헤더에 토큰 포함**

---
