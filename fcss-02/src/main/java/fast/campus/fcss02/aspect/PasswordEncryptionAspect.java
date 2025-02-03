package fast.campus.fcss02.aspect;

import fast.campus.fcss02.annotation.CustomEncryption;
import fast.campus.fcss02.service.EncryptService;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * <p>비밀번호 암호화를 처리하는 AOP 클래스</p>
 *
 * <p><b>주요 기능:</b></p>
 * <ol>
 *   <li>암호화가 필요한 필드에 커스텀 어노테이션(@CustomEncryption)을 부여</li>
 *   <li>API 요청 시점을 AOP로 감지하여 반응
 *      <ul>
 *          <li>Spring AOP 라이브러리를 사용</li>
 *      </ul>
 *   </li>
 *   <li>Java Reflection을 통해 어노테이션이 부여된 필드를 조회
 *      <ul>
 *          <li>Apache Commons Lang3 라이브러리의 FieldUtils 클래스 활용</li>
 *      </ul>
 *   </li>
 *   <li>암호화 수행</li>
 * </ol>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PasswordEncryptionAspect {

  private final EncryptService encryptService;

  /**
   * <p>API 요청을 처리하는 메소드를 감지하고 암호화 로직 실행</p>
   *
   * <p><b>동작 방식:</b></p>
   * <ol>
   *   <li>controller 패키지 내의 모든 메소드 실행 시점을 감지</li>
   *   <li>Around 유형으로 Aspect 구현</li>
   * </ol>
   *
   * @param pjp ProceedingJoinPoint (메소드 실행 컨텍스트 정보 제공)
   * @return 실행된 메소드의 결과값
   * @throws Throwable 예외 처리
   */
  @Around("execution(* fast.campus.fcss02.controller..*.*(..))")
  public Object passwordEncryptionAspect(ProceedingJoinPoint pjp) throws Throwable {

    // ProceedingJoinPoint 의 getArgs() 를 통해 요청 객체에 접근
    // Object[] 형태로 되어 있기 때문에 Arrays.stream 을 통해 stream() 으로 변경
    // 각 argument 마다 fieldEncryption (암호화 로직) 메소드를 수행
    Arrays.stream(pjp.getArgs())
        .forEach(this::fieldEncryption);

    return pjp.proceed();
  }

  /**
   * <p>객체 필드를 검사하고 암호화를 수행</p>
   *
   * <p><b>동작 방식:</b></p>
   * <ol>
   *   <li>객체가 null 또는 empty인 경우 로직 종료</li>
   *   <li>Apache Commons Lang3 라이브러리의 FieldUtils를 사용해 필드 접근</li>
   *   <li>다음 조건의 필드는 제외:
   *      <ul>
   *          <li>final 및 static 필드</li>
   *      </ul>
   *   </li>
   *   <li>@CustomEncryption 어노테이션이 부여된 필드만 처리</li>
   *   <li>필드 값이 String인 경우 EncryptService를 통해 암호화 후 업데이트</li>
   * </ol>
   *
   * @param object 처리 대상 객체
   */
  public void fieldEncryption(Object object) {
    if (ObjectUtils.isEmpty(object)) {
      return;
    }

    // getAllFieldsList 를 통해 각 데이터 필드에 접근
    FieldUtils.getAllFieldsList(object.getClass())
        .stream()
        // final 및 static 필드는 제외
        .filter(filter -> !(Modifier.isFinal(filter.getModifiers()) && Modifier.isStatic(
            filter.getModifiers())))
        // 나머지 각 필드에 대해서는 CustomEncryption
        .forEach(field -> {
          try {
            // 어노테이션 존재 여부 확인
            boolean encryptionTarget = field.isAnnotationPresent(CustomEncryption.class);
            if (!encryptionTarget) {
              return;
            }

            // String 형태가 아니라면 제외 (암호는 문자열이기 때문에)
            Object encryptionField = FieldUtils.readField(field, object, true);
            if (!(encryptionField instanceof String)) {
              return;
            }

            // EncryptService 의 encrypt 메소드를 통해 암호화 수행
            String encrypted = encryptService.encrypt((String) encryptionField);

            // 필드의 값을 암호화된 값으로 업데이트
            FieldUtils.writeField(field, object, encrypted);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }
}
